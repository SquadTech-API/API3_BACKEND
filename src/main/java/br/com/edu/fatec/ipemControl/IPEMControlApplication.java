package br.com.edu.fatec.ipemControl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IPEMControlApplication {

    public static void main(String[] args) {
        SpringApplication.run(IPEMControlApplication.class, args);
    }

}
