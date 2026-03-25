# 会员购卡支付流程实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 改造会员购卡流程，支持混合支付模式（现金/刷卡即时完成，微信/支付宝先下单后付款）

**Architecture:** 在现有 `MemberCardService` 基础上扩展，新增支付确认和取消订单接口。`finance_order` 表新增 `card_id` 字段用于待支付订单记录卡片定义。

**Tech Stack:** Spring Boot 3.2, MyBatis-Plus, Vue 3, Element Plus, TypeScript

**Spec:** `docs/superpowers/specs/2026-03-24-payment-card-flow-design.md`

---

## 文件结构

### 后端 (boxing-gym-backend)

| 文件 | 操作 | 说明 |
|------|------|------|
| `src/main/resources/sql/init.sql` | 修改 | 新增 `card_id` 字段 |
| `src/main/java/com/boxinggym/entity/FinanceOrder.java` | 修改 | 新增 `cardId` 属性 |
| `src/main/java/com/boxinggym/enums/PaymentStatusEnum.java` | 新增 | 支付状态枚举 |
| `src/main/java/com/boxinggym/dto/ConfirmPaymentDTO.java` | 新增 | 确认支付请求 |
| `src/main/java/com/boxinggym/dto/CancelOrderDTO.java` | 新增 | 取消订单请求 |
| `src/main/java/com/boxinggym/vo/PurchaseCardResultVO.java` | 新增 | 购卡结果响应 |
| `src/main/java/com/boxinggym/vo/PaymentResultVO.java` | 新增 | 支付结果响应 |
| `src/main/java/com/boxinggym/vo/PendingOrderVO.java` | 新增 | 待支付订单响应 |
| `src/main/java/com/boxinggym/service/MemberCardService.java` | 修改 | 新增接口方法 |
| `src/main/java/com/boxinggym/service/impl/MemberCardServiceImpl.java` | 修改 | 实现核心逻辑 |
| `src/main/java/com/boxinggym/controller/MemberCardController.java` | 修改 | 新增 REST 接口 |

### 前端 (boxing-gym-frontend)

| 文件 | 操作 | 说明 |
|------|------|------|
| `src/types/memberCard.ts` | 修改 | 新增类型定义 |
| `src/api/memberCard.ts` | 修改 | 新增 API 方法 |
| `src/views/member/index.vue` | 修改 | 购卡弹窗和待支付订单 |

---

## Task 1: 数据库和实体层

**Files:**
- Modify: `boxing-gym-backend/src/main/resources/sql/init.sql`
- Modify: `boxing-gym-backend/src/main/java/com/boxinggym/entity/FinanceOrder.java`
- Create: `boxing-gym-backend/src/main/java/com/boxinggym/enums/PaymentStatusEnum.java`

- [ ] **Step 1: 更新 init.sql 添加 card_id 字段**

在 `finance_order` 表定义中添加字段：

```sql
-- 在 finance_order 表中添加（在 member_card_id 字段附近）
`card_id` bigint DEFAULT NULL COMMENT '卡片定义ID（待支付订单使用）',
```

并添加索引：

```sql
-- 在索引区域添加
INDEX `idx_card_id` (`card_id`),
```

- [ ] **Step 2: 更新 FinanceOrder 实体**

在 `FinanceOrder.java` 中添加字段：

```java
/**
 * 关联卡片定义ID（待支付订单使用）
 */
private Long cardId;
```

位置：在 `memberCardId` 字段附近添加。

- [ ] **Step 3: 创建 PaymentStatusEnum 枚举**

创建文件 `src/main/java/com/boxinggym/enums/PaymentStatusEnum.java`：

```java
package com.boxinggym.enums;

import lombok.Getter;

/**
 * 支付状态枚举
 */
@Getter
public enum PaymentStatusEnum {

    PENDING(0, "待支付"),
    PAID(1, "已支付"),
    PARTIAL(2, "部分支付"),
    CANCELLED(3, "已取消");

    private final Integer code;
    private final String description;

    PaymentStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static PaymentStatusEnum fromCode(Integer code) {
        for (PaymentStatusEnum status : PaymentStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的支付状态代码: " + code);
    }
}
```

- [ ] **Step 4: 执行数据库迁移**

运行 SQL 更新现有数据库：

```bash
mysql -u root -p boxing_gym -e "ALTER TABLE finance_order ADD COLUMN card_id BIGINT NULL COMMENT '卡片定义ID（待支付订单使用）'; ALTER TABLE finance_order ADD INDEX idx_card_id (card_id);"
```

- [ ] **Step 5: 编译验证**

```bash
cd boxing-gym-backend && mvn -DskipTests compile
```

Expected: BUILD SUCCESS

- [ ] **Step 6: Commit**

```bash
git add boxing-gym-backend/src/main/resources/sql/init.sql \
        boxing-gym-backend/src/main/java/com/boxinggym/entity/FinanceOrder.java \
        boxing-gym-backend/src/main/java/com/boxinggym/enums/PaymentStatusEnum.java
git commit -m "$(cat <<'EOF'
feat(db): add card_id field to finance_order for pending orders

- Add card_id column to finance_order table
- Add PaymentStatusEnum for payment status management
- Support async payment flow for WeChat/Alipay

Co-Authored-By: Claude Opus 4.5 <noreply@anthropic.com>
EOF
)"
```

---

## Task 2: DTO 和 VO 层

**Files:**
- Create: `boxing-gym-backend/src/main/java/com/boxinggym/dto/ConfirmPaymentDTO.java`
- Create: `boxing-gym-backend/src/main/java/com/boxinggym/dto/CancelOrderDTO.java`
- Create: `boxing-gym-backend/src/main/java/com/boxinggym/vo/PurchaseCardResultVO.java`
- Create: `boxing-gym-backend/src/main/java/com/boxinggym/vo/PaymentResultVO.java`
- Create: `boxing-gym-backend/src/main/java/com/boxinggym/vo/PendingOrderVO.java`

- [ ] **Step 1: 创建 ConfirmPaymentDTO**

```java
package com.boxinggym.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 确认支付请求DTO
 */
@Data
@Schema(description = "确认支付请求DTO")
public class ConfirmPaymentDTO {

    @Schema(description = "订单ID")
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
}
```

- [ ] **Step 2: 创建 CancelOrderDTO**

```java
package com.boxinggym.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 取消订单请求DTO
 */
@Data
@Schema(description = "取消订单请求DTO")
public class CancelOrderDTO {

    @Schema(description = "订单ID")
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @Schema(description = "取消原因")
    private String reason;
}
```

- [ ] **Step 3: 创建 PurchaseCardResultVO**

```java
package com.boxinggym.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 购卡结果响应VO
 */
@Data
@Schema(description = "购卡结果响应VO")
public class PurchaseCardResultVO {

    @Schema(description = "会员卡ID（即时完成时有值）")
    private Long cardId;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "状态：completed-已完成, pending-待支付")
    private String status;
}
```

- [ ] **Step 4: 创建 PaymentResultVO**

```java
package com.boxinggym.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 支付结果响应VO
 */
@Data
@Schema(description = "支付结果响应VO")
public class PaymentResultVO {

    @Schema(description = "会员卡ID")
    private Long cardId;

    @Schema(description = "状态：completed-已完成, cancelled-已取消")
    private String status;
}
```

- [ ] **Step 5: 创建 PendingOrderVO**

```java
package com.boxinggym.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 待支付订单响应VO
 */
@Data
@Schema(description = "待支付订单响应VO")
public class PendingOrderVO {

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "卡片定义ID")
    private Long cardId;

    @Schema(description = "卡片名称")
    private String cardName;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "支付方式")
    private Integer payMethod;

    @Schema(description = "支付方式描述")
    private String payMethodDesc;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
```

- [ ] **Step 6: 编译验证**

```bash
cd boxing-gym-backend && mvn -DskipTests compile
```

Expected: BUILD SUCCESS

- [ ] **Step 7: Commit**

```bash
git add boxing-gym-backend/src/main/java/com/boxinggym/dto/ConfirmPaymentDTO.java \
        boxing-gym-backend/src/main/java/com/boxinggym/dto/CancelOrderDTO.java \
        boxing-gym-backend/src/main/java/com/boxinggym/vo/PurchaseCardResultVO.java \
        boxing-gym-backend/src/main/java/com/boxinggym/vo/PaymentResultVO.java \
        boxing-gym-backend/src/main/java/com/boxinggym/vo/PendingOrderVO.java
git commit -m "$(cat <<'EOF'
feat(dto): add DTOs and VOs for payment flow

- Add ConfirmPaymentDTO and CancelOrderDTO for request
- Add PurchaseCardResultVO, PaymentResultVO, PendingOrderVO for response

Co-Authored-By: Claude Opus 4.5 <noreply@anthropic.com>
EOF
)"
```

---

## Task 3: Service 接口层

**Files:**
- Modify: `boxing-gym-backend/src/main/java/com/boxinggym/service/MemberCardService.java`

- [ ] **Step 1: 更新 purchaseCard 返回值**

将 `Long purchaseCard(PurchaseCardDTO dto)` 改为 `PurchaseCardResultVO purchaseCard(PurchaseCardDTO dto)`

并添加新方法声明：

```java
/**
 * 确认支付
 *
 * @param dto 确认支付请求
 * @return 支付结果
 */
PaymentResultVO confirmPayment(ConfirmPaymentDTO dto);

/**
 * 取消订单
 *
 * @param dto 取消订单请求
 * @return 取消结果
 */
PaymentResultVO cancelOrder(CancelOrderDTO dto);

/**
 * 获取会员待支付订单
 *
 * @param memberId 会员ID
 * @return 待支付订单列表
 */
List<PendingOrderVO> getPendingOrders(Long memberId);
```

- [ ] **Step 2: 编译验证**

```bash
cd boxing-gym-backend && mvn -DskipTests compile
```

Expected: 编译失败（实现类未更新），确认接口定义正确

- [ ] **Step 3: Commit**

```bash
git add boxing-gym-backend/src/main/java/com/boxinggym/service/MemberCardService.java
git commit -m "$(cat <<'EOF'
feat(service): add payment flow methods to MemberCardService

- Change purchaseCard return type to PurchaseCardResultVO
- Add confirmPayment, cancelOrder, getPendingOrders methods

Co-Authored-By: Claude Opus 4.5 <noreply@anthropic.com>
EOF
)"
```

---

## Task 4: Service 实现层 - 核心逻辑

**Files:**
- Modify: `boxing-gym-backend/src/main/java/com/boxinggym/service/impl/MemberCardServiceImpl.java`

- [ ] **Step 1: 改造 purchaseCard 方法**

替换整个 `purchaseCard` 方法，实现混合支付逻辑：

```java
@Override
@Transactional(rollbackFor = Exception.class)
public PurchaseCardResultVO purchaseCard(PurchaseCardDTO dto) {
    // 获取会员
    Member member = memberMapper.selectById(dto.getMemberId());
    if (member == null) {
        throw new BusinessException("会员不存在");
    }
    // 获取卡片定义
    MembershipCard cardDef = membershipCardService.getById(dto.getCardId());
    if (cardDef == null) {
        throw new BusinessException("卡片定义不存在");
    }
    if (cardDef.getStatus() != 1) {
        throw new BusinessException("该卡片已停售");
    }

    BigDecimal paidAmount = dto.getPaidAmount() != null ? dto.getPaidAmount() : cardDef.getPrice();

    PurchaseCardResultVO result = new PurchaseCardResultVO();
    result.setOrderId(null);

    // 判断支付方式：现金/刷卡即时完成，微信/支付宝待支付
    boolean immediatePayment = dto.getPayMethod() == 3 || dto.getPayMethod() == 4;

    if (immediatePayment) {
        // 即时完成：创建卡片和订单
        MemberCard memberCard = createMemberCard(dto.getMemberId(), cardDef, dto.getRemark());
        FinanceOrder order = createFinanceOrder(dto.getMemberId(), paidAmount, dto.getPayMethod(),
                cardDef.getCardName(), memberCard.getId(), dto.getCardId());
        memberCard.setOrderId(order.getId());
        updateById(memberCard);

        result.setCardId(memberCard.getId());
        result.setOrderId(order.getId());
        result.setStatus("completed");
    } else {
        // 待支付：只创建订单
        FinanceOrder order = createPendingOrder(dto.getMemberId(), paidAmount, dto.getPayMethod(),
                cardDef.getCardName(), dto.getCardId());

        result.setCardId(null);
        result.setOrderId(order.getId());
        result.setStatus("pending");
    }

    return result;
}

/**
 * 创建会员卡
 */
private MemberCard createMemberCard(Long memberId, MembershipCard cardDef, String remark) {
    MemberCard memberCard = new MemberCard();
    memberCard.setMemberId(memberId);
    memberCard.setCardId(cardDef.getId());
    memberCard.setCardCategory(cardDef.getCardCategory());
    memberCard.setCardType(cardDef.getCardType());
    memberCard.setStatus(CardStatusEnum.INACTIVE.getCode());
    if (cardDef.getSessionCount() != null) {
        memberCard.setTotalSessions(cardDef.getSessionCount());
        memberCard.setRemainingSessions(cardDef.getSessionCount());
    }
    memberCard.setPurchaseTime(LocalDateTime.now());
    int activationDays = cardDef.getActivationDeadlineDays() != null ? cardDef.getActivationDeadlineDays() : 30;
    memberCard.setActivationDeadline(LocalDate.now().plusDays(activationDays));
    memberCard.setRemark(remark);
    memberCard.setCreateTime(LocalDateTime.now());
    memberCard.setUpdateTime(LocalDateTime.now());
    save(memberCard);
    return memberCard;
}

/**
 * 创建已支付订单（即时完成）
 */
private FinanceOrder createFinanceOrder(Long memberId, BigDecimal amount, Integer payMethod,
        String cardName, Long memberCardId, Long cardDefId) {
    FinanceOrder order = new FinanceOrder();
    order.setOrderNo("FO" + LocalDateTime.now().format(CARD_NO_FORMATTER));
    order.setMemberId(memberId);
    order.setAmount(amount);
    order.setType(OrderTypeEnum.RECHARGE.getCode());
    order.setPayMethod(payMethod);
    order.setRemark("购买" + cardName);
    order.setPaidAmount(amount);
    order.setPaymentStatus(PaymentStatusEnum.PAID.getCode());
    order.setMemberCardId(memberCardId);
    order.setCardId(cardDefId);
    order.setOrderCategory(OrderCategoryEnum.PURCHASE_CARD.getCode());
    order.setCreateTime(LocalDateTime.now());
    order.setUpdateTime(LocalDateTime.now());
    try {
        order.setCreateBy(SecurityUtil.getCurrentUserId());
    } catch (Exception e) {
        log.warn("获取当前用户ID失败", e);
    }
    financeOrderMapper.insert(order);
    return order;
}

/**
 * 创建待支付订单
 */
private FinanceOrder createPendingOrder(Long memberId, BigDecimal amount, Integer payMethod,
        String cardName, Long cardDefId) {
    FinanceOrder order = new FinanceOrder();
    order.setOrderNo("FO" + LocalDateTime.now().format(CARD_NO_FORMATTER));
    order.setMemberId(memberId);
    order.setAmount(amount);
    order.setType(OrderTypeEnum.RECHARGE.getCode());
    order.setPayMethod(payMethod);
    order.setRemark("购买" + cardName);
    order.setPaidAmount(BigDecimal.ZERO);
    order.setPaymentStatus(PaymentStatusEnum.PENDING.getCode());
    order.setMemberCardId(null);
    order.setCardId(cardDefId);
    order.setOrderCategory(OrderCategoryEnum.PURCHASE_CARD.getCode());
    order.setCreateTime(LocalDateTime.now());
    order.setUpdateTime(LocalDateTime.now());
    try {
        order.setCreateBy(SecurityUtil.getCurrentUserId());
    } catch (Exception e) {
        log.warn("获取当前用户ID失败", e);
    }
    financeOrderMapper.insert(order);
    return order;
}
```

- [ ] **Step 2: 实现 confirmPayment 方法**

在 `MemberCardServiceImpl` 中添加：

```java
@Override
@Transactional(rollbackFor = Exception.class)
public PaymentResultVO confirmPayment(ConfirmPaymentDTO dto) {
    FinanceOrder order = financeOrderMapper.selectById(dto.getOrderId());
    if (order == null) {
        throw new BusinessException("订单不存在");
    }

    // 验证订单类型
    if (!OrderCategoryEnum.PURCHASE_CARD.getCode().equals(order.getOrderCategory())) {
        throw new BusinessException("非购卡订单");
    }

    // 验证订单状态（幂等性处理）
    if (PaymentStatusEnum.PAID.getCode().equals(order.getPaymentStatus())) {
        throw new BusinessException("订单已支付，请勿重复确认");
    }
    if (PaymentStatusEnum.CANCELLED.getCode().equals(order.getPaymentStatus())) {
        throw new BusinessException("订单已取消");
    }
    if (!PaymentStatusEnum.PENDING.getCode().equals(order.getPaymentStatus())) {
        throw new BusinessException("订单状态不正确");
    }

    // 验证卡片定义
    MembershipCard cardDef = membershipCardService.getById(order.getCardId());
    if (cardDef == null) {
        throw new BusinessException("卡片定义不存在");
    }
    if (cardDef.getStatus() != 1) {
        throw new BusinessException("该卡片已停售");
    }

    // 创建会员卡
    MemberCard memberCard = createMemberCard(order.getMemberId(), cardDef, "支付确认后创建");

    // 更新订单
    order.setMemberCardId(memberCard.getId());
    order.setPaymentStatus(PaymentStatusEnum.PAID.getCode());
    order.setPaidAmount(order.getAmount());
    order.setUpdateTime(LocalDateTime.now());
    financeOrderMapper.updateById(order);

    // 关联双向ID
    memberCard.setOrderId(order.getId());
    updateById(memberCard);

    PaymentResultVO result = new PaymentResultVO();
    result.setCardId(memberCard.getId());
    result.setStatus("completed");
    return result;
}
```

- [ ] **Step 3: 实现 cancelOrder 方法**

在 `MemberCardServiceImpl` 中添加：

```java
@Override
@Transactional(rollbackFor = Exception.class)
public PaymentResultVO cancelOrder(CancelOrderDTO dto) {
    FinanceOrder order = financeOrderMapper.selectById(dto.getOrderId());
    if (order == null) {
        throw new BusinessException("订单不存在");
    }

    // 验证订单类型
    if (!OrderCategoryEnum.PURCHASE_CARD.getCode().equals(order.getOrderCategory())) {
        throw new BusinessException("非购卡订单");
    }

    // 验证订单状态
    if (PaymentStatusEnum.PAID.getCode().equals(order.getPaymentStatus())) {
        throw new BusinessException("已支付订单无法取消");
    }
    if (PaymentStatusEnum.CANCELLED.getCode().equals(order.getPaymentStatus())) {
        throw new BusinessException("订单已取消");
    }
    if (!PaymentStatusEnum.PENDING.getCode().equals(order.getPaymentStatus())) {
        throw new BusinessException("订单状态不正确");
    }

    // 更新订单状态
    order.setPaymentStatus(PaymentStatusEnum.CANCELLED.getCode());
    if (dto.getReason() != null) {
        order.setRemark(order.getRemark() + " | 取消原因: " + dto.getReason());
    }
    order.setUpdateTime(LocalDateTime.now());
    financeOrderMapper.updateById(order);

    PaymentResultVO result = new PaymentResultVO();
    result.setStatus("cancelled");
    return result;
}
```

- [ ] **Step 4: 实现 getPendingOrders 方法**

在 `MemberCardServiceImpl` 中添加：

```java
@Override
public List<PendingOrderVO> getPendingOrders(Long memberId) {
    LambdaQueryWrapper<FinanceOrder> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(FinanceOrder::getMemberId, memberId)
            .eq(FinanceOrder::getPaymentStatus, PaymentStatusEnum.PENDING.getCode())
            .eq(FinanceOrder::getOrderCategory, OrderCategoryEnum.PURCHASE_CARD.getCode())
            .orderByDesc(FinanceOrder::getCreateTime);
    List<FinanceOrder> orders = financeOrderMapper.selectList(wrapper);

    return orders.stream().map(order -> {
        PendingOrderVO vo = new PendingOrderVO();
        vo.setOrderId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setCardId(order.getCardId());
        vo.setAmount(order.getAmount());
        vo.setPayMethod(order.getPayMethod());
        vo.setCreateTime(order.getCreateTime());

        // 获取卡片名称
        MembershipCard cardDef = membershipCardService.getById(order.getCardId());
        if (cardDef != null) {
            vo.setCardName(cardDef.getCardName());
        }

        // 支付方式描述
        try {
            PayMethodEnum payMethod = PayMethodEnum.fromCode(order.getPayMethod());
            vo.setPayMethodDesc(payMethod.getDescription());
        } catch (IllegalArgumentException e) {
            vo.setPayMethodDesc("未知");
        }

        return vo;
    }).collect(Collectors.toList());
}
```

- [ ] **Step 5: 编译验证**

```bash
cd boxing-gym-backend && mvn -DskipTests compile
```

Expected: BUILD SUCCESS

- [ ] **Step 6: Commit**

```bash
git add boxing-gym-backend/src/main/java/com/boxinggym/service/impl/MemberCardServiceImpl.java
git commit -m "$(cat <<'EOF'
feat(service): implement hybrid payment flow for card purchase

- Refactor purchaseCard to support immediate and pending payment
- Add confirmPayment for manual payment confirmation
- Add cancelOrder for order cancellation
- Add getPendingOrders to list pending orders by member

Co-Authored-By: Claude Opus 4.5 <noreply@anthropic.com>
EOF
)"
```

---

## Task 5: Controller 层

**Files:**
- Modify: `boxing-gym-backend/src/main/java/com/boxinggym/controller/MemberCardController.java`

- [ ] **Step 1: 更新 purchaseCard 接口返回值**

找到 `purchaseCard` 方法，修改返回值类型和调用：

```java
/**
 * 购买会员卡
 */
@Operation(summary = "购买会员卡")
@PostMapping("/purchase")
public Result<PurchaseCardResultVO> purchaseCard(@Valid @RequestBody PurchaseCardDTO dto) {
    PurchaseCardResultVO result = memberCardService.purchaseCard(dto);
    return Result.success(result);
}
```

- [ ] **Step 2: 新增 confirmPayment 接口**

```java
/**
 * 确认支付
 */
@Operation(summary = "确认支付")
@PostMapping("/payment/confirm")
public Result<PaymentResultVO> confirmPayment(@Valid @RequestBody ConfirmPaymentDTO dto) {
    PaymentResultVO result = memberCardService.confirmPayment(dto);
    return Result.success(result);
}
```

- [ ] **Step 3: 新增 cancelOrder 接口**

```java
/**
 * 取消订单
 */
@Operation(summary = "取消订单")
@PostMapping("/payment/cancel")
public Result<PaymentResultVO> cancelOrder(@Valid @RequestBody CancelOrderDTO dto) {
    PaymentResultVO result = memberCardService.cancelOrder(dto);
    return Result.success(result);
}
```

- [ ] **Step 4: 新增 getPendingOrders 接口**

```java
/**
 * 获取会员待支付订单
 */
@Operation(summary = "获取会员待支付订单")
@GetMapping("/payment/pending")
public Result<List<PendingOrderVO>> getPendingOrders(@RequestParam Long memberId) {
    List<PendingOrderVO> orders = memberCardService.getPendingOrders(memberId);
    return Result.success(orders);
}
```

- [ ] **Step 5: 添加必要的 import**

确保 Controller 顶部有：

```java
import com.boxinggym.dto.ConfirmPaymentDTO;
import com.boxinggym.dto.CancelOrderDTO;
import com.boxinggym.vo.PurchaseCardResultVO;
import com.boxinggym.vo.PaymentResultVO;
import com.boxinggym.vo.PendingOrderVO;
```

- [ ] **Step 6: 编译验证**

```bash
cd boxing-gym-backend && mvn -DskipTests compile
```

Expected: BUILD SUCCESS

- [ ] **Step 7: 启动后端测试**

```bash
cd boxing-gym-backend && mvn spring-boot:run
```

Expected: 服务启动成功，访问 http://localhost:8080/doc.html 可看到新接口

- [ ] **Step 8: Commit**

```bash
git add boxing-gym-backend/src/main/java/com/boxinggym/controller/MemberCardController.java
git commit -m "$(cat <<'EOF'
feat(controller): add REST endpoints for payment flow

- Update purchaseCard to return PurchaseCardResultVO
- Add confirmPayment, cancelOrder, getPendingOrders endpoints

Co-Authored-By: Claude Opus 4.5 <noreply@anthropic.com>
EOF
)"
```

---

## Task 6: 前端类型定义和 API

**Files:**
- Modify: `boxing-gym-frontend/src/types/memberCard.ts`
- Modify: `boxing-gym-frontend/src/api/memberCard.ts`

- [ ] **Step 1: 添加类型定义到 memberCard.ts**

在文件末尾添加：

```typescript
/** 购卡结果 */
export interface PurchaseCardResult {
  cardId?: number
  orderId: number
  status: 'completed' | 'pending'
}

/** 支付结果 */
export interface PaymentResult {
  cardId?: number
  status: string
}

/** 待支付订单 */
export interface PendingOrder {
  orderId: number
  orderNo: string
  cardId: number
  cardName: string
  amount: number
  payMethod: number
  payMethodDesc: string
  createTime: string
}
```

- [ ] **Step 2: 添加 API 方法到 memberCard.ts**

在文件末尾添加：

```typescript
/** 确认支付 */
export function confirmPayment(orderId: number): Promise<PaymentResult> {
  return request.post('/member-card/payment/confirm', { orderId })
}

/** 取消订单 */
export function cancelOrder(orderId: number, reason?: string): Promise<PaymentResult> {
  return request.post('/member-card/payment/cancel', { orderId, reason })
}

/** 获取会员待支付订单 */
export function getPendingOrders(memberId: number): Promise<PendingOrder[]> {
  return request.get('/member-card/payment/pending', { params: { memberId } })
}
```

并确保添加必要的 import：

```typescript
import type { PurchaseCardResult, PaymentResult, PendingOrder } from '@/types/memberCard'
```

- [ ] **Step 3: 更新 purchaseCard 返回类型**

找到 `purchaseCard` 函数，更新返回类型：

```typescript
/** 购买会员卡 */
export function purchaseCard(data: PurchaseCardDTO): Promise<PurchaseCardResult> {
  return request.post('/member-card/purchase', data)
}
```

- [ ] **Step 4: TypeScript 检查**

```bash
cd boxing-gym-frontend && npm run build
```

Expected: 构建成功

- [ ] **Step 5: Commit**

```bash
git add boxing-gym-frontend/src/types/memberCard.ts \
        boxing-gym-frontend/src/api/memberCard.ts
git commit -m "$(cat <<'EOF'
feat(frontend): add types and APIs for payment flow

- Add PurchaseCardResult, PaymentResult, PendingOrder types
- Add confirmPayment, cancelOrder, getPendingOrders APIs
- Update purchaseCard return type

Co-Authored-By: Claude Opus 4.5 <noreply@anthropic.com>
EOF
)"
```

---

## Task 7: 前端购卡弹窗改造

**Files:**
- Modify: `boxing-gym-frontend/src/views/member/index.vue`

- [ ] **Step 1: 更新购卡成功的提示逻辑**

找到购卡成功后的处理逻辑，根据返回的 `status` 显示不同提示。

在 `handlePurchaseCardSubmit` 或类似方法中：

```typescript
const handlePurchaseCardSubmit = async () => {
  // ... 验证逻辑 ...

  try {
    const result = await purchaseCard(purchaseForm)

    if (result.status === 'completed') {
      ElMessage.success('购卡成功，卡片已发放')
    } else {
      ElMessage.success('订单已创建，等待支付确认')
    }

    purchaseVisible.value = false
    // 刷新会员卡片列表
    if (currentMemberId.value) {
      memberCardsMap.value.delete(currentMemberId.value)
      await loadMemberCards(currentMemberId.value)
    }
  } catch (error) {
    console.error('Failed to purchase card:', error)
  }
}
```

- [ ] **Step 2: 添加待支付订单状态**

在 `<script setup>` 中添加（在 `const selectedCard` 附近）：

```typescript
// 当前展开的会员ID
const currentMemberId = ref<number>()

// 待支付订单
const pendingOrders = ref<PendingOrder[]>([])
const pendingOrdersLoading = ref(false)

/** 加载待支付订单 */
const loadPendingOrders = async (memberId: number) => {
  pendingOrdersLoading.value = true
  try {
    pendingOrders.value = await getPendingOrders(memberId)
  } catch (error) {
    console.error('Failed to load pending orders:', error)
    pendingOrders.value = []
  } finally {
    pendingOrdersLoading.value = false
  }
}

/** 确认支付 */
const handleConfirmPayment = async (order: PendingOrder) => {
  try {
    await ElMessageBox.confirm(
      `确定要确认支付订单 "${order.orderNo}" 吗？\n卡片：${order.cardName}\n金额：${order.amount}`,
      '确认支付',
      { type: 'info' }
    )
    const result = await confirmPayment(order.orderId)
    ElMessage.success(`支付成功，卡片已发放`)
    // 刷新列表
    await loadPendingOrders(currentMemberId.value!)
    await loadMemberCards(currentMemberId.value!)
  } catch (error: unknown) {
    if (error !== 'cancel') {
      console.error('Failed to confirm payment:', error)
    }
  }
}

/** 取消订单 */
const handleCancelOrder = async (order: PendingOrder) => {
  try {
    const { value } = await ElMessageBox.prompt(
      `确定要取消订单 "${order.orderNo}" 吗？`,
      '取消订单',
      {
        confirmButtonText: '确定取消',
        cancelButtonText: '返回',
        inputPlaceholder: '请输入取消原因（可选）',
        type: 'warning'
      }
    )
    await cancelOrder(order.orderId, value || undefined)
    ElMessage.success('订单已取消')
    await loadPendingOrders(currentMemberId.value!)
  } catch (error: unknown) {
    if (error !== 'cancel') {
      console.error('Failed to cancel order:', error)
    }
  }
}
```

- [ ] **Step 3: 在展开行加载时同时加载待支付订单**

修改 `handleExpandChange` 或 `loadMemberCards` 方法：

```typescript
/** 处理行展开 */
const handleExpandChange = async (row: Member, expandedRows: Member[]) => {
  expandedRowKeys.value = expandedRows.map(r => r.id!)

  if (expandedRows.some(r => r.id === row.id)) {
    currentMemberId.value = row.id
    if (!memberCardsMap.value.has(row.id!)) {
      await loadMemberCards(row.id!)
    }
    // 同时加载待支付订单
    await loadPendingOrders(row.id!)
  }
}
```

添加 `currentMemberId` 变量：

```typescript
const currentMemberId = ref<number>()
```

- [ ] **Step 4: 添加待支付订单模板**

在展开内容的卡片表格下方添加待支付订单区域：

```vue
<!-- 待支付订单 -->
<div v-if="pendingOrders.length > 0" class="pending-orders-wrapper">
  <div class="pending-header">
    <el-icon><Clock /></el-icon>
    <span>待支付订单 ({{ pendingOrders.length }}笔)</span>
  </div>
  <el-table :data="pendingOrders" size="small" border v-loading="pendingOrdersLoading">
    <el-table-column prop="orderNo" label="订单号" width="180" />
    <el-table-column prop="cardName" label="卡片" min-width="120" />
    <el-table-column prop="amount" label="金额" width="100">
      <template #default="{ row }">
        {{ formatCurrency(row.amount) }}
      </template>
    </el-table-column>
    <el-table-column prop="payMethodDesc" label="支付方式" width="100" />
    <el-table-column prop="createTime" label="创建时间" width="160" />
    <el-table-column label="操作" width="160" fixed="right">
      <template #default="{ row }">
        <el-button type="success" link size="small" @click="handleConfirmPayment(row)">
          确认支付
        </el-button>
        <el-button type="danger" link size="small" @click="handleCancelOrder(row)">
          取消
        </el-button>
      </template>
    </el-table-column>
  </el-table>
</div>
```

- [ ] **Step 5: 添加样式**

在 `<style scoped>` 中添加：

```css
.pending-orders-wrapper {
  background: #fffaf0;
  border-radius: 4px;
  padding: 12px;
  margin-top: 12px;
}

.pending-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 12px;
  font-size: 14px;
  font-weight: 500;
  color: #e6a23c;
}
```

- [ ] **Step 6: 添加 Clock 图标导入**

在 import 语句中添加 `Clock`：

```typescript
import { Plus, Search, Refresh, Edit, Delete, Coin, Wallet, CreditCard, View, Clock } from '@element-plus/icons-vue'
```

- [ ] **Step 7: 添加必要的类型导入**

```typescript
import type { PendingOrder, PurchaseCardResult } from '@/types/memberCard'
```

- [ ] **Step 8: 测试前端**

```bash
cd boxing-gym-frontend && npm run dev
```

手动测试：
1. 打开会员列表
2. 展开一个会员
3. 点击"购卡"，选择微信/支付宝支付
4. 确认显示"订单已创建，等待支付确认"
5. 待支付订单区域显示新订单
6. 点击"确认支付"，卡片发放成功

- [ ] **Step 9: Commit**

```bash
git add boxing-gym-frontend/src/views/member/index.vue
git commit -m "$(cat <<'EOF'
feat(frontend): implement pending orders UI in member list

- Show different messages for completed/pending purchase
- Add pending orders section in expanded row
- Add confirm payment and cancel order actions

Co-Authored-By: Claude Opus 4.5 <noreply@anthropic.com>
EOF
)"
```

---

## Task 8: 集成测试和最终提交

- [ ] **Step 1: 启动后端服务**

```bash
cd boxing-gym-backend && mvn spring-boot:run
```

- [ ] **Step 2: 启动前端服务**

```bash
cd boxing-gym-frontend && npm run dev
```

- [ ] **Step 3: 完整流程测试**

测试场景：

| 场景 | 预期结果 |
|------|----------|
| 现金购卡 | 即时完成，卡片创建，状态未激活 |
| 微信购卡 | 订单创建，状态待支付，无卡片 |
| 确认支付 | 卡片创建，订单状态变为已支付 |
| 取消订单 | 订单状态变为已取消 |
| 重复确认支付 | 提示"订单已支付，请勿重复确认" |
| 卡片下架后确认支付 | 提示"该卡片已停售" |

- [ ] **Step 4: 最终 Commit（如有遗漏）**

```bash
git status
# 如有未提交的文件，逐一提交
```

- [ ] **Step 5: 推送到远程**

```bash
git push origin main
```

---

## 实现总结

| 优先级 | 任务 | 状态 |
|--------|------|------|
| P0 | 数据库 card_id 字段 | [ ] |
| P0 | purchaseCard 混合支付 | [ ] |
| P0 | confirmPayment 接口 | [ ] |
| P1 | cancelOrder 接口 | [ ] |
| P1 | getPendingOrders 接口 | [ ] |
| P1 | 前端购卡弹窗 | [ ] |
| P2 | 前端待支付订单入口 | [ ] |
