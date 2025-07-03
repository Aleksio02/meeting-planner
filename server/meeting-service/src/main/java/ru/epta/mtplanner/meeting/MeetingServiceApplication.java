package ru.epta.mtplanner.meeting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan({"ru.epta.commons.dao.dto", "ru.epta.mtplanner.meeting.dao.dto"})
@EnableJpaRepositories("ru.epta")
@ComponentScan("ru.epta")
@SpringBootApplication
public class MeetingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MeetingServiceApplication.class, args);
	}

}
