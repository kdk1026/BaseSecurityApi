package com.kdk.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2024. 6. 7. kdk	최초작성
 * </pre>
 *
 *
 * @author kdk
 */
@Configuration
public class SwaggerConfiguration {

	@Bean
	public SwaggerProperties swaggerProperties() {
		return new SwaggerProperties();
	}

	@Bean
	public OpenAPI openAPI(SwaggerProperties swaggerProperties) {
		String jwtSchemeName = "jwtAuth";

		SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

		return new OpenAPI()
			.info(apiInfo(swaggerProperties))
			.addSecurityItem(securityRequirement)
			.components(new Components()
					.addSecuritySchemes(jwtSchemeName,
							new SecurityScheme()
							.type(Type.HTTP)
							.scheme("bearer")
							.bearerFormat("JWT")
					)
			);
	}

	private Info apiInfo(SwaggerProperties swaggerProperties) {
		return new Info()
				.version(swaggerProperties.getVersion())
				.title(swaggerProperties.getTitle())
				.description(swaggerProperties.getDescription());
	}

}