package ru.epta.mtplanner.meeting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan({"ru.epta"})
@EnableJpaRepositories("ru.epta")
@ComponentScan("ru.epta")
@EnableFeignClients
@SpringBootApplication
public class MeetingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MeetingServiceApplication.class, args);
	}

}
