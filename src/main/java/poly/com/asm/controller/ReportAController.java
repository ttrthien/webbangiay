package poly.com.asm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;
import poly.com.asm.dao.OrderDetailDAO;

@Controller
@RequestMapping("/admin/report")
public class ReportAController {
    @Autowired
    OrderDetailDAO dao;

    @RequestMapping("/revenue")
    public String revenue(Model model, HttpServletRequest request) {
        model.addAttribute("items", dao.getRevenueByCategory());
        // Nếu là AJAX, trả về file con. Nếu không, trả về layout chính.
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "admin/report/revenue"; 
        }
        model.addAttribute("view", "admin/report/revenue.html");
        return "layout/index";
    }

    @RequestMapping("/vip")
    public String vip(Model model, HttpServletRequest request) {
        model.addAttribute("items", dao.getTop10VIP(PageRequest.of(0, 10)));
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "admin/report/vip";
        }
        model.addAttribute("view", "admin/report/vip.html");
        return "layout/index";
    }
}