package com.kdk.app.login.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdk.app.common.component.SpringBootProperty;
import com.kdk.app.common.jwt.JwtTokenProvider;
import com.kdk.app.common.vo.ResponseCodeEnum;
import com.kdk.app.login.service.LoginService;
import com.kdk.app.login.vo.LoginParamVo;
import com.kdk.app.login.vo.LoginResVo;
import com.kdk.app.login.vo.UserVo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

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
@Tag(name = "login", description = "로그인 API")
@RestController
@RequestMapping("/login")
public class LoginController {

	private LoginService loginService;
	private SpringBootProperty springBootProperty;

	public LoginController(LoginService loginService, SpringBootProperty springBootProperty) {
		this.loginService = loginService;
		this.springBootProperty = springBootProperty;
	}

	@Operation(summary = "로그인")
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LoginResVo> login(@Valid LoginParamVo loginParamVo, BindingResult bindingResult,
			HttpServletResponse response) {
		LoginResVo loginResVo = new LoginResVo();

		if ( bindingResult.hasErrors() ) {
			loginResVo.setCode(ResponseCodeEnum.NO_INPUT.getCode());
			loginResVo.setMessage( (bindingResult.getAllErrors()).get(0).getDefaultMessage() );
			return ResponseEntity.status(HttpStatus.OK).body(loginResVo);
		}

		UserVo userVo = loginService.findByUsername(loginParamVo.getUserId());
		if ( userVo == null ) {
			loginResVo.setCode(ResponseCodeEnum.LOGIN_INVALID.getCode());
			loginResVo.setMessage(ResponseCodeEnum.LOGIN_INVALID.getMessage());
			return ResponseEntity.status(HttpStatus.OK).body(loginResVo);
		}

		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

		boolean isPasswordMatches = bCryptPasswordEncoder.matches(loginParamVo.getUserPw(), userVo.getPassword());
		if ( !isPasswordMatches ) {
			loginResVo.setCode(ResponseCodeEnum.LOGIN_INVALID.getCode());
			loginResVo.setMessage(ResponseCodeEnum.LOGIN_INVALID.getMessage());
			return ResponseEntity.status(HttpStatus.OK).body(loginResVo);
		}

		JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(springBootProperty);

		String sToken = jwtTokenProvider.generateAccessToken(userVo);
		loginResVo.setAccessToken(sToken);

		String sAccessTokenExpireMin = springBootProperty.getProperty("jwt.access.expire.minute");
		int nAccessTokenExpireMin = Integer.parseInt(sAccessTokenExpireMin);
		loginResVo.setAccessTokenExpireSecond(nAccessTokenExpireMin * 60);

		String sRefreshToken = jwtTokenProvider.generateRefreshToken(userVo);
		loginResVo.setRefreshToken(sRefreshToken);

		String sRefreshTokenExpireMin = springBootProperty.getProperty("jwt.refresh.expire.minute");
		int nRefreshTokenExpireMin = Integer.parseInt(sRefreshTokenExpireMin);
		loginResVo.setRefreshTokenExpireSecond(nRefreshTokenExpireMin * 60);

		String sTokenType = springBootProperty.getProperty("jwt.token.type");
		if ( sTokenType.lastIndexOf(" ") == -1 ) {
			sTokenType = sTokenType + " ";
		}

		loginResVo.setTokenType(sTokenType);

		loginResVo.setUserId(userVo.getUsername());

		loginResVo.setCode(ResponseCodeEnum.SUCCESS.getCode());
		loginResVo.setMessage(ResponseCodeEnum.SUCCESS.getMessage());
		return ResponseEntity.status(HttpStatus.OK).body(loginResVo);
	}

}
