package com.kdk.app.sample.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2026. 1. 5. 김대광	최초작성
 * </pre>
 *
 *
 * @author 김대광
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PersonVo {

	private Integer seq;
	private String name;
	private Integer age;

}
