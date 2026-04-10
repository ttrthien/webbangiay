package poly.com.asm.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.com.asm.entity.Account;
import poly.com.asm.service.AccountService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/accounts")
public class AccountManagerRestController {

    @Autowired
    AccountService accountService;

    // 1. Lấy danh sách tất cả tài khoản
    @GetMapping
    public List<Account> getAll() {
        return accountService.findAll();
    }

    // 2. Cập nhật quyền Admin (Cấp / Hủy)
    @PutMapping("/{username}/role")
    public ResponseEntity<?> updateRole(
            @PathVariable("username") String username, 
            @RequestBody Map<String, Boolean> body) {
        
        Account account = accountService.findById(username);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Cập nhật trạng thái admin từ JSON body
        Boolean isAdmin = body.get("admin");
        account.setAdmin(isAdmin);
        accountService.update(account);
        
        return ResponseEntity.ok(account);
    }

    // 3. Xóa tài khoản
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> delete(@PathVariable("username") String username) {
        try {
            accountService.delete(username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // Sẽ quăng lỗi nếu tài khoản đã có đơn hàng (dính khóa ngoại)
            return ResponseEntity.badRequest().build();
        }
    }
}