package poly.com.asm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
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
        // 1. Interceptor toàn cục: Chạy cho tất cả mọi request
        registry.addInterceptor(globalInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**", "/images/**", "/assets/**"); 

        // 2. Interceptor bảo mật (Bác bảo vệ): Kiểm tra đăng nhập/phân quyền
        registry.addInterceptor(authInterceptor)
                .addPathPatterns(
                    // Bảo vệ các trang HTML (MVC cũ)
                    "/order/**", 
                    "/account/edit-profile", 
                    "/account/change-password", 
                    "/admin/**",
                    
                    // Bảo vệ các REST API (Quan trọng cho Giai đoạn 2)
                    "/api/admin/**",
                    "/api/account/**",       // Bảo vệ API Profile/Đổi mật khẩu của Tân
                    "/api/orders/checkout"   // Bảo vệ API Thanh toán của Thiện
                )
                .excludePathPatterns(
                    // Các API cho phép truy cập tự do không cần login
                    "/auth/**", 
                    "/api/auth/**",     
                    "/api/products/**", 
                    "/api/cart/**",     
                    "/api/categories/**",
                    "/static/**", 
                    "/images/**",
                    "/assets/**"
                );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Cho phép Frontend (VueJS) gọi vào Backend
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:8080", "http://127.0.0.1:5500") // Thêm cổng 5500 nếu Hòa chạy Live Server
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true); 
    }
}