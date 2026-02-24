package poly.com.asm.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import poly.com.asm.dao.ProductDAO;
import poly.com.asm.entity.Product;
import poly.com.asm.model.CartItem;
import poly.com.asm.service.CartService;

@Service
@SessionScope
public class CartServiceImpl implements CartService {

    @Autowired
    ProductDAO pdao;

    Map<String, CartItem> map = new HashMap<>();

    @Override
    public void add(Integer id, Integer size) {
        String key = id + "-" + size;
        
        CartItem item = map.get(key);
        if (item == null) {
            Product p = pdao.findById(id).orElse(null);
            if (p != null) {
                item = new CartItem(p.getId(), p.getName(), p.getPrice(), 1, p.getImage(), size);
                map.put(key, item);
            }
        } else {
            item.setQty(item.getQty() + 1);
        }
    }

    @Override
    public void remove(Integer id, Integer size) {
        String key = id + "-" + size;
        map.remove(key);
    }

    @Override
    public void update(Integer id, Integer size, int qty) {
        String key = id + "-" + size;
        CartItem item = map.get(key);
        if (item != null) {
            item.setQty(qty);
        }
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Collection<CartItem> getItems() {
        return map.values();
    }

    @Override
    public int getCount() {
        return map.values().stream().mapToInt(item -> item.getQty()).sum();
    }

    @Override
    public double getAmount() {
        return map.values().stream().mapToDouble(item -> item.getPrice() * item.getQty()).sum();
    }
}