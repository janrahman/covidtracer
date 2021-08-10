package de.hhu.covidtracer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class CovidTracerApplication implements ApplicationRunner {
    @Value("${server.address}")
    private String serverAddress;

    @Value("${server.port}")
    private int serverPort;


    public static void main(String[] args) {
        SpringApplication.run(CovidTracerApplication.class, args);
    }


    @Override
    public void run(ApplicationArguments args) {
        log.info("Application is now ready to use ...");
        log.info("Control + C to exit.");
        log.info("Accessible via: http://" + serverAddress + ":" + serverPort);
    }
}
