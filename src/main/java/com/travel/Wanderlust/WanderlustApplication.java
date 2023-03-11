package com.travel.Wanderlust;

import com.travel.Wanderlust.config.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
public class WanderlustApplication {

	public static void main(String[] args) {
		SpringApplication.run(WanderlustApplication.class, args);
	}

}
