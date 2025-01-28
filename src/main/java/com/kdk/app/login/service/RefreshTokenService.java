package com.kdk.app.login.service;

import com.kdk.app.login.vo.LoginResVo;
import com.kdk.app.login.vo.RefreshTokenParamVo;

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
public interface RefreshTokenService {

	/**
	 * 토큰 갱신 처리
	 * @param refreshTokenParamVo
	 * @return
	 * @throws Exception
	 */
	public LoginResVo refreshToken(RefreshTokenParamVo refreshTokenParamVo) throws Exception;

}
