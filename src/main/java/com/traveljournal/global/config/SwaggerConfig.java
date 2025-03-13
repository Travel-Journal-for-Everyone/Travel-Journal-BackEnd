package com.traveljournal.global.config;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
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

		// 공통 응답 정의
		Components components = new Components()
			.addResponses("Success", new ApiResponse()
				.description("성공 응답")
				.content(new Content().addMediaType("application/json",
					new MediaType().example(new HashMap<String, Object>() {{
						put("success", true);
						put("message", "요청이 성공적으로 처리되었습니다.");
						put("data", null);
					}})
				))
			)
			.addResponses("Logout", new ApiResponse()
				.description("성공 응답")
				.content(new Content().addMediaType("application/json",
					new MediaType().example(new HashMap<String, Object>() {{
						put("success", true);
						put("message", "로그아웃 성공");
						put("data", null);
					}})
				))
			)
			.addResponses("ValidName", new ApiResponse()
				.description("성공 응답")
				.content(new Content().addMediaType("application/json",
					new MediaType().example(new HashMap<String, Object>() {{
						put("success", true);
						put("message", "valid");
						put("data", null);
					}})
				))
			)
			.addResponses("DuplicateName", new ApiResponse()
				.description("실패 응답 - 중복 닉네임")
				.content(new Content().addMediaType("application/json",
					new MediaType().example(new HashMap<String, Object>() {{
						put("success", false);
						put("message", "duplicate");
						put("data", null);
					}})
				))
			)
			.addResponses("Unauthorized", new ApiResponse()
				.description("인증 실패")
				.content(new Content().addMediaType("application/json",
					new MediaType().example(new HashMap<String, Object>() {{
						put("success", false);
						put("message", "인증에 실패했습니다.");
						put("data", null);
					}})
				))
			);

		return new OpenAPI()
			.info(info)
			.servers(List.of(server))
			.components(components);
	}
}
