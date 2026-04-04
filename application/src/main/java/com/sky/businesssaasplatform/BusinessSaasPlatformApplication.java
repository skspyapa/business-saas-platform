package com.sky.businesssaasplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
// @ComponentScan(basePackages = {
// 		"com.sky.businesssaasplatform",
// 		"com.sky.tenant",
// 		"com.sky.catalog",
// 		"com.sky.transaction",
// 		"com.sky.core"
// })
// @EnableJpaRepositories(basePackages = {
// 		"com.sky.tenant.repository",
// 		"com.sky.catalog.repository",
// 		"com.sky.transaction.repository",
// 		"com.sky.core.repository"
// })
// @EntityScan(basePackages = "com.sky.tenant.entity")
public class BusinessSaasPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(BusinessSaasPlatformApplication.class, args);
	}

}
