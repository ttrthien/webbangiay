package poly.com.asm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest; // Import quan trọng
import poly.com.asm.entity.Order;
import poly.com.asm.service.OrderService;

@Controller
@RequestMapping("/admin/order")
public class OrderAController {

    @Autowired
    OrderService orderService;

    // Hàm phụ trợ kiểm tra request từ AJAX (Axios)
    private boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    @RequestMapping("/index")
    public String index(Model model, HttpServletRequest request) {
        model.addAttribute("orders", orderService.findAll());
        
        // NẾU LÀ AJAX: Chỉ trả về file fragment admin/order.html
        if (isAjax(request)) {
            return "admin/order"; 
        }

        // NẾU KHÔNG (F5): Trả về layout tổng
        model.addAttribute("view", "admin/order.html");
        return "layout/index";
    }

    @RequestMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        model.addAttribute("order", orderService.findById(id));
        
        // NẾU LÀ AJAX: Chỉ trả về file fragment admin/order-detail.html
        if (isAjax(request)) {
            return "admin/order-detail"; 
        }

        model.addAttribute("view", "admin/order-detail.html");
        return "layout/index";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam("id") Long id, @RequestParam("status") Integer status,
            RedirectAttributes params) {
        try {
            Order order = orderService.findById(id);
            if (order != null) {
                order.setStatus(status);
                orderService.create(order); 
                
                String sttText = switch (status) {
                    case 0 -> "Mới";
                    case 1 -> "Đã xác nhận";
                    case 2 -> "Đang giao";
                    case 3 -> "Hoàn tất";
                    case 4 -> "Đã hủy";
                    default -> "Khác";
                };
                params.addFlashAttribute("message", "Đơn hàng #" + id + " -> " + sttText);
            }
        } catch (Exception e) {
            params.addFlashAttribute("message", "Lỗi cập nhật trạng thái đơn hàng!");
        }
        // Redirect sẽ quay lại hàm index bên trên, index sẽ tự xử lý trả về fragment hay layout
        return "redirect:/admin/order/index";
    }

    @RequestMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes params) {
        try {
            orderService.delete(id);
            params.addFlashAttribute("message", "Đã xóa đơn hàng #" + id + " thành công!");
        } catch (Exception e) {
            params.addFlashAttribute("message", "Không thể xóa đơn hàng này (Lỗi ràng buộc dữ liệu)!");
        }
        return "redirect:/admin/order/index";
    }
}