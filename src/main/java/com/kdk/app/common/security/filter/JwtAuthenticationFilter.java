package com.kdk.app.common.security.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.kdk.app.common.jwt.JwtTokenProvider;
import com.kdk.app.common.security.service.UserDetailsServiceImpl;
import com.kdk.app.common.util.json.JacksonUtil;
import com.kdk.app.common.vo.CommonResVo;
import com.kdk.app.common.vo.ResponseCodeEnum;
import com.kdk.app.login.vo.UserVo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private UserDetailsServiceImpl userDetailsServiceImpl;

	public JwtAuthenticationFilter(UserDetailsServiceImpl userDetailsServiceImpl) {
		this.userDetailsServiceImpl = userDetailsServiceImpl;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		CommonResVo commonResVo = new CommonResVo();

		JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();

		// 1. 헤더에서 토큰 가져오기
		String sToken = jwtTokenProvider.getTokenFromReqHeader(request);

		if ( StringUtils.isBlank(sToken) ) {
			commonResVo.setCode(ResponseCodeEnum.ACCESS_TOEKN_INVALID.getCode());
			commonResVo.setMessage(ResponseCodeEnum.ACCESS_TOEKN_INVALID.getMessage());
		} else {
			// 2. 토큰 유효성 검증
			switch ( jwtTokenProvider.isValidateJwtToken(sToken) ) {
			case 0:
				commonResVo.setCode(ResponseCodeEnum.ACCESS_TOEKN_INVALID.getCode());
				commonResVo.setMessage(ResponseCodeEnum.ACCESS_TOEKN_INVALID.getMessage());
				break;
			case 2:
				commonResVo.setCode(ResponseCodeEnum.ACCESS_TOKEN_EXPIRED.getCode());
				commonResVo.setMessage(ResponseCodeEnum.ACCESS_TOKEN_EXPIRED.getMessage());
				break;

			default:
				break;
			}

			if ( !StringUtils.isBlank(commonResVo.getCode()) ) {
				String sMessage = JacksonUtil.ToJson.converterObjToJsonStr(commonResVo, false);

				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
				response.getWriter().write(sMessage);
			} else {
				// 3. 토큰에서 사용자 정보 추출
				UserVo userVo = jwtTokenProvider.getAuthUserFromJwt(sToken);

				UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(userVo.getUsername());

				UsernamePasswordAuthenticationToken authentication
					= new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);

				filterChain.doFilter(request, response);
			}
		}
	}

}
