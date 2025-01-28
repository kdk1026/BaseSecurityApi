package com.kdk.app.common.vo;

import java.text.MessageFormat;

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
public enum ResponseCodeEnum {

	SUCCESS("200", "성공"),
	LOGIN_INVALID("500", "아이디 또는 비밀번호를 확인해주세요."),
	NO_INPUT("500", "{0} 은(는) 필수 항목입니다."),
	SEARCH_FAIL("500", "조회중 오류가 발생했습니다."),
	ERROR("500", "처리 실패\n잠시 후 다시 시도해주세요.\n지속적인 오류 발생 시 관리자에게 문의바랍니다."),

	// 인증
	ACCESS_DENIED("401", "로그인 후 이용해 주세요."),
	ACCESS_TOEKN_INVALID("401", "인증정보가 올바르지 않습니다."),
	ACCESS_TOKEN_EXPIRED("402", "인증정보가 만료되었습니다."),
	REFRESH_TOKEN_EXPIRED("403", "인증정보 갱신기간이 만료되었습니다.")
	;

	private String code;
	private String message;

	private ResponseCodeEnum(String code, String message) {
		this.code = code;
		this.message = message;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	public String getMessage(Object ... arguments) {
		return MessageFormat.format(message, arguments);
	}

}
