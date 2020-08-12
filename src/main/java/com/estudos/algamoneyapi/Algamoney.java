package com.estudos.algamoneyapi;

import com.estudos.algamoneyapi.config.property.AlgamoneyApiProperty;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AlgamoneyApiProperty.class)
public class Algamoney {

	public static void main(String[] args) {
		SpringApplication.run(Algamoney.class, args);
	}
}
