package poly.com.asm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/account")
public class AccountAController {

    @RequestMapping("/index")
    public String index(Model model) {
        // Không cần gọi accountService.findAll() ở đây nữa
        // vì JS (Axios) trong file account.html sẽ tự động tải dữ liệu qua REST API.
        
        model.addAttribute("view", "admin/account.html");
        return "layout/index";
    }
    
    // ĐÃ XÓA: phương thức updateRole() và delete()
    // Lý do: Các chức năng này hiện tại đã được xử lý bởi AccountRestController 
    // (/api/admin/accounts) để đảm bảo trang không bị giật/reload khi bấm nút.
}