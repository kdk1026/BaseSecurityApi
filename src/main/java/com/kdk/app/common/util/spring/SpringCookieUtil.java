package com.kdk.app.common.util.spring;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.util.StringUtils;

import com.kdk.app.common.ExceptionMessage;

import jakarta.servlet.http.HttpServletResponse;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2024. 7. 3. kdk	최초작성
 * </pre>
 *
 * <pre>
 * Spring 5.0 부터 지원
 *   - 5.1 부터는 사용 빈도는 적으나 sameSite 설정 가능
 *   - <a href="https://cherish-it.tistory.com/12">sameSite 참고</a>
 * </pre>
 * @author kdk
 */
public class SpringCookieUtil {

	private SpringCookieUtil() {
		super();
	}

	private static class LazyHolder {
		private static final SpringCookieUtil INSTANCE = new SpringCookieUtil();
	}

	public static SpringCookieUtil getInstance() {
		return LazyHolder.INSTANCE;
	}

	/**
	 * Spring 쿠키 설정
	 * @param response
	 * @param name
	 * @param value
	 * @param maxAge
	 * @param isSecure
	 * @param isHttpOnly
	 * @param domain
	 */
	public void addCookie(HttpServletResponse response, String name, String value, int maxAge, boolean isSecure, boolean isHttpOnly, String domain) {
		if ( response == null ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("response"));
		}

		if ( !StringUtils.hasText(name) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("name"));
		}

		if ( !StringUtils.hasText(value) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("value"));
		}

		ResponseCookie cookie = ResponseCookie.from(value, value)
				.path("/")
				.maxAge(maxAge)
				.secure(isSecure)
				.httpOnly(isHttpOnly)
				.domain( StringUtils.hasText(domain) ? domain : null )
				.build();

		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}

}
