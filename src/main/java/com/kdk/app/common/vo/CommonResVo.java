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
 * 2024. 6. 7. kdk	최초작성
 * </pre>
 *
 *
 * @author kdk
 */
@Getter
@Setter
@ToString
public class CommonResVo {

	@Schema(description = "응답 코드", requiredMode = Schema.RequiredMode.REQUIRED)
	private String code;

	@Schema(description = "응답 메시지", requiredMode = Schema.RequiredMode.REQUIRED)
	private String message;

}
