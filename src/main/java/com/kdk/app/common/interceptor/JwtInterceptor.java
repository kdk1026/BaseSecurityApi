package com.kdk.app.common.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import com.kdk.app.common.CommonConstants;
import com.kdk.app.common.jwt.JwtTokenProvider;
import com.kdk.app.common.vo.ResponseCodeEnum;
import com.kdk.app.common.vo.UserVo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		String sUri = request.getRequestURI();

		if ( sUri.contains("swagger") || sUri.contains("api-docs") || sUri.contains("webjars") ) {
			return true;
		}

		if ( HttpMethod.OPTIONS.matches(request.getMethod()) ) {
			return true;
		}

		// ------------------------------------------------------------------------
		// 토큰 가져오기
		// ------------------------------------------------------------------------
		JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();

		String sAccessToken = jwtTokenProvider.getTokenFromReqHeader(request);

		// ------------------------------------------------------------------------
		// 토큰 유효성 검증
		// ------------------------------------------------------------------------
		if ( StringUtils.isBlank(sAccessToken) ) {
			log.error("No AccessToken");

			response.sendError(HttpStatus.UNAUTHORIZED.value(), ResponseCodeEnum.ACCESS_DENIED.getMessage());
			return false;
		}

		int nValid = jwtTokenProvider.isValidateJwtToken(sAccessToken);

		if ( nValid == 0 ) {
			log.error("AccessToken Invalid");
			response.sendError(HttpStatus.UNAUTHORIZED.value(), ResponseCodeEnum.ACCESS_TOEKN_INVALID.getMessage());
			return false;
		}

		if ( nValid == 2 ) {
			log.error("AccessToken Expired");
			response.sendError(HttpStatus.UNAUTHORIZED.value(), ResponseCodeEnum.ACCESS_TOKEN_EXPIRED.getMessage());
			return false;
		}

		// ------------------------------------------------------------------------
		// 토큰에 이상이 없는 경우, access_token인지 검증
		// ------------------------------------------------------------------------
		String sTokenKind = jwtTokenProvider.getTokenKind(sAccessToken);

		if ( !CommonConstants.Jwt.ACCESS_TOKEN.equals(sTokenKind) ) {
			log.error("Is Not AccessToken");

			response.sendError(HttpStatus.UNAUTHORIZED.value(), ResponseCodeEnum.ACCESS_TOEKN_INVALID.getMessage());
			return false;
		}

		// ------------------------------------------------------------------------
		// 토큰에 이상이 없는 경우, 사용자 정보 추출하여 검증
		// ------------------------------------------------------------------------
		UserVo user = jwtTokenProvider.getAuthUserFromJwt(sAccessToken);

		if ( (user == null) || StringUtils.isBlank(user.getUsername()) ) {
			log.error("UserInfo Invalid");

			response.sendError(HttpStatus.UNAUTHORIZED.value(), ResponseCodeEnum.ACCESS_TOEKN_INVALID.getMessage());
			return false;
		}

		// ------------------------------------------------------------------------
		// 편하게 사용하기 위해, request에 담음
		// - 세션이 아니므로 addPathPatterns 대상 URI에서만 가지고 올 수 있음
		// ------------------------------------------------------------------------
		request.setAttribute("user", user);

		return true;
	}

}
