package com.example.filemanager;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FileManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileManagerApplication.class, args);
	}

	@Bean
	public MinioClient minioClient(){
		return MinioClient.builder()
				.endpoint("http://127.0.0.1:9000")
				.credentials("minio99", "minio123")
				.build();
	}

}
