package com.kdk.app.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdk.app.common.service.ConfirmService;
import com.kdk.app.common.vo.ConfirmResVo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(name = "confirm", description = "권한 확인 API")
@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private ConfirmService confirmService;

	@Operation(summary = "Admin 확인")
	@GetMapping(value = "confirm", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ConfirmResVo> confirm(Authentication authentication) {
		ConfirmResVo confirmResVo = confirmService.getConfirm(authentication);
		return ResponseEntity.status(HttpStatus.OK).body(confirmResVo);
	}

}
