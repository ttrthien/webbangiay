package poly.com.asm.controller;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;

import poly.com.asm.entity.Product;
import poly.com.asm.service.ProductService;
import poly.com.asm.service.CategoryService;

@Controller
public class ProductController {
    @Autowired
    ProductService productService;
    
    @Autowired
    CategoryService categoryService;

    // 1. XỬ LÝ CHI TIẾT SẢN PHẨM
    @RequestMapping("/product/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, 
                         @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {
        
        Product item = productService.findById(id);
        model.addAttribute("item", item);
        
        if ("XMLHttpRequest".equals(requestedWith)) {
            return "product/product-detail"; // Trả về mảnh HTML chi tiết
        }
        
        model.addAttribute("view", "product/product-detail.html");
        return "layout/index";
    }

    // 2. XỬ LÝ LỌC THEO DANH MỤC (SỬA LỖI 404 CHO THIỆN)
    @RequestMapping("/product/list-by-category/{cid}")
    public String listByCategory(Model model, @PathVariable("cid") String cid, 
                                 @RequestParam("p") Optional<Integer> p,
                                 @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {
        
        Pageable pageable = PageRequest.of(p.orElse(0), 8);
        Page<Product> page = productService.findByCategoryId(cid, pageable);
        
        model.addAttribute("page", page);
        model.addAttribute("categories", categoryService.findAll());
        
        // Nếu là AJAX (Axios gọi), chỉ trả về mảnh danh sách
        if ("XMLHttpRequest".equals(requestedWith)) {
            return "product/product-list"; 
        }
        
        // Nếu người dùng load trang trực tiếp
        model.addAttribute("view", "product/product-list.html");
        return "layout/index";
    }
}