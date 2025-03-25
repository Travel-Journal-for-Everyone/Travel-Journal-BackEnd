package com.traveljournal.global.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PreDestroy;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.crt.AwsCrtHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {
	@Value("${spring.cloud.aws.credentials.access-key}")
	private String accessKey;

	@Value("${spring.cloud.aws.credentials.secret-key}")
	private String secretKey;

	@Value("${spring.cloud.aws.region.static}")
	private String region;

	private S3Client s3Client;

	@Bean
	public S3Client s3Client() {
		AwsCrtHttpClient.Builder httpClientBuilder = AwsCrtHttpClient.builder()
			.connectionTimeout(Duration.ofSeconds(5))
			.maxConcurrency(100);

		this.s3Client = S3Client.builder()
			.region(Region.of(region))
			.credentialsProvider(StaticCredentialsProvider.create(
				AwsBasicCredentials.create(accessKey, secretKey)))
			.httpClient(httpClientBuilder.build())
			.build();

		return this.s3Client;
	}

	@PreDestroy
	public void closeS3Client() {
		if (this.s3Client != null) {
			this.s3Client.close();
		}
	}
}