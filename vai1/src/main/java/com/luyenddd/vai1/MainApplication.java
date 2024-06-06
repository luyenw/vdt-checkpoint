package com.luyenddd.vai1;

import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLOutput;

@SpringBootApplication
public class MainApplication {
	public static void main(String[] args) {
		int numberOfCores = Runtime.getRuntime().availableProcessors();
		System.out.println(numberOfCores);
		SpringApplication.run(MainApplication.class, args);
	}
}
