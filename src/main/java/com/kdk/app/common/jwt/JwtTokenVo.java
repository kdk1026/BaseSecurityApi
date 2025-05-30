package com.kdk.app.common.jwt;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class JwtTokenVo {

	private String accessToken;
	private String refreshToken;

	private String accessTokenIv;
	private String refreshTokenIv;

}
