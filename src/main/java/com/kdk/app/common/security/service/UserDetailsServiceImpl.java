package com.kdk.app.common.security.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kdk.app.common.vo.UserVo;
import com.kdk.app.login.service.LoginService;

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
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private LoginService loginService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserVo vo = loginService.findByUsername(username);

		if ( vo == null ) {
			throw new UsernameNotFoundException("User not found");
		}

		List<String> authorities = loginService.findAuthoritiesByUsername(username);

		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		for ( String authority : authorities ) {
			grantedAuthorities.add(new SimpleGrantedAuthority(authority));
		}

		boolean enabled = "Y".equals(vo.getUseYn()) ? true : false;

		return new User(vo.getUsername(), vo.getPassword(), enabled, true, true, true, grantedAuthorities);
	}

}
