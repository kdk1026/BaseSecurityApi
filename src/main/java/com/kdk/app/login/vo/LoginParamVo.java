package com.kdk.app.login.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
@ToString(exclude = "userPw")
public class LoginParamVo {

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "아이디")
	@NotBlank(message = "아이디는 필수 항목입니다.")
	@Pattern(regexp = "^[a-z0-9]+$", message = "아이디는 영문 소문자와 숫자만 포함해야 합니다.")
	private String userId;

	@Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "비밀번호")
	@NotBlank(message = "비밀번호는 필수 항목입니다.")
	private String userPw;

}