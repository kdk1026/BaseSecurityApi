package com.kdk.app.login.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdk.app.common.vo.ResponseCodeEnum;
import com.kdk.app.login.service.RefreshTokenService;
import com.kdk.app.login.vo.LoginResVo;
import com.kdk.app.login.vo.RefreshTokenParamVo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@Tag(name = "login", description = "로그인 API")
@RestController
@RequestMapping("/login")
public class RefreshTokenController {

	@Autowired
	private RefreshTokenService refreshTokenService;

	@Operation(summary = "로그인")
	@GetMapping(value = "refresh-token", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LoginResVo> refreshToken(@Valid RefreshTokenParamVo refreshTokenParamVo, BindingResult bindingResult,
			HttpServletResponse response) {
		LoginResVo loginResVo = new LoginResVo();

		if ( bindingResult.hasErrors() ) {
			loginResVo.setCode(ResponseCodeEnum.NO_INPUT.getCode());
			loginResVo.setMessage( (bindingResult.getAllErrors()).get(0).getDefaultMessage() );
			return ResponseEntity.status(HttpStatus.OK).body(loginResVo);
		}

		try {
			loginResVo = refreshTokenService.refreshToken(refreshTokenParamVo);

		} catch (Exception e) {
			log.error("", e);

			loginResVo.setCode(ResponseCodeEnum.ERROR.getCode());
			loginResVo.setMessage(ResponseCodeEnum.ERROR.getMessage());
		}

		return ResponseEntity.status(HttpStatus.OK).body(loginResVo);
	}

}
