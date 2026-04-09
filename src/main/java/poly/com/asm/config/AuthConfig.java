package poly.com.asm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import poly.com.asm.interceptor.AuthInterceptor;
import poly.com.asm.interceptor.GlobalInterceptor;

@Configuration
public class AuthConfig implements WebMvcConfigurer {
    @Autowired AuthInterceptor authInterceptor;
    @Autowired GlobalInterceptor globalInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Interceptor toàn cục
        registry.addInterceptor(globalInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**", "/images/**"); 

        // Interceptor bảo mật (Bác bảo vệ)
        registry.addInterceptor(authInterceptor)
                .addPathPatterns(
                    // Bảo vệ các trang HTML cũ
                    "/order/**", 
                    "/account/edit-profile", 
                    "/account/change-password", 
                    "/admin/**",
                    
                    // Bảo vệ các API mới
                    "/api/admin/**",
                    "/api/account/**",       // <-- Bảo vệ 2 API của TÂN ở đây
                    "/api/orders/checkout"   // <-- Bảo vệ API của Thiện
                )
                .excludePathPatterns(
                    // Những API mở cửa tự do
                    "/auth/**", 
                    "/api/auth/**",     // Cho phép Login/Register tự do
                    "/api/products/**", // Cho phép khách xem sản phẩm tự do
                    "/api/cart/**",     // Cho phép khách thao tác giỏ hàng tự do
                    "/static/**", 
                    "/images/**"
                );
    }
    
    // Giấy phép thông hành cho VueJS của team trưởng
    @Override
    public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:8080") // Chỉ cho phép VueJS chạy ở port 8080 gọi vào
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true); 
    }
}