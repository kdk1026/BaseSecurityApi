package com.kdk.app.login.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kdk.app.login.vo.UserVo;

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

	private static final String MEMBER_USER = "user";
	private static final String MEMBER_MANAGER = "manager";
	private static final String MEMBER_ADMIN = "admin";

	private static final String BCRYPT_PASSWORD = "$2a$10$SHHHWnin79imcnL7c9VEBO5TRaBeaB11wVFYJ6CCSlIjBIaM/89Sy";

	private static final String ROLE_USER = "ROLE_USER";

	public UserVo findByUsername(String username) {
		UserVo vo = null;

		if ( MEMBER_USER.equals(username) ) {
			vo = new UserVo();
			vo.setUsername(MEMBER_USER);
			vo.setPassword(BCRYPT_PASSWORD);
			vo.setUseYn("Y");
		}

		if ( MEMBER_MANAGER.equals(username) ) {
			vo = new UserVo();
			vo.setUsername(MEMBER_MANAGER);
			vo.setPassword(BCRYPT_PASSWORD);
			vo.setUseYn("Y");
		}

		if ( MEMBER_ADMIN.equals(username) ) {
			vo = new UserVo();
			vo.setUsername(MEMBER_ADMIN);
			vo.setPassword(BCRYPT_PASSWORD);
			vo.setUseYn("Y");
		}

		return vo;
	}

	public List<String> findAuthoritiesByUsername(String username) {
		List<String> authorities = new ArrayList<>();

		if ( MEMBER_USER.equals(username) ) {
			authorities.add(ROLE_USER);
		}

		if ( MEMBER_MANAGER.equals(username) ) {
			authorities.add(ROLE_USER);
			authorities.add("ROLE_MANAGER");
		}

		if ( MEMBER_ADMIN.equals(username) ) {
			authorities.add(ROLE_USER);
			authorities.add("ROLE_MANAGER");
			authorities.add("ROLE_ADMIN");
		}

		return authorities;
	}

}
