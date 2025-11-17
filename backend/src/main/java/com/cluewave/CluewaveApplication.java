// ---------------------------------------------------------------------
// MAIN APPLICATION ENTRY POINT
// Bootstraps the Spring Boot application and starts the embedded server.
// Provides the main method required by Spring Boot to launch the backend.
// ---------------------------------------------------------------------

package com.cluewave;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CluewaveApplication {

    public static void main(String[] args) {
        SpringApplication.run(CluewaveApplication.class, args);
    }
}