package site.mokaform.surveyserver.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI(@Value("${springdoc.version}") String appVersion) {
        Info info = new Info().title("Demo API").version(appVersion)
                .description("Team MOKA의 mokaform survey service API입니다.");

        return new OpenAPI()
                .info(info);
    }
}
