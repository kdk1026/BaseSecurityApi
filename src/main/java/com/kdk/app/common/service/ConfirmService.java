package com.kdk.app.common.service;

import org.springframework.security.core.Authentication;

import com.kdk.app.common.vo.ConfirmResVo;

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
public interface ConfirmService {

	public ConfirmResVo getConfirm(Authentication authentication);

}
