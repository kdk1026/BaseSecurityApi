package com.kdk.app.common.encryption.vo;

import com.kdk.app.common.vo.CommonResVo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 11. 7. 김대광	최초작성
 * </pre>
 *
 *
 * @author 김대광
 */
@Getter
@Setter
@ToString
public class AesKeyResVo extends CommonResVo {

	private AesKeyVo data;

}
