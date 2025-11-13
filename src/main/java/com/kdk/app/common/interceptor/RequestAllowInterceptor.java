package com.kdk.app.common.interceptor;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import com.kdk.app.common.CommonConstants;
import com.kdk.app.common.component.SpringBootProperty;
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
public class RequestAllowInterceptor implements HandlerInterceptor {

	private final SpringBootProperty springBootProperty;

	public RequestAllowInterceptor(SpringBootProperty springBootProperty) {
		this.springBootProperty = springBootProperty;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		String csrfToken = request.getHeader(CommonConstants.CsrfToken.CSRF_TOKEN_HEADER_KEY);
		String csrfTokenInCookie = CookieUtil.getCookieValue(request, CommonConstants.CsrfToken.CSRF_TOKEN_COOKIE_KEY);

		String origin = request.getHeader("Origin");
		String referer = request.getHeader("Referer");

		String frontUrl = springBootProperty.getProperty("front.url");

		boolean isAllow = false;

		if ( csrfToken.equals(csrfTokenInCookie) ) {
			isAllow = true;
		}

		if ( origin == null || !origin.equals(frontUrl) ) {
			isAllow = false;
		}

		if ( referer == null || !referer.contains(frontUrl) ) {
			isAllow = false;
		}

		if ( isAllow ) {
			return true;
		} else {
			response.sendError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.name());
			return false;
		}
	}

}
