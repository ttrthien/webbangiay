package poly.com.asm.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import poly.com.asm.dao.AccountDAO;
import poly.com.asm.entity.Account;
import poly.com.asm.model.LoginRequest;
import java.util.Map;
import java.util.HashMap;

@CrossOrigin(origins = "http://localhost:8080") 
@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    @Autowired AccountDAO accountDao;
    @Autowired HttpSession session;

    // 1. API Đăng nhập: Cấp Session (JSESSIONID)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Account user = accountDao.findById(loginRequest.getUsername()).orElse(null);
        
        Map<String, Object> response = new HashMap<>();
        
        if (user != null && user.getPassword().equals(loginRequest.getPassword())) {
            // Lưu đối tượng user vào Session
            session.setAttribute("user", user);
            
            response.put("success", true);
            response.put("message", "Đăng nhập thành công!");
            response.put("username", user.getUsername());
            response.put("isAdmin", user.getAdmin());
            return ResponseEntity.ok(response);
        }
        
        response.put("success", false);
        response.put("message", "Tài khoản hoặc mật khẩu không chính xác!");
        return ResponseEntity.status(401).body(response);
    }

    // 2. API Kiểm tra trạng thái (Dùng để hiện Profile hoặc Check Session)
    @GetMapping("/check")
    public ResponseEntity<?> check() {
        Account user = (Account) session.getAttribute("user");
        if (user != null) {
            // Trả về user nhưng ẩn mật khẩu để bảo mật
            user.setPassword("******");
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(401).body("{\"message\": \"Chưa đăng nhập\"}");
    }
    
    // 3. API Test Quyền Admin (Dùng để test Case 403 và 200)
    @GetMapping("/admin/test")
    public ResponseEntity<?> testAdmin() {
        Account user = (Account) session.getAttribute("user");
        
        // Trường hợp chưa Login (401)
        if (user == null) {
            return ResponseEntity.status(401).body("{\"message\": \"Vui lòng đăng nhập!\"}");
        }
        
        // Trường hợp đã Login nhưng không phải Admin (403)
        if (!user.getAdmin()) {
            return ResponseEntity.status(403).body("{\"message\": \"Lỗi 403: Bạn không có quyền Admin!\"}");
        }
        
        // Trường hợp đúng Admin (200)
        return ResponseEntity.ok("Chào Admin " + user.getUsername());
    }

    // 4. API Đăng xuất: Hủy Session lập tức
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        session.invalidate(); // Xóa sạch dữ liệu và hủy Session ID trên Server
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Đăng xuất thành công!");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Account account) {
        Map<String, Object> response = new HashMap<>();
        
        if (accountDao.existsById(account.getUsername())) {
            response.put("success", false);
            response.put("message", "Tên đăng nhập đã tồn tại!");
            return ResponseEntity.status(400).body(response);
        }

        account.setAdmin(false);       
        account.setActivated(true);    
        if (account.getPhoto() == null || account.getPhoto().isEmpty()) {
            account.setPhoto("user.png"); 
        }
        
        try {
            accountDao.save(account);
            response.put("success", true);
            response.put("message", "Đăng ký tài khoản thành công!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @PutMapping("/profile/update")
    public ResponseEntity<?> updateProfile(@RequestBody Account data) {
        Account sessionUser = (Account) session.getAttribute("user");
        if (sessionUser == null) return ResponseEntity.status(401).build();

        Account currentUser = accountDao.findById(sessionUser.getUsername()).get();
        
        currentUser.setFullname(data.getFullname());
        currentUser.setEmail(data.getEmail());
        
        accountDao.save(currentUser);
        session.setAttribute("user", currentUser);
        
        return ResponseEntity.ok("{\"success\": true, \"message\": \"Cập nhật thành công!\"}");
    }
}