package io.grant.coronaviruslivetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // To enable loading the fetchVirusData() method first hour of every day
public class CoronavirusLivetrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoronavirusLivetrackerApplication.class, args);
	}

}
