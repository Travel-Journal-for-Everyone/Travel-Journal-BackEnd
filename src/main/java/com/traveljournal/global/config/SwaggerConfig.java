package com.traveljournal.global.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

	@Value("${springdoc.request-url}")
	private String requestUrl;

	@Bean
	public OpenAPI openAPI() {
		Info info = new Info()
			.version("v1")
			.title("Travel Journal API")
			.description("여행 일지 애플리케이션의 API 문서입니다.");

		Server server = new Server();
		server.setUrl(requestUrl);
		server.setDescription("API 서버");

		return new OpenAPI()
			.info(info)
			.servers(List.of(server));
	}
}
