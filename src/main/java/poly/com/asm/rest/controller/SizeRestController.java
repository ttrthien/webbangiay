package poly.com.asm.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poly.com.asm.entity.Size;
import poly.com.asm.service.SizeService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/sizes")
public class SizeRestController {

    @Autowired
    SizeService sizeService; // Gọi đến Service quản lý Size của bạn

    @GetMapping
    public List<Size> getAll() {
        // Trả về toàn bộ danh sách Size dưới dạng JSON
        return sizeService.findAll();
    }
}