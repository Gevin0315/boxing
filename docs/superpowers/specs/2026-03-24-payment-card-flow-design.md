# 会员购卡支付流程设计

## 概述

改造会员购卡流程，支持混合支付模式：现金/刷卡即时完成，微信/支付宝先下单后付款。

## 需求背景

- 当前购卡流程假设付款和发卡同步完成
- 需要支持微信/支付宝等异步支付方式
- 支付成功后才发放卡片

## 需求确认

| 项目 | 决策 |
|------|------|
| 支付模式 | 混合：现金/刷卡即时完成，微信/支付宝先下单后付款 |
| 发卡时机 | 支付成功后创建卡片（status=0 未激活） |
| 支付回调 | 先做手动触发接口，后续对接真实回调 |
| 购卡入口 | 会员管理页面 |
| 价格处理 | 支付时再查最新价格，卡片下架则支付失败 |

## 数据模型改动

### finance_order 表

新增字段：

| 字段 | 类型 | 说明 |
|------|------|------|
| `card_id` | BIGINT | 购买的卡片定义ID（引用 `membership_card.id`），待支付订单使用 |

字段语义调整：

| 字段 | 说明 |
|------|------|
| `member_card_id` | 待支付时为 NULL，支付成功后回填 |
| `payment_status` | 0=待支付, 1=已支付, 2=部分支付(暂不使用), 3=已取消 |

### PaymentStatusEnum 枚举

新增或更新枚举：

```java
public enum PaymentStatusEnum {
    PENDING(0, "待支付"),
    PAID(1, "已支付"),
    PARTIAL(2, "部分支付"),  // 暂不使用，保留
    CANCELLED(3, "已取消");
}
```

### member_card 表

无需改动，现有字段已满足需求：

| 字段 | 说明 |
|------|------|
| `order_id` | 支付成功创建卡片时回填 |
| `status` | 0=未激活, 1=生效中, 2=已过期, 3=已作废 |

## 后端接口设计

### 1. 改造 purchaseCard 方法

**路径**: `POST /api/member-card/purchase`

**请求体**: `PurchaseCardDTO`
```java
{
  "memberId": 1,
  "cardId": 1,
  "payMethod": 1,  // 1-微信, 2-支付宝, 3-现金, 4-刷卡
  "paidAmount": 100.00,  // 可选，实际支付金额
  "remark": "备注"
}
```

**业务逻辑**:

```
├─ payMethod ∈ {3(现金), 4(刷卡)} → 即时完成
│   ├─ 创建 member_card (status=0 未激活)
│   ├─ 创建 finance_order (paymentStatus=1, memberCardId=卡片ID)
│   └─ 关联双向ID
│
└─ payMethod ∈ {1(微信), 2(支付宝)} → 待支付
    └─ 创建 finance_order (paymentStatus=0, memberCardId=NULL, cardId=卡片定义ID)
```

**返回值**:
```java
{
  "cardId": 123,      // 即时完成时有值
  "orderId": 456,     // 订单ID
  "status": "completed" | "pending"
}
```

### 2. 新增 confirmPayment 方法

**路径**: `POST /api/member-card/payment/confirm`

**请求体**: `ConfirmPaymentDTO`
```java
{
  "orderId": 456
}
```

**业务逻辑**:
1. 查询订单，验证存在且 `orderCategory=2`（购卡订单）
2. 验证订单状态：
   - `paymentStatus=1` → 抛出 "订单已支付，请勿重复确认"
   - `paymentStatus=3` → 抛出 "订单已取消"
   - `paymentStatus!=0` → 抛出 "订单状态不正确"
3. 查询卡片定义，验证未下架（`status=1`）
4. 使用订单中记录的金额创建卡片（不使用最新价格）
5. 创建 `member_card` (status=0 未激活, purchaseTime=当前时间)
6. 更新订单 (`paymentStatus=1`, `memberCardId=卡片ID`, `paidAmount=amount`)
7. 关联双向ID

**事务要求**: `@Transactional(rollbackFor = Exception.class)`

**返回值**: `PaymentResultVO`
```java
{
  "cardId": 123,
  "status": "completed"
}
```

**异常情况**:
- 订单不存在 → 404 "订单不存在"
- 订单已支付 → 400 "订单已支付，请勿重复确认"
- 订单已取消 → 400 "订单已取消"
- 订单非待支付状态 → 400 "订单状态不正确"
- 非购卡订单 → 400 "非购卡订单"
- 卡片已下架 → 400 "该卡片已停售"

### 3. 新增 cancelOrder 方法

**路径**: `POST /api/member-card/payment/cancel`

**请求体**: `CancelOrderDTO`
```java
{
  "orderId": 456,
  "reason": "客户取消"  // 可选
}
```

**业务逻辑**:
1. 查询订单，验证存在且 `orderCategory=2`（购卡订单）
2. 验证订单状态：
   - `paymentStatus=1` → 抛出 "已支付订单无法取消"
   - `paymentStatus=3` → 抛出 "订单已取消"
   - `paymentStatus!=0` → 抛出 "订单状态不正确"
3. 更新订单 `paymentStatus=3` (已取消)，记录取消原因

**返回值**: `PaymentResultVO`
```java
{
  "status": "cancelled"
}
```

**异常情况**:
- 订单不存在 → 404 "订单不存在"
- 订单已支付 → 400 "已支付订单无法取消"
- 订单已取消 → 400 "订单已取消"
- 非购卡订单 → 400 "非购卡订单"

### 4. 新增 getPendingOrders 方法

**路径**: `GET /api/member-card/payment/pending`

**参数**: `memberId` (Long)

