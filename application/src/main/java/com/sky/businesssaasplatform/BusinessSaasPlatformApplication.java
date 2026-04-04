package com.sky.businesssaasplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing
@ComponentScan(basePackages = {
		"com.sky.businesssaasplatform",
		"com.sky.tenant",
		"com.sky.catalog",
		"com.sky.transaction",
		"com.sky.core"
})
@EnableJpaRepositories(basePackages = {
		"com.sky.businesssaasplatform",
		"com.sky.tenant",
		"com.sky.catalog",
		"com.sky.transaction",
		"com.sky.core"
})
public class BusinessSaasPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(BusinessSaasPlatformApplication.class, args);
	}

}
