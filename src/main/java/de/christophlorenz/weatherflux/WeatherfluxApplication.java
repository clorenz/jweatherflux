package de.christophlorenz.weatherflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.influx.InfluxDbAutoConfiguration;

@SpringBootApplication
public class WeatherfluxApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherfluxApplication.class, args);
	}

}
