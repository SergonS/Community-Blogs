package com.sergon.blogpost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BlogpostApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogpostApplication.class, args);
	}

}
