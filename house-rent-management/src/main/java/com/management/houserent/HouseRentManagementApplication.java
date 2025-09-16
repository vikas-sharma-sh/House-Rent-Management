package com.management.houserent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HouseRentManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(HouseRentManagementApplication.class, args);
	}

}
