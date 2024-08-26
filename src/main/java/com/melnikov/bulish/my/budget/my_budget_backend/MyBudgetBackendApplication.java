package com.melnikov.bulish.my.budget.my_budget_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MyBudgetBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyBudgetBackendApplication.class, args);
	}

}
