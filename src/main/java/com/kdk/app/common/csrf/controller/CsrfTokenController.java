package com.kdk.app.common.csrf.controller;

import java.util.UUID;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdk.app.common.CommonConstants;
import com.kdk.app.common.csrf.vo.CsrfTokenResVo;
import com.kdk.app.common.util.CookieUtil;
import com.kdk.app.common.vo.ResponseCodeEnum;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

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
@Tag(name = "CSRF 토큰 생성", description = "CSRF 토큰 생성 API")
@RestController
@RequestMapping("/csrf-token")
public class CsrfTokenController {

	private final Environment env;

	public CsrfTokenController(Environment env) {
		this.env = env;
	}

	@Operation(summary = "CSRF 토큰 생성")
	@GetMapping()
	public ResponseEntity<CsrfTokenResVo> getCsrfToken(HttpServletResponse response) {
		CsrfTokenResVo resVo = new CsrfTokenResVo();

		String csrfToken = UUID.randomUUID().toString();

		CookieUtil.addSessionCookie(response, CommonConstants.CsrfToken.CSRF_TOKEN_COOKIE_KEY, csrfToken, null, env.getActiveProfiles()[0]);
		resVo.setCsrfToken(csrfToken);

		resVo.setCode(ResponseCodeEnum.SUCCESS.getCode());
		resVo.setMessage(ResponseCodeEnum.SUCCESS.getMessage());
		return ResponseEntity.status(HttpStatus.OK).body(resVo);
	}

}
