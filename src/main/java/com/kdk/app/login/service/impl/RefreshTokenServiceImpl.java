package com.kdk.app.login.service.impl;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.kdk.app.common.CommonConstants;
import com.kdk.app.common.component.SpringBootProperty;
import com.kdk.app.common.jwt.JwtTokenProvider;
import com.kdk.app.common.util.date.Jsr310DateUtil;
import com.kdk.app.common.vo.ResponseCodeEnum;
import com.kdk.app.login.service.RefreshTokenService;
import com.kdk.app.login.vo.LoginResVo;
import com.kdk.app.login.vo.RefreshTokenParamVo;
import com.kdk.app.login.vo.UserVo;

import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

	private SpringBootProperty springBootProperty;

	public RefreshTokenServiceImpl(SpringBootProperty springBootProperty) {
		this.springBootProperty = springBootProperty;
	}

	@Override
	public LoginResVo refreshToken(RefreshTokenParamVo refreshTokenParamVo) {
		LoginResVo loginResVo;

		JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(springBootProperty);

		// ------------------------------------------------------------------------
		// 접근 토큰 유효성 검증
		// ------------------------------------------------------------------------
		String sAccessToken = refreshTokenParamVo.getAccessToken();

		loginResVo = this.validToken(sAccessToken, jwtTokenProvider, 1);

		if ( !StringUtils.isBlank(loginResVo.getCode())
				&& !ResponseCodeEnum.ACCESS_TOKEN_EXPIRED.getCode().equals(loginResVo.getCode()) ) {
			return loginResVo;
		}

		// ------------------------------------------------------------------------
		// 갱신 토큰 유효성 검증
		// ------------------------------------------------------------------------
//		HttpServletRequest request = ContextUtil.getInstance().getRequest();
//		String sRefreshToken = CookieUtil.getCookie(request, CommonConstants.Jwt.REFRESH_TOKEN).getValue();
		String sRefreshToken = refreshTokenParamVo.getRefreshToken();

		loginResVo = this.validToken(sRefreshToken, jwtTokenProvider, 2);

		if ( !StringUtils.isBlank(loginResVo.getCode()) ) {
			return loginResVo;
		}

		// ------------------------------------------------------------------------
		// 토큰에서 사용자 정보 추출
		// ------------------------------------------------------------------------
		UserVo userVo = jwtTokenProvider.getAuthUserFromJwt(sRefreshToken);

		//--------------------------------------------------
		// Access 토큰 갱신 조건 검증
		//--------------------------------------------------
		Date accessTokenDate = jwtTokenProvider.getExpirationFromJwt(sAccessToken);
		String sAccessExpireDate = Jsr310DateUtil.Convert.getDateToString(accessTokenDate, "yyyyMMddHHmmss");
		int nMinGap = Jsr310DateUtil.GetTimeInterval.intervalMinutes(sAccessExpireDate);

		// ------------------------------------------------------------------------
		// Access 토큰 갱신
		// ------------------------------------------------------------------------
		String sNewAccessToken = "";

		if ( nMinGap <= 0 ) {
			// 조건 충족 시, Access 토큰 갱신
			sNewAccessToken = jwtTokenProvider.generateAccessToken(userVo);
		} else {
			sNewAccessToken = refreshTokenParamVo.getAccessToken();
		}

		//--------------------------------------------------
		// Access 토큰 응답 설정
		//--------------------------------------------------
		String sAccessTokenExpireMin = springBootProperty.getProperty("jwt.access.expire.minute");
		int nAccessTokenExpireMin = Integer.parseInt(sAccessTokenExpireMin);

		loginResVo.setAccessTokenExpireSecond(nAccessTokenExpireMin * 60);
		loginResVo.setAccessToken(sNewAccessToken);

		//--------------------------------------------------
		// Refresh 토큰 갱신 조건 검증
		//--------------------------------------------------
		Date refreshTokenDate = jwtTokenProvider.getExpirationFromJwt(sRefreshToken);
		String sRefreshExpireDate = Jsr310DateUtil.Convert.getDateToString(refreshTokenDate, "yyyyMMddHHmmss");
		int nRefreshMinGap = Jsr310DateUtil.GetTimeInterval.intervalMinutes(sRefreshExpireDate);

		if ( nRefreshMinGap <= 60 ) {
			// 조건 충족 시, Refresh 토큰 갱신
			String sNewRefreshToken = jwtTokenProvider.generateRefreshToken(userVo);

			//--------------------------------------------------
			// Refresh 토큰 응답 설정
			//--------------------------------------------------
			String sRefreshTokenExpireMin = springBootProperty.getProperty("jwt.refresh.expire.minute");
			int nRefreshTokenExpireMin = Integer.parseInt(sRefreshTokenExpireMin);

			loginResVo.setRefreshTokenExpireSecond(nRefreshTokenExpireMin * 60);
			loginResVo.setRefreshToken(sNewRefreshToken);
		}

		String sTokenType = springBootProperty.getProperty("jwt.token.type");
		if ( sTokenType.lastIndexOf(" ") == -1 ) {
			sTokenType = sTokenType + " ";
		}

		loginResVo.setTokenType(sTokenType);

		loginResVo.setCode(ResponseCodeEnum.SUCCESS.getCode());
		loginResVo.setMessage(ResponseCodeEnum.SUCCESS.getMessage());
		return loginResVo;
	}

	private LoginResVo validToken(String token, JwtTokenProvider jwtTokenProvider, int type) {
		LoginResVo loginResVo = new LoginResVo();

		// ------------------------------------------------------------------------
		// 토큰 유효성 검증
		// ------------------------------------------------------------------------
		if ( StringUtils.isBlank(token) ) {
			loginResVo.setCode(ResponseCodeEnum.ACCESS_DENIED.getCode());
			loginResVo.setMessage(ResponseCodeEnum.ACCESS_DENIED.getMessage());
			return loginResVo;
		}

		int nValid = jwtTokenProvider.isValidateJwtToken(token);

		if ( nValid == 0 ) {
			loginResVo.setCode(ResponseCodeEnum.ACCESS_TOEKN_INVALID.getCode());
			loginResVo.setMessage(ResponseCodeEnum.ACCESS_TOEKN_INVALID.getMessage());
			return loginResVo;
		}

		if ( nValid == 2 ) {
			if ( type == 1 ) {
				log.error("Is Not AccessToken");

				loginResVo.setCode(ResponseCodeEnum.ACCESS_TOKEN_EXPIRED.getCode());
				loginResVo.setMessage(ResponseCodeEnum.ACCESS_TOKEN_EXPIRED.getMessage());
			} else {
				log.error("Is Not RefreshToken");

				loginResVo.setCode(ResponseCodeEnum.REFRESH_TOKEN_EXPIRED.getCode());
				loginResVo.setMessage(ResponseCodeEnum.REFRESH_TOKEN_EXPIRED.getMessage());
			}
		}

		// ------------------------------------------------------------------------
		// 토큰에 이상이 없는 경우, 타입에 따라 access_token인지 refresh_token인지 검증
		// ------------------------------------------------------------------------
		String sTokenKind = jwtTokenProvider.getTokenKind(token);

		if ( type == 1 && !CommonConstants.Jwt.ACCESS_TOKEN.equals(sTokenKind) ) {
			loginResVo.setCode(ResponseCodeEnum.ACCESS_TOEKN_INVALID.getCode());
			loginResVo.setMessage(ResponseCodeEnum.ACCESS_TOEKN_INVALID.getMessage());
			return loginResVo;
		}

		if ( type == 2 && !CommonConstants.Jwt.REFRESH_TOKEN.equals(sTokenKind) ) {
			loginResVo.setCode(ResponseCodeEnum.ACCESS_TOEKN_INVALID.getCode());
			loginResVo.setMessage(ResponseCodeEnum.ACCESS_TOEKN_INVALID.getMessage());
			return loginResVo;
		}

		// ------------------------------------------------------------------------
		// 토큰에 이상이 없는 경우, 사용자 정보 추출하여 검증
		// ------------------------------------------------------------------------
		UserVo userVo = jwtTokenProvider.getAuthUserFromJwt(token);

		if ( (userVo == null) || StringUtils.isBlank(userVo.getUsername()) ) {
			loginResVo.setCode(ResponseCodeEnum.ACCESS_TOEKN_INVALID.getCode());
			loginResVo.setMessage(ResponseCodeEnum.ACCESS_TOEKN_INVALID.getMessage());
			return loginResVo;
		}

		return loginResVo;
	}

}
