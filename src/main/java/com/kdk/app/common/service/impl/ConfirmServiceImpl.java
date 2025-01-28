package com.kdk.app.common.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.kdk.app.common.service.ConfirmService;
import com.kdk.app.common.vo.ConfirmResVo;
import com.kdk.app.common.vo.ResponseCodeEnum;

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
public class ConfirmServiceImpl implements ConfirmService {

	@Override
	public ConfirmResVo getConfirm(Authentication authentication) {
		ConfirmResVo confirmResVo = new ConfirmResVo();

		String sUsernam = authentication.getName();

		@SuppressWarnings("unchecked")
		List<GrantedAuthority> authorities = (List<GrantedAuthority>) authentication.getAuthorities();
		List<String> roles = new ArrayList<>();
		for ( GrantedAuthority authoritiy : authorities ) {
			roles.add(authoritiy.getAuthority());
		}

		confirmResVo.setUsername(sUsernam);
		confirmResVo.setRoles(roles);

		confirmResVo.setCode(ResponseCodeEnum.SUCCESS.getCode());
		confirmResVo.setMessage(ResponseCodeEnum.SUCCESS.getMessage());
		return confirmResVo;
	}

}
