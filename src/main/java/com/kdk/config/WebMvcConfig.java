package com.kdk.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdk.app.common.component.SpringBootProperty;
import com.kdk.app.common.interceptor.SwaggerInterceptor;
import com.kdk.config.mvc.HTMLCharacterEscapes;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 1. 27. kdk	최초작성
 * </pre>
 *
 *
 * @author kdk
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Value("${upload.folder}")
	private String uploadFolder;

	@Value("${cors.origins}")
	private String corsOrigins;

	private final SpringBootProperty springBootProperty;

	public WebMvcConfig(SpringBootProperty springBootProperty) {
		this.springBootProperty = springBootProperty;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		/*
		 * allowCredentials(true) 쿠키 인증 요청 허용
		 * allowedOrigins("*") 와 allowCredentials(true) 동시 사용 불가
		 */
		registry
			.addMapping("/**")
			.allowedOriginPatterns(corsOrigins)
			.allowedMethods("GET", "POST", "PUT", "DELETE")
			.allowedHeaders("*")
			.exposedHeaders("Content-Disposition")
			.allowCredentials(true)
			.maxAge(3600);
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addRedirectViewController("/", "/swagger-ui/index.html");
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor( new SwaggerInterceptor(springBootProperty) )
			.addPathPatterns("/swagger-ui/index.html");
	}

    @Bean
    MappingJackson2HttpMessageConverter jsonEscapeConverter() {
		ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
		objectMapper.getFactory().setCharacterEscapes(new HTMLCharacterEscapes());
		return new MappingJackson2HttpMessageConverter(objectMapper);
	}

	// XXX jsonEscapeConverter 가 제대로 동작 안하는 경우 설정
	/*
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		// JSON
		boolean jsonReplaced = false;

		for (int i = 0; i < converters.size(); i++) {
			if (converters.get(i).getClass() == MappingJackson2HttpMessageConverter.class) {
				converters.set(i, jsonEscapeConverter());
				jsonReplaced = true;
			}
		}
		if (!jsonReplaced) converters.add(0, jsonEscapeConverter());

		// YAML
		converters.add(new MappingJackson2YamlHttpMessageConverter());

		// XML (라이브러리 필요)
//		if (converters.stream().noneMatch(c -> c instanceof MappingJackson2XmlHttpMessageConverter)) {
//	        converters.add(new MappingJackson2XmlHttpMessageConverter());
//	    }
	}
	*/

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry
			.addResourceHandler("/upload/**")
			.addResourceLocations(uploadFolder);
	}

}
