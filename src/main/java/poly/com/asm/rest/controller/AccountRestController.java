package poly.com.asm.rest.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import poly.com.asm.dao.AccountDAO;
import poly.com.asm.entity.Account;
import poly.com.asm.model.ChangePasswordRequest;
import poly.com.asm.model.ProfileUpdateRequest;

//@CrossOrigin("*") 
@RestController
@RequestMapping("/api/account")
public class AccountRestController {

    @Autowired HttpSession session;
    @Autowired AccountDAO accountDao;

    // 1. API Sửa thông tin cá nhân
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody ProfileUpdateRequest request) {
        Account currentUser = (Account) session.getAttribute("user");
        
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Vui lòng đăng nhập!"));
        }

        // Cập nhật dữ liệu
        currentUser.setFullname(request.getFullname());
        currentUser.setEmail(request.getEmail());
        if (request.getPhoto() != null && !request.getPhoto().isEmpty()) {
            currentUser.setPhoto(request.getPhoto());
        }

        try {
            accountDao.save(currentUser);
            session.setAttribute("user", currentUser); // Làm mới thẻ thông hành
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật thông tin thành công!");
            response.put("user", currentUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Lỗi cập nhật dữ liệu!"));
        }
    }

    // 2. API Đổi mật khẩu
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        Account currentUser = (Account) session.getAttribute("user");
        
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Vui lòng đăng nhập!"));
        }

        if (!currentUser.getPassword().equals(request.getOldPassword())) {
            return ResponseEntity.status(400).body(Map.of("message", "Mật khẩu cũ không chính xác!"));
        }

        try {
            currentUser.setPassword(request.getNewPassword());
            accountDao.save(currentUser);
            
            // Xóa session, ép người dùng phải đăng nhập lại với pass mới
            session.invalidate(); 
            
            return ResponseEntity.ok(Map.of("success", true, "message", "Đổi mật khẩu thành công! Vui lòng đăng nhập lại."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Lỗi đổi mật khẩu!"));
        }
    }
}