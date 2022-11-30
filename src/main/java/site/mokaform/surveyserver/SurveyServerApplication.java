package site.mokaform.surveyserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SurveyServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SurveyServerApplication.class, args);
    }

}
