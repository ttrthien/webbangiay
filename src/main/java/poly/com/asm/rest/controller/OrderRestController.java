package poly.com.asm.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import poly.com.asm.config.Config;
import poly.com.asm.entity.*;
import poly.com.asm.model.CartItem;
import poly.com.asm.service.*;

import java.util.*;

@RestController
// @CrossOrigin("*")
@RequestMapping("/api/orders") // Đổi gốc thành /api/orders để dùng chung cho cả User và Admin
public class OrderRestController {

    @Autowired private OrderService orderService;
    @Autowired private VNPayService vnpayService;
    @Autowired private CartService cartService;
    @Autowired private ProductService productService; // Cần thêm để tìm sản phẩm
    @Autowired private HttpSession session;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody Order orderData, HttpServletRequest request) {
        Account user = (Account) session.getAttribute("user");
        
        if (user == null) {
            return ResponseEntity.status(401).body("{\"message\": \"Vui lòng đăng nhập để thanh toán!\"}");
        }
        if (cartService.getCount() == 0) {
            return ResponseEntity.badRequest().body("{\"message\": \"Giỏ hàng của bạn đang trống!\"}");
        }

        try {
            // 1. THIẾT LẬP THÔNG TIN CƠ BẢN
            orderData.setAccount(user);
            orderData.setCreateDate(new Date());
            orderData.setStatus(0); // Chờ thanh toán
            orderData.setPaymentStatus("UNPAID");

            // 2. QUAN TRỌNG: ĐỔ HÀNG TỪ GIỎ VÀO ĐƠN HÀNG
            List<OrderDetail> details = new ArrayList<>();
            for (CartItem item : cartService.getItems()) {
                OrderDetail detail = new OrderDetail();
                detail.setOrder(orderData); // Thiết lập quan hệ 2 chiều để JPA lưu được
                detail.setProduct(productService.findById(item.getId())); 
                detail.setPrice(item.getPrice());
                detail.setQuantity(item.getQty());
                detail.setSize(item.getSize());
                details.add(detail);
            }
            orderData.setOrderDetails(details); // Gán danh sách món hàng vào đơn

            // 3. LƯU VÀO DATABASE
            Order savedOrder = orderService.save(orderData);

            // 4. XỬ LÝ THANH TOÁN
            String ipAddress = Config.getIpAddress(request);
            String orderInfo = "Thanh toan don hang #" + savedOrder.getId();
            long amount = (long) cartService.getAmount();

            String vnpayUrl = "";
            if ("VNPAY".equalsIgnoreCase(orderData.getPaymentMethod())) {
                vnpayUrl = vnpayService.createPaymentUrl(amount, orderInfo, ipAddress);
                session.setAttribute("pendingOrderId", savedOrder.getId());
            } else {
                // Nếu là COD, xác nhận đơn luôn và xóa giỏ
                savedOrder.setStatus(1); 
                orderService.save(savedOrder);
                cartService.clear();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("vnpayUrl", vnpayUrl);
            response.put("orderId", savedOrder.getId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("{\"message\": \"Lỗi xử lý đơn hàng: " + e.getMessage() + "\"}");
        }
    }

    // --- CÁC HÀM QUẢN TRỊ ---
    @GetMapping("/admin/all")
    public List<Order> getAllOrders() {
        return orderService.findAll();
    }

    @PutMapping("/admin/{id}/status")
    public Order updateStatus(@PathVariable("id") Long id, @RequestBody Map<String, Integer> body) {
        Order order = orderService.findById(id);
        if (order == null) throw new RuntimeException("Không tìm thấy đơn hàng");
        order.setStatus(body.get("status"));
        return orderService.save(order);
    }
}