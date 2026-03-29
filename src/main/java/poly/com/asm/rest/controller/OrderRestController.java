package poly.com.asm.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import poly.com.asm.entity.Order;
import poly.com.asm.service.OrderService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/orders")
public class OrderRestController {

    @Autowired
    private OrderService orderService;

    // ✅ 1. Lấy danh sách đơn hàng
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.findAll();
    }

    // ✅ 2. Cập nhật trạng thái đơn hàng
    @PutMapping("/{id}/status")
    public Order updateStatus(@PathVariable("id") Long id,
                              @RequestBody Map<String, Integer> body) {

        Order order = orderService.findById(id);
        if (order == null) {
            throw new RuntimeException("Không tìm thấy đơn hàng");
        }

        Integer newStatus = body.get("status");
        if (newStatus == null) {
            throw new RuntimeException("Thiếu status");
        }

        Integer currentStatus = order.getStatus();

        // ❌ Không cho update nếu đã hoàn tất
        if (currentStatus != null && currentStatus == 3) {
            throw new RuntimeException("Đơn hàng đã hoàn tất");
        }

        // 🔒 Validate luồng
        if (!isValidTransition(currentStatus, newStatus)) {
            throw new RuntimeException("Không thể cập nhật trạng thái không hợp lệ");
        }

        order.setStatus(newStatus);
        return orderService.save(order);
    }

    // ✅ 3. Cập nhật trạng thái thanh toán (FIX CHÍNH Ở ĐÂY)
    @PutMapping("/{id}/payment")
    public Order updatePayment(@PathVariable("id") Long id,
                               @RequestBody Map<String, String> body) {

        Order order = orderService.findById(id);
        if (order == null) {
            throw new RuntimeException("Không tìm thấy đơn hàng");
        }

        String paymentStatus = body.get("paymentStatus");
        if (paymentStatus == null) {
            throw new RuntimeException("Thiếu paymentStatus");
        }

        // Chuẩn hóa giá trị
        paymentStatus = paymentStatus.toUpperCase();

        if (!paymentStatus.equals("PAID") &&
            !paymentStatus.equals("UNPAID") &&
            !paymentStatus.equals("FAILED")) {
            throw new RuntimeException("Giá trị paymentStatus không hợp lệ");
        }

        // ✅ Update payment
        order.setPaymentStatus(paymentStatus);

        // 🔥 QUAN TRỌNG: auto cập nhật status theo payment
        if (paymentStatus.equals("PAID")) {
            if (order.getStatus() == null || order.getStatus() == 0) {
                order.setStatus(1); // chuyển sang xác nhận
            }
        }

        return orderService.save(order);
    }

    // ✅ 4. API gộp (dễ test hơn, khuyên dùng)
    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Long id,
                             @RequestBody Map<String, Object> body) {

        Order order = orderService.findById(id);
        if (order == null) {
            throw new RuntimeException("Không tìm thấy đơn hàng");
        }

        // update status
        if (body.containsKey("status")) {
            Integer status = (Integer) body.get("status");

            if (!isValidTransition(order.getStatus(), status)) {
                throw new RuntimeException("Trạng thái không hợp lệ");
            }

            order.setStatus(status);
        }

        // update payment
        if (body.containsKey("paymentStatus")) {
            String paymentStatus = body.get("paymentStatus").toString().toUpperCase();

            order.setPaymentStatus(paymentStatus);

            if (paymentStatus.equals("PAID") &&
                (order.getStatus() == null || order.getStatus() == 0)) {
                order.setStatus(1);
            }
        }

        return orderService.save(order);
    }

    // 🔒 Validate luồng trạng thái
    private boolean isValidTransition(Integer current, Integer next) {
        if (current == null) current = 0;

        return switch (current) {
            case 0 -> next == 1;
            case 1 -> next == 2;
            case 2 -> next == 3;
            default -> false;
        };
    }
}