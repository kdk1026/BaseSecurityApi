package com.kdk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.ContentTypeOptionsConfig;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.kdk.app.common.component.SpringBootProperty;
import com.kdk.app.common.security.filter.JwtAuthenticationWebFilter;
import com.kdk.app.common.security.service.UserDetailsServiceImpl;

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

	private final UserDetailsServiceImpl userDetailsServiceImpl;
	private final SpringBootProperty springBootProperty;
	private final Environment env;

	public SecurityConfig(UserDetailsServiceImpl userDetailsServiceImpl, SpringBootProperty springBootProperty, Environment env) {
		this.userDetailsServiceImpl = userDetailsServiceImpl;
		this.springBootProperty = springBootProperty;
		this.env = env;
	}

	@Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
        		.requestMatchers("/favicon.ico", "/", "/swagger-ui/**", "/v3/**", "/login/**");
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		JwtAuthenticationWebFilter jwtAuthFilter = new JwtAuthenticationWebFilter(userDetailsServiceImpl, springBootProperty, env);

		http
			.authorizeHttpRequests(authorizeHttpRequests ->
				authorizeHttpRequests
					.requestMatchers("/admin/**").hasRole("ADMIN")
					.requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER")
					.requestMatchers("/upload/**").permitAll()
					.requestMatchers("/capsule-config/**").permitAll()
					.requestMatchers("/actuator/**").permitAll()
					.requestMatchers("/test/**").permitAll()
					.anyRequest().authenticated()
			)
			.formLogin(AbstractHttpConfigurer::disable
			)
			.csrf(csrf -> csrf.disable())
			.cors(this.corsCustomizer())
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
			.headers(headers ->
				headers
					.cacheControl(cache -> cache.disable())
					.contentTypeOptions(ContentTypeOptionsConfig::disable)
					.httpStrictTransportSecurity(this.hstsCustomizer())
					.frameOptions(FrameOptionsConfig::deny)
					.xssProtection(this.xssCustomizer())

			)
			;

		return http.build();
	}

    private Customizer<CorsConfigurer<HttpSecurity>> corsCustomizer() {
        return cors -> cors.configurationSource(this.corsConfigurationSource());
    }

	CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration configuration = new CorsConfiguration();

		String corsOrigins = springBootProperty.getProperty("cors.origins");

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

	private Customizer<HeadersConfigurer<HttpSecurity>.HstsConfig> hstsCustomizer() {
		return hsts -> hsts
				.includeSubDomains(true)
				.maxAgeInSeconds(31536000); // 1년
	}

	private Customizer<HeadersConfigurer<HttpSecurity>.XXssConfig> xssCustomizer() {
		return xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK);
	}

}
