package kuit.project.beering;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BeeringApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeeringApplication.class, args);
	}

}
