package com.kdk.app.common.security.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.kdk.app.common.CommonConstants;
import com.kdk.app.common.component.SpringBootProperty;
import com.kdk.app.common.jwt.JwtTokenProvider;
import com.kdk.app.common.security.service.UserDetailsServiceImpl;
import com.kdk.app.common.util.CookieUtil;
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
public class JwtAuthenticationWebFilter extends OncePerRequestFilter {

	private final UserDetailsServiceImpl userDetailsServiceImpl;
	private final SpringBootProperty springBootProperty;
	private final Environment env;

	public JwtAuthenticationWebFilter(UserDetailsServiceImpl userDetailsServiceImpl, SpringBootProperty springBootProperty, Environment env) {
		this.userDetailsServiceImpl = userDetailsServiceImpl;
		this.springBootProperty = springBootProperty;
		this.env = env;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		CommonResVo commonResVo = new CommonResVo();

		JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(springBootProperty);

		// 1. 헤더에서 토큰 가져오기
		String sToken = jwtTokenProvider.getTokenFromReqHeader(request);

		if ( StringUtils.isBlank(sToken) ) {
			commonResVo.setCode(ResponseCodeEnum.ACCESS_DENIED.getCode());
			commonResVo.setMessage(ResponseCodeEnum.ACCESS_DENIED.getMessage());
		} else {
			String sRefreshToken = CookieUtil.getCookieValue(request, CommonConstants.Jwt.REFRESH_TOKEN);

			if ( StringUtils.isBlank(sRefreshToken) ) {
				commonResVo.setCode(ResponseCodeEnum.ACCESS_DENIED.getCode());
				commonResVo.setMessage(ResponseCodeEnum.ACCESS_DENIED.getMessage());
			} else {
				// 2. 토큰 유효성 검증
				switch ( jwtTokenProvider.isValidateJwtToken(sToken) ) {
				case 0:
					commonResVo.setCode(ResponseCodeEnum.ACCESS_TOEKN_INVALID.getCode());
					commonResVo.setMessage(ResponseCodeEnum.ACCESS_TOEKN_INVALID.getMessage());
					break;
				case 2:
					String sProfile = env.getActiveProfiles()[0];
					String sAccessToken = jwtTokenProvider.getRenewedAccessToken(sRefreshToken, sProfile);

					UserVo userVo = jwtTokenProvider.getAuthUserFromJwt(sAccessToken);

					UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(userVo.getUsername());

					UsernamePasswordAuthenticationToken authentication
						= new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

					SecurityContextHolder.getContext().setAuthentication(authentication);

					filterChain.doFilter(request, response);
					break;

				default:
					break;
				}
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
