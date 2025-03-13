package com.traveljournal.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;

@Configuration
public class FeignConfig {
	@Bean
	Logger.Level feginLoggerLevel() {
		return Logger.Level.FULL;
	}
}
