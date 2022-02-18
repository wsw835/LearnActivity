package com.wsw.actlearn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan(value = {"com.wsw.actlearn.*"})
@EnableTransactionManagement
public class ActlearnApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActlearnApplication.class, args);
    }

}
