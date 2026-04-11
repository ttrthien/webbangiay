package poly.com.asm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/account")
public class AccountAController {

    @RequestMapping("/index")
    public String index(Model model, 
                        @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {
        
        // 1. Nếu là yêu cầu từ Axios (AJAX) - Chỉ trả về cái "ruột" (fragment)
        if ("XMLHttpRequest".equals(requestedWith)) {
            return "admin/account"; // Không có đuôi .html và KHÔNG return layout/index
        }
        
        // 2. Nếu người dùng gõ trực tiếp link lên thanh địa chỉ (Full Load)
        model.addAttribute("view", "admin/account.html");
        return "layout/index";
    }
}