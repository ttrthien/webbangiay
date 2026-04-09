package poly.com.asm.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import poly.com.asm.entity.Product;
import poly.com.asm.service.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
public class CrudProductRestController {

    @Autowired ProductService productService;
    @Autowired CategoryService categoryService;
    @Autowired ParamService paramService;

    @GetMapping
    public List<Product> getAll() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getOne(@PathVariable("id") Integer id) {
        Product p = productService.findById(id);
        return p != null ? ResponseEntity.ok(p) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Product> create(
            @ModelAttribute Product product, 
            @RequestParam(value = "photo_file", required = false) MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            paramService.save(file, "images");
            product.setImage(file.getOriginalFilename());
        }
        return ResponseEntity.ok(productService.create(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(
            @PathVariable("id") Integer id,
            @ModelAttribute Product product,
            @RequestParam(value = "photo_file", required = false) MultipartFile file) {
        
        Product oldProduct = productService.findById(id);
        if (oldProduct == null) return ResponseEntity.notFound().build();

        if (file != null && !file.isEmpty()) {
            paramService.save(file, "images");
            product.setImage(file.getOriginalFilename());
        } else {
            product.setImage(oldProduct.getImage());
        }
        
        product.setId(id);
        return ResponseEntity.ok(productService.update(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        try {
            productService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}