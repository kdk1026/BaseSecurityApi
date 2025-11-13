package com.kdk.app.common.interceptor;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import com.kdk.app.common.CommonConstants;
import com.kdk.app.common.util.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 11. 13. 김대광	최초작성
 * </pre>
 *
 *
 * @author 김대광
 */
public class CsrfTokenInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		String csrfToken = request.getHeader(CommonConstants.CsrfToken.CSRF_TOKEN_HEADER_KEY);
		String csrfTokenInCookie = CookieUtil.getCookieValue(request, CommonConstants.CsrfToken.CSRF_TOKEN_COOKIE_KEY);

		if ( csrfToken.equals(csrfTokenInCookie) ) {
			return true;
		} else {
			response.sendError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.name());
			return false;
		}
	}

}
