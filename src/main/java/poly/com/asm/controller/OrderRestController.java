package poly.com.asm.controller;

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

	// ✅ 1. Lấy danh sách đơn hàng mới nhất
	@GetMapping
	public List<Order> getAllOrders() {
		return orderService.findAll();
	}

	@PutMapping("/{id}/status")
	public Order updateStatus(@PathVariable("id") Long id, @RequestBody Map<String, Integer> body) {
		Order order = orderService.findById(id);

		if (order == null) {
			throw new RuntimeException("Không tìm thấy đơn hàng");
		}

		Integer newStatus = body.get("status");

		if (newStatus == null) {
			throw new RuntimeException("Thiếu status");
		}

		Integer currentStatus = order.getStatus();

		if (currentStatus != null && currentStatus == 3) {
			throw new RuntimeException("Đơn hàng đã hoàn tất");
		}

		if (!isValidTransition(currentStatus, newStatus)) {
			throw new RuntimeException("Không thể cập nhật trạng thái không hợp lệ");
		}

		order.setStatus(newStatus);
		return orderService.save(order);
	}

	private boolean isValidTransition(Integer current, Integer next) {
		if (current == null)
			current = 0;

		return switch (current) {
		case 0 -> next == 1;
		case 1 -> next == 2;
		case 2 -> next == 3;
		default -> false;
		};
	}
}
