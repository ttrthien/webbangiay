package poly.com.asm.service;

import java.util.Collection;
import poly.com.asm.model.CartItem;

public interface CartService {
    void add(Integer id, Integer size); 

    void remove(Integer id, Integer size); 

    void update(Integer id, Integer size, int qty); 

    void clear();

    Collection<CartItem> getItems();

    int getCount();

    double getAmount();
}