**业务逻辑**:
1. 查询指定会员的待支付订单（`paymentStatus=0`, `orderCategory=2`）
2. 关联查询卡片定义信息

**返回值**: `List<PendingOrderVO>`
```java
[
  {
    "orderId": 456,
    "orderNo": "FO20260324123456",
    "cardId": 1,
    "cardName": "团课次卡20次",
    "amount": 100.00,
    "payMethod": 1,
    "payMethodDesc": "微信",
    "createTime": "2026-03-24 12:34:56"
  }
]
```

### 5. 新增 DTO/VO 定义

```java
// 确认支付请求
public class ConfirmPaymentDTO {
    @NotNull
    private Long orderId;
}

// 取消订单请求
public class CancelOrderDTO {
    @NotNull
    private Long orderId;
    private String reason;
}

// 支付结果响应
public class PaymentResultVO {
    private Long cardId;
    private String status;
}

// 购卡结果响应（扩展）
public class PurchaseCardResultVO {
    private Long cardId;
    private Long orderId;
    private String status;  // "completed" | "pending"
}

// 待支付订单响应
public class PendingOrderVO {
    private Long orderId;
    private String orderNo;
    private Long cardId;
    private String cardName;
    private BigDecimal amount;
    private Integer payMethod;
    private String payMethodDesc;
    private LocalDateTime createTime;
}
```

## 前端设计

### 1. 购卡弹窗改造

**当前**: 选择卡片 → 选择支付方式（现金/刷卡/...）→ 提交

**改造**:
- 支付方式选项保持不变
- 提交后根据返回的 `status` 显示不同提示
  - `completed` → "购卡成功，卡片已发放"
  - `pending` → "订单已创建，等待支付确认"

### 2. API 接口

```typescript
// 购卡返回值改造
interface PurchaseCardResult {
  cardId?: number
  orderId: number
  status: 'completed' | 'pending'
}

// 新增：确认支付
export function confirmPayment(orderId: number): Promise<{ cardId: number; status: string }>

// 新增：取消订单
export function cancelOrder(orderId: number): Promise<{ status: string }>

// 新增：获取待支付订单
export function getPendingOrders(memberId: number): Promise<PendingOrder[]>
```

### 3. 待支付订单入口

在会员卡片展开区域增加"待支付订单"标签页：

- 显示该会员的待支付订单列表
- 每条订单显示：卡片名称、金额、支付方式、创建时间
- 操作按钮：确认支付、取消订单

## 流程图

```
┌─────────────────────────────────────────────────────────────┐
│                      会员购卡流程                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   前台选择会员 + 卡片 + 支付方式                              │
│                 │                                           │
│                 ▼                                           │
│   ┌─────────────────────────┐                               │
│   │  payMethod = 现金/刷卡?  │                               │
│   └───────────┬─────────────┘                               │
│          是   │   否                                        │
│              ▼                                               │
│   ┌─────────────────┐    ┌─────────────────┐                │
│   │ 创建订单(已支付) │    │ 创建订单(待支付) │                │
│   │ 创建卡片(未激活) │    │ cardId=卡片定义ID│                │
│   │ 关联双向ID      │    │ memberCardId=NULL│               │
│   └────────┬────────┘    └────────┬────────┘                │
│            │                      │                          │
│            ▼                      ▼                          │
│      "购卡成功"            ┌──────────────┐                  │
│                            │ 等待支付确认  │                  │
│                            └──────┬───────┘                  │
│                                   │                          │
│                    ┌──────────────┼──────────────┐           │
│                    ▼              ▼              ▼           │
│              "确认支付"      "取消订单"     (真实回调)        │
│                    │              │                          │
│                    ▼              ▼                          │
│           创建卡片(未激活)   订单状态=已取消                  │
│           更新订单已支付                                     │
│                    │                                        │
│                    ▼                                        │
│              "支付成功"                                      │
│                                                            │
└─────────────────────────────────────────────────────────────┘
```

## 实现优先级

1. **P0 - 核心流程**
   - 数据库新增 `card_id` 字段
   - 改造 `purchaseCard` 方法
   - 新增 `confirmPayment` 方法

2. **P1 - 完整功能**
   - 新增 `cancelOrder` 方法
   - 新增 `getPendingOrders` 方法
   - 前端购卡弹窗改造

3. **P2 - 用户体验**
   - 前端待支付订单入口
   - 支付状态提示优化

## 补充说明

### 价格快照策略

- 订单创建时，使用卡片定义的当前价格作为 `amount`
- 支付确认时，使用订单中记录的 `amount` 创建卡片，不查询最新价格
- 如果卡片下架，支付确认失败，需要取消订单后重新下单

### 订单过期处理（P2）

待支付订单建议设置过期时间（如 24 小时），通过定时任务自动取消：

```java
// 在 MemberCardServiceImpl 中新增
@Scheduled(cron = "0 0 * * * ?")  // 每小时执行
public void checkExpiredPendingOrders() {
    // 查询 paymentStatus=0 且 createTime < now - 24h 的订单
    // 更新为 paymentStatus=3 (已取消)
}
```

### 部署注意事项

**API 兼容性**: `purchaseCard` 返回值变更，需确保：
1. 后端先部署（向后兼容，新增字段不影响旧前端）
2. 前端后部署（使用新返回值）

### 数据库迁移脚本

```sql
-- 新增 card_id 字段
ALTER TABLE finance_order ADD COLUMN card_id BIGINT NULL COMMENT '卡片定义ID（待支付订单使用）';

-- 添加索引
ALTER TABLE finance_order ADD INDEX idx_card_id (card_id);
```
