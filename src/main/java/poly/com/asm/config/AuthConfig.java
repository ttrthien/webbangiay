package poly.com.asm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import poly.com.asm.interceptor.AuthInterceptor;
import poly.com.asm.interceptor.GlobalInterceptor;

@Configuration
public class AuthConfig implements WebMvcConfigurer {
    @Autowired
    AuthInterceptor authInterceptor;

    @Autowired
    GlobalInterceptor globalInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(globalInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**", "/images/**", "/api/**"); 

        registry.addInterceptor(authInterceptor)
                .addPathPatterns(
                    "/order/**", 
                    "/account/edit-profile", 
                    "/account/change-password", 
                    "/admin/**" 
                )
                .excludePathPatterns(
                    "/auth/**", 
                    "/api/**", 
                    "/static/**", 
                    "/images/**"
                );
    }
    @Override
    public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:8080") 
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true); 
    }
}