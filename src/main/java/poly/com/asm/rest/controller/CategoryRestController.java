package poly.com.asm.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.com.asm.entity.Category;
import poly.com.asm.service.CategoryService;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/admin/categories")
public class CategoryRestController {

    @Autowired
    CategoryService categoryService;

    @GetMapping
    public List<Category> getAll() {
        return categoryService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getOne(@PathVariable("id") String id) {
        if (categoryService.findById(id) == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Category category) {
        if (categoryService.findById(category.getId()) != null) {
            return ResponseEntity.badRequest().body("Mã loại đã tồn tại!");
        }
        return ResponseEntity.ok(categoryService.create(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> update(@PathVariable("id") String id, @RequestBody Category category) {
        if (categoryService.findById(id) == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(categoryService.update(category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        try {
            categoryService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}