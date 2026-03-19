package com.boxinggym.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码响应 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "验证码响应")
public class CaptchaVO {

    @Schema(description = "验证码唯一标识", example = "uuid-xxx-xxx")
    private String uuid;

    @Schema(description = "Base64编码的验证码图片", example = "data:image/png;base64,...")
    private String img;
}
