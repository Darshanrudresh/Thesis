package com.advantest.myapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * This is the main class.
 *
 * 
 * @author darshan.rudresh
 */
@SpringBootApplication
@EnableCaching
public class MyapplicationApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyapplicationApplication.class, args);

    }

}
