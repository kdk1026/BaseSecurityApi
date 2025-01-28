package com.kdk.app.common.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
@Getter
@Setter
@ToString
public class ConfirmResVo extends CommonResVo {

	@Schema(description = "아이디", requiredMode = Schema.RequiredMode.REQUIRED)
	private String username;

}
