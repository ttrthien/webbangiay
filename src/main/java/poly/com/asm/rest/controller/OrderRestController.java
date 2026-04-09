package poly.com.asm.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import poly.com.asm.config.Config;
import poly.com.asm.entity.Account;
import poly.com.asm.entity.Order;
import poly.com.asm.service.CartService;
import poly.com.asm.service.OrderService;
import poly.com.asm.service.VNPayService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
// @CrossOrigin("*")
@RequestMapping("/api/orders") // Đổi gốc thành /api/orders để dùng chung cho cả User và Admin
public class OrderRestController {

    @Autowired private OrderService orderService;
    @Autowired private VNPayService vnpayService;
    @Autowired private CartService cartService;
    @Autowired private HttpSession session;

    // --- PHẦN CỦA THIỆN: CHỐT ĐƠN & THANH TOÁN (CLIENT) ---

    @PostMapping("/checkout") // Đường dẫn đầy đủ: /api/orders/checkout
    public ResponseEntity<?> checkout(@RequestBody Order orderData, HttpServletRequest request) {
        Account user = (Account) session.getAttribute("user");
        
        if (user == null) {
            return ResponseEntity.status(401).body("{\"message\": \"Vui lòng đăng nhập để thanh toán!\"}");
        }
        if (cartService.getCount() == 0) {
            return ResponseEntity.badRequest().body("{\"message\": \"Giỏ hàng của bạn đang trống!\"}");
        }

        try {
            // 1. Lưu đơn hàng vào Database
            orderData.setAccount(user);
            orderData.setCreateDate(new Date());
            orderData.setStatus(0); // 0: Mới tạo (Chờ thanh toán)
            orderData.setPaymentStatus("UNPAID");
            Order savedOrder = orderService.save(orderData);

            // 2. Tạo URL thanh toán VNPay
            String ipAddress = Config.getIpAddress(request);
            String orderInfo = "Thanh toan don hang #" + savedOrder.getId();
            long amount = (long) cartService.getAmount();

            String vnpayUrl = vnpayService.createPaymentUrl(amount, orderInfo, ipAddress);
            
            // Lưu ID đơn hàng vào session để đối soát khi vnpay-return gọi về
            session.setAttribute("pendingOrderId", savedOrder.getId());

            // 3. Trả về link cho Hòa xử lý Axios
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("vnpayUrl", vnpayUrl);
            response.put("orderId", savedOrder.getId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"message\": \"Lỗi xử lý đơn hàng: " + e.getMessage() + "\"}");
        }
    }


    // --- PHẦN QUẢN TRỊ (ADMIN) ---

    @GetMapping("/admin/all") // Đường dẫn: /api/orders/admin/all
    public List<Order> getAllOrders() {
        return orderService.findAll();
    }

    @PutMapping("/admin/{id}/status")
    public Order updateStatus(@PathVariable("id") Long id, @RequestBody Map<String, Integer> body) {
        Order order = orderService.findById(id);
        if (order == null) throw new RuntimeException("Không tìm thấy đơn hàng");

        Integer newStatus = body.get("status");
        if (!isValidTransition(order.getStatus(), newStatus)) {
            throw new RuntimeException("Chuyển đổi trạng thái không hợp lệ");
        }

        order.setStatus(newStatus);
        return orderService.save(order);
    }

    @PutMapping("/admin/{id}/payment")
    public Order updatePayment(@PathVariable("id") Long id, @RequestBody Map<String, String> body) {
        Order order = orderService.findById(id);
        if (order == null) throw new RuntimeException("Không tìm thấy đơn hàng");

        String paymentStatus = body.get("paymentStatus").toUpperCase();
        order.setPaymentStatus(paymentStatus);

        // Nếu đã trả tiền mà trạng thái là "Chờ (0)" thì tự động nhảy sang "Xác nhận (1)"
        if (paymentStatus.equals("PAID") && (order.getStatus() == null || order.getStatus() == 0)) {
            order.setStatus(1);
        }

        return orderService.save(order);
    }

    private boolean isValidTransition(Integer current, Integer next) {
        if (current == null) current = 0; 

        return switch (current) {
            case 0 -> (next == 1 || next == 4);
            case 1 -> (next == 2 || next == 4); 
            case 2 -> (next == 3);              
            case 3, 4 -> false;                
            default -> false;
        };
    }
}