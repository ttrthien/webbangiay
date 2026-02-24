package poly.com.asm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import poly.com.asm.service.CartService;

@Controller
public class CartController {

    @Autowired
    CartService cartService;

    @Autowired
    HttpSession session;

    @RequestMapping("/cart/view")
    public String view(Model model) {
        model.addAttribute("cart", cartService);
        session.setAttribute("cart", cartService);
        model.addAttribute("view", "cart/cart.html");
        return "layout/index";
    }

    @PostMapping("/cart/add/{id}")
    public String add(@PathVariable("id") Integer id, @RequestParam("size") Integer size) {
        cartService.add(id, size);
        session.setAttribute("cart", cartService);
        return "redirect:/cart/view";
    }

    @GetMapping("/api/cart/add/{id}")
    @ResponseBody
    public Integer addToCartApi(@PathVariable("id") Integer id, @RequestParam("size") Integer size) {
        cartService.add(id, size);
        session.setAttribute("cart", cartService);
        return cartService.getCount();
    }

    @RequestMapping("/cart/remove/{id}")
    public String remove(@PathVariable("id") Integer id, @RequestParam("size") Integer size) {
        cartService.remove(id, size);
        session.setAttribute("cart", cartService);
        return "redirect:/cart/view";
    }

    @RequestMapping("/cart/update")
    public String update(@RequestParam("id") Integer id, 
                        @RequestParam("size") Integer size, 
                        @RequestParam("qty") int qty) {
        cartService.update(id, size, qty);
        session.setAttribute("cart", cartService);
        return "redirect:/cart/view";
    }

    @RequestMapping("/cart/clear")
    public String clear() {
        cartService.clear();
        session.setAttribute("cart", cartService);
        return "redirect:/cart/view";
    }
}