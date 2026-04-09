package poly.com.asm.rest.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import poly.com.asm.model.CartItem;
import poly.com.asm.service.CartService;


@RestController
@RequestMapping("/api/cart")
public class CartRestController {

    @Autowired
    CartService cartService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCart() {
        Collection<CartItem> items = cartService.getItems();
        Map<String, Object> response = new HashMap<>();
        response.put("items", items);
        response.put("count", cartService.getCount());
        response.put("amount", cartService.getAmount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getCartInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("count", cartService.getCount());
        response.put("amount", cartService.getAmount());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add/{id}")
    public ResponseEntity<Map<String, Object>> addToCart(
            @PathVariable("id") Integer id,
            @RequestParam("size") Integer size) {
        cartService.add(id, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Thêm vào giỏ hàng thành công!");
        response.put("count", cartService.getCount());
        response.put("amount", cartService.getAmount());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateCart(
            @PathVariable("id") Integer id,
            @RequestParam("size") Integer size,
            @RequestParam("qty") Integer qty) {
        cartService.update(id, size, qty);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Cập nhật giỏ hàng thành công!");
        response.put("count", cartService.getCount());
        response.put("amount", cartService.getAmount());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Map<String, Object>> removeFromCart(
            @PathVariable("id") Integer id,
            @RequestParam("size") Integer size) {
        cartService.remove(id, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Xóa khỏi giỏ hàng thành công!");
        response.put("count", cartService.getCount());
        response.put("amount", cartService.getAmount());
        return ResponseEntity.ok(response);
    }
}