package com.kdk.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.kdk.app.common.security.filter.JwtAuthenticationFilter;
import com.kdk.app.common.security.service.UserDetailsServiceImpl;
import com.kdk.app.common.util.spring.SpringBootPropertyUtil;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 1. 27. kdk	최초작성
 * </pre>
 *
 * JavaDoc에 나온 Example 대로 람다를 안쓰면 너무 복잡해짐...
 *
 * @author kdk
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;

	@Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
        		.requestMatchers("/favicon.ico", "/swagger-ui/**", "/v3/**", "/login/**");
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		JwtAuthenticationFilter jwtAuthFilter = new JwtAuthenticationFilter(userDetailsServiceImpl);

		http
			.authorizeHttpRequests((authorizeHttpRequests) ->
				authorizeHttpRequests
					.requestMatchers("/").permitAll()
					.requestMatchers("/admin/**").hasRole("ADMIN")
					.requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER")
					.requestMatchers("/upload/**").permitAll()
					.requestMatchers("/actuator/**").permitAll()
					.requestMatchers("/test/**").permitAll()
					.anyRequest().authenticated()
			)
			.formLogin((formLogin) ->
				formLogin
					.disable()
			)
			.csrf((csrf) -> csrf.disable())
			.cors(this.corsCustomizer())
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
			;

		return http.build();
	}

    private Customizer<CorsConfigurer<HttpSecurity>> corsCustomizer() {
        return (cors) -> cors.configurationSource(this.corsConfigurationSource());
    }

	CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration configuration = new CorsConfiguration();

		String corsOrigins = SpringBootPropertyUtil.getProperty("cors.origins");

		configuration.addAllowedOriginPattern(corsOrigins);
		configuration.addAllowedMethod("GET");
		configuration.addAllowedMethod("POST");
		configuration.addAllowedMethod("PUT");
		configuration.addAllowedMethod("DELETE");
		configuration.addAllowedHeader("*");
		configuration.addExposedHeader("Content-Disposition");
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600L);

		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}
