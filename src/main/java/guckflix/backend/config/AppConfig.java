package guckflix.backend.config;

import guckflix.backend.file.FileUploader;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    /**
     * 영화 비디오 로케일 설정
     * 기본값을 Locale.US로 설정한 것은 서버 실행 환경(KR)이 아니라 US로 동작하도록 만듦
     * postman Accept-language ko / en으로 요청
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.US);
        return localeResolver;
    }

    /**
     * 이미지 캐시를 위한 ETag 설정.
     * 제대로 동작하지 않아 컨트롤러에서 e-tag를 생성하기로 함
     */
//    @Bean
//    public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
//        FilterRegistrationBean<ShallowEtagHeaderFilter> filterRegistrationBean
//                = new FilterRegistrationBean<>( new ShallowEtagHeaderFilter());
//        filterRegistrationBean.addUrlPatterns("/images/**");
//        return filterRegistrationBean;
//    }

    /**
     * 서버에서 eTag를 내려주지만
     * Cache-Control이 no-cache, no-store, max-age=0, must-revalidate라서
     * 요청 시 클라이언트가 If-None-Match를 전송하지 않음..
     */
//    @Bean
//    public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
//        return new ShallowEtagHeaderFilter();
//    }

    /**
     * Spring Data Jpa의 Pageable 사용하는 것 대신 커스텀 페이징 객체 Resolver
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PagingRequestArgumentResolver());
    }

    /**
     * 이미지 업로드 클래스
     */
    @Bean
    public FileUploader fileUploader(){
        return new FileUploader();
    }
}
