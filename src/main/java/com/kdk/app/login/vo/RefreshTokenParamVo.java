package com.kdk.app.login.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 1. 28. kdk	최초작성
 * </pre>
 *
 *
 * @author kdk
 */
@Getter
@Setter
@ToString
public class RefreshTokenParamVo {

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "접근 토큰")
	@NotBlank(message = "접근 토큰은 필수 항목입니다.")
	private String accessToken;

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "갱신 토큰")
	@NotBlank(message = "갱신 토큰은 필수 항목입니다.")
	private String refreshToken;

}
