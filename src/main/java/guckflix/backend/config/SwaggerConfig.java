package guckflix.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * API 문서 Swagger 설정
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private static final String API_NAME = "guckflix API";
    private static final String API_VERSION = "0.0.1";
    private static final String API_DESCRIPTION = "guckflix API docs";

    /**
     * .ignoredParameterTypes() : Controller 메서드에 파라미터를 넣는 경우 모두 포함되어버리므로 제외할 클래스, 어노테이션을 설정 가능
     * .paths() : 어떤 패키지를 보여줄 것인가 설정
     */
    @Bean
    public Docket restAPI() {
        return new Docket(DocumentationType.SWAGGER_2)
                .ignoredParameterTypes(AuthenticationPrincipal.class)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("guckflix.backend"))
                .paths(PathSelectors.any())
                .build();
    }

    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(API_NAME)
                .version(API_VERSION)
                .description(API_DESCRIPTION)
                .build();
    }
}
