package site.mokaform.surveyserver.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
                .components(new Components()
                        .addSecuritySchemes("Access Token", getAccessTokenAuthScheme())
                        .addSecuritySchemes("Refresh Token", getRefreshTokenAuthScheme()))
                .addSecurityItem(getSecurityItem())
                .info(info);
    }

    private SecurityScheme getAccessTokenAuthScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");
    }

    private SecurityScheme getRefreshTokenAuthScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .name("refreshToken");
    }

    private SecurityRequirement getSecurityItem() {
        SecurityRequirement securityItem = new SecurityRequirement();
        securityItem.addList("Access Token");
        securityItem.addList("Refresh Token");
        return securityItem;
    }
}
