package com.kdk.app.common;

import java.nio.charset.StandardCharsets;

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
public class CommonConstants {
	private CommonConstants() {
		super();
	}

	// ------------------------------------------------------------------------
	// 인코딩
	// ------------------------------------------------------------------------
	public static class Encoding {
		private Encoding() {
			super();
		}

		public static final String UTF_8 = StandardCharsets.UTF_8.toString();
	}

	// ------------------------------------------------------------------------
	// 프로파일
	// ------------------------------------------------------------------------
	public static class Profile {
		private Profile() {
			super();
		}

		public static final String LOCAL = "local";
		public static final String DEV = "dev";
		public static final String PROD = "prod";
	}

	// ------------------------------------------------------------------------
	// JWT
	// ------------------------------------------------------------------------
	public static class Jwt {
		private Jwt() {
			super();
		}

		public static final String USER_INFO = "userInfo";
		public static final String TOKEN_KIND = "tokenKind";
		public static final String ACCESS_TOKEN = "accessToken";
		public static final String REFRESH_TOKEN = "refreshToken";
	}

}
