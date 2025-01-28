package com.kdk.app.common.vo;

import java.util.List;

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

	@Schema(description = "권한", requiredMode = Schema.RequiredMode.REQUIRED)
	private List<String> roles;

}
