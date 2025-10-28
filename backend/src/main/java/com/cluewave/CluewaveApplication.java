package com.cluewave;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Cluewave backend. This class boots the Spring context
 * and starts the embedded web server on the port provided by Heroku (via the
 * PORT environment variable) or defaulting to 8080 when run locally.
 */
@SpringBootApplication
public class CluewaveApplication {

    public static void main(String[] args) {
        SpringApplication.run(CluewaveApplication.class, args);
    }
}