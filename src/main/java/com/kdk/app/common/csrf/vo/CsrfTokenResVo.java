package com.kdk.app.common.csrf.vo;

import com.kdk.app.common.vo.CommonResVo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
@Getter
@Setter
@ToString
public class CsrfTokenResVo extends CommonResVo {

	private String csrfToken;

}
