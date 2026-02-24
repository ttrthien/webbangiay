package poly.com.asm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.com.asm.entity.Product;
import poly.com.asm.service.*;

@Controller
@RequestMapping("/admin/product")
public class ProductAController {
    @Autowired ProductService productService;
    @Autowired CategoryService categoryService;
    @Autowired ParamService paramService;
    @Autowired SizeService sizeService; 

    @RequestMapping("/index")
    public String index(Model model) {
        if (!model.containsAttribute("item")) {
            model.addAttribute("item", new Product());
        }
        model.addAttribute("items", productService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("allSizes", sizeService.findAll()); 
        model.addAttribute("view", "admin/product.html");
        return "layout/index";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("item") Product item, 
                       @RequestParam("photo_file") MultipartFile file, 
                       RedirectAttributes params) {
        String msg = (item.getId() != null) ? "Cập nhật sản phẩm thành công!" : "Thêm mới sản phẩm thành công!";
        
        // XỬ LÝ ẢNH - Đã sửa lỗi đường dẫn
        if (!file.isEmpty()) {
            // Chỉ truyền "images", không truyền "/images/" để tránh lỗi Disk Write
            paramService.save(file, "images"); 
            item.setImage(file.getOriginalFilename());
        } else if (item.getId() != null) {
            // Giữ lại tên ảnh cũ từ database nếu người dùng không chọn file mới
            Product oldProduct = productService.findById(item.getId());
            item.setImage(oldProduct.getImage());
        }

        // LƯU SẢN PHẨM
        if (item.getId() != null) {
            productService.update(item);
        } else {
            productService.create(item);
        }
        
        params.addFlashAttribute("message", msg);
        return "redirect:/admin/product/index";
    }

    @RequestMapping("/edit/{id}")
    public String edit(Model model, @PathVariable("id") Integer id) {
        Product p = productService.findById(id);
        model.addAttribute("item", p);
        model.addAttribute("items", productService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("allSizes", sizeService.findAll()); 
        model.addAttribute("view", "admin/product.html");
        return "layout/index";
    }

    @RequestMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes params) {
        try {
            productService.delete(id);
            params.addFlashAttribute("message", "Xóa sản phẩm thành công!");
        } catch (Exception e) {
            params.addFlashAttribute("message", "Không thể xóa sản phẩm này vì đã có trong đơn hàng!");
        }
        return "redirect:/admin/product/index";
    }
}