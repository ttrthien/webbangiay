package poly.com.asm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest; // Thêm import này
import poly.com.asm.entity.Product;
import poly.com.asm.service.*;

@Controller
@RequestMapping("/admin/product")
public class ProductAController {
    @Autowired ProductService productService;
    @Autowired CategoryService categoryService;
    @Autowired ParamService paramService;
    @Autowired SizeService sizeService; 

    // Hàm phụ trợ kiểm tra AJAX
    private boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    @RequestMapping("/index")
    public String index(Model model, HttpServletRequest request) {
        if (!model.containsAttribute("item")) {
            model.addAttribute("item", new Product());
        }
        model.addAttribute("items", productService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("allSizes", sizeService.findAll()); 
        
        // NẾU LÀ AJAX: Chỉ trả về file sản phẩm (phần ruột)
        if (isAjax(request)) {
            return "admin/product"; 
        }

        // NẾU KHÔNG (F5): Trả về layout tổng
        model.addAttribute("view", "admin/product.html");
        return "layout/index";
    }

    @RequestMapping("/edit/{id}")
    public String edit(Model model, @PathVariable("id") Integer id, HttpServletRequest request) {
        Product p = productService.findById(id);
        model.addAttribute("item", p);
        model.addAttribute("items", productService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("allSizes", sizeService.findAll()); 
        
        // Tương tự cho hàm edit
        if (isAjax(request)) {
            return "admin/product";
        }

        model.addAttribute("view", "admin/product.html");
        return "layout/index";
    }

    // Các hàm save và delete dùng redirect nên không cần sửa 
    // vì redirect sẽ quay lại hàm index ở trên
}