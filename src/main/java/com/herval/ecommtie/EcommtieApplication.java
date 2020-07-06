package com.herval.ecommtie;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EcommtieApplication {

    @Bean
    public ModelMapper mapper() {
        return new ModelMapper();
    }

    public static void main(String[] args) {
        SpringApplication.run(EcommtieApplication.class, args);
    }

}
