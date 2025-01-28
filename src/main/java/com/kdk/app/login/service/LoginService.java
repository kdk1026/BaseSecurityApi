package com.kdk.app.login.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kdk.app.common.vo.UserVo;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 1. 27. kdk	최초작성
 * </pre>
 *
 * @author kdk
 */
@Service
public class LoginService {

	public UserVo findByUsername(String username) {
		UserVo vo = null;

		if ( "user".equals(username) ) {
			vo = new UserVo();
			vo.setUsername("user");
			vo.setPassword("$2a$10$SHHHWnin79imcnL7c9VEBO5TRaBeaB11wVFYJ6CCSlIjBIaM/89Sy");
			vo.setUseYn("Y");
		}

		if ( "manager".equals(username) ) {
			vo = new UserVo();
			vo.setUsername("manager");
			vo.setPassword("$2a$10$SHHHWnin79imcnL7c9VEBO5TRaBeaB11wVFYJ6CCSlIjBIaM/89Sy");
			vo.setUseYn("Y");
		}

		if ( "admin".equals(username) ) {
			vo = new UserVo();
			vo.setUsername("admin");
			vo.setPassword("$2a$10$SHHHWnin79imcnL7c9VEBO5TRaBeaB11wVFYJ6CCSlIjBIaM/89Sy");
			vo.setUseYn("Y");
		}

		return vo;
	}

	public List<String> findAuthoritiesByUsername(String username) {
		List<String> authorities = new ArrayList<>();

		if ( "user".equals(username) ) {
			authorities.add("ROLE_USER");
		}

		if ( "manager".equals(username) ) {
			authorities.add("ROLE_USER");
			authorities.add("ROLE_MANAGER");
		}

		if ( "admin".equals(username) ) {
			authorities.add("ROLE_USER");
			authorities.add("ROLE_MANAGER");
			authorities.add("ROLE_ADMIN");
		}

		return authorities;
	}

}
