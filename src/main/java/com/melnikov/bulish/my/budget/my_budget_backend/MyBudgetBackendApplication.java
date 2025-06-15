package com.melnikov.bulish.my.budget.my_budget_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.DispatcherServlet;

@EnableScheduling
@SpringBootApplication
public class MyBudgetBackendApplication {

	public static void main(String[] args) {
		var ctx = SpringApplication.run(MyBudgetBackendApplication.class, args);
		var dispatcherServlet = (DispatcherServlet)ctx.getBean("dispatcherServlet");

		dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
	}
}
