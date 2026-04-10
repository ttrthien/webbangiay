package poly.com.asm.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.com.asm.entity.Order;
import poly.com.asm.service.OrderService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderRestController {

    @Autowired
    OrderService orderService;

    // 1. Lấy danh sách toàn bộ đơn hàng
    @GetMapping
    public List<Order> getAll() {
        return orderService.findAll();
    }

    // 2. Lấy thông tin 1 đơn hàng (bao gồm cả OrderDetails)
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOne(@PathVariable("id") Long id) {
        Order order = orderService.findById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

    // 3. Cập nhật trạng thái đơn hàng
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable("id") Long id, 
            @RequestBody Map<String, Integer> body) {
        Order order = orderService.findById(id);
        if (order == null) return ResponseEntity.notFound().build();
        
        Integer newStatus = body.get("status");
        order.setStatus(newStatus);
        orderService.update(order);
        
        return ResponseEntity.ok(order);
    }

    // 4. Xóa đơn hàng
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        try {
            orderService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}