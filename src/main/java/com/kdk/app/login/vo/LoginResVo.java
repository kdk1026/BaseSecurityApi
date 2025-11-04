package com.kdk.app.login.vo;

import com.kdk.app.common.vo.CommonResVo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2024. 6. 7. kdk	최초작성
 * </pre>
 *
 *
 * @author kdk
 */
@Getter
@Setter
@ToString
public class LoginResVo extends CommonResVo {

	@Schema(description = "토큰 타입", requiredMode = Schema.RequiredMode.REQUIRED)
	private String tokenType;

	@Schema(description = "접근 토큰", requiredMode = Schema.RequiredMode.REQUIRED)
	private String accessToken;

	@Schema(description = "접근 토큰 만료시간", requiredMode = Schema.RequiredMode.REQUIRED)
	private int accessTokenExpireSecond;

	@Schema(description = "갱신 토큰", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private String refreshToken;

	@Schema(description = "갱신 토큰 만료시간", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private int refreshTokenExpireSecond;

	@Schema(description = "아이디", requiredMode = Schema.RequiredMode.REQUIRED)
	private String userId;

}
