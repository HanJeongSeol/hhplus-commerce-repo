package kr.hhplus.be.server.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("e-커머스 서비스 API 명세서")
                        .version("v1.0")
                        .description("항해 플러스 e-커머스 API 명세서입니다.")
                );
    }
}
