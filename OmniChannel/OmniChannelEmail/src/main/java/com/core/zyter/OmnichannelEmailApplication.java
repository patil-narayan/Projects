package com.core.zyter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Omnichannel API", version = "2.0", description = "Email Information"))
public class OmnichannelEmailApplication {

	public static void main(String[] args) {
		SpringApplication.run(OmnichannelEmailApplication.class, args);
	}

}
