package poly.com.asm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import poly.com.asm.entity.Order;
import poly.com.asm.service.OrderService;

@Controller
@RequestMapping("/admin/order")
public class OrderAController {

    @Autowired
    OrderService orderService;

    @RequestMapping("/index")
    public String index(Model model) {
        model.addAttribute("orders", orderService.findAll());
        model.addAttribute("view", "admin/order.html");
        return "layout/index";
    }

    @RequestMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        model.addAttribute("order", orderService.findById(id));
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
                // Dùng update thay vì create để rõ nghĩa (hoặc save tùy service)
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