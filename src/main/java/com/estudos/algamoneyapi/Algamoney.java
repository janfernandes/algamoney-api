package com.estudos.algamoneyapi;

import com.estudos.algamoneyapi.config.property.AlgamoneyApiProperty;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableConfigurationProperties(AlgamoneyApiProperty.class)
public class Algamoney {

	private static ConfigurableApplicationContext APPLICATION_CONTEXT;

	public static void main(String[] args) {
		APPLICATION_CONTEXT = SpringApplication.run(Algamoney.class, args);
	}

	public static <T> T getBean(Class<T> type){
		return APPLICATION_CONTEXT.getBean(type);
	}
}
