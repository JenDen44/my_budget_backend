package com.melnikov.bulish.my.budget.my_budget_backend.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class MyBudgetBeanConfiguration {

        @Bean
        public ModelMapper modelMapper() {
            return new ModelMapper();
     }
}