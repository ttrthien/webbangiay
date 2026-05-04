package poly.com.asm.rest.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poly.com.asm.model.ReportItem;

@RestController
@RequestMapping("/api/admin/report")
@CrossOrigin(origins = "*")
public class ReportRestController {
    
    @Autowired
    poly.com.asm.dao.OrderDetailDAO dao;
    
    @GetMapping("/revenue")
    public List<ReportItem> getRevenueByCategory() {
        return dao.getRevenueByCategory();
    }
    
    @GetMapping("/vip")
    public List<ReportItem> getTop10VIP() {
        return dao.getTop10VIP(PageRequest.of(0, 10));
    }
    
    @GetMapping("/revenue/{category}")
    public List<ReportItem> getRevenueByCategoryName(
            @PathVariable String category) {
        return dao.getRevenueByCategory().stream()
            .filter(item -> item.getGroup().equals(category))
            .collect(Collectors.toList());
    }
    
    @GetMapping("/vip/{name}")
    public List<ReportItem> getVIPByName(@PathVariable String name) {
        return dao.getTop10VIP(PageRequest.of(0, 10)).stream()
            .filter(item -> item.getGroup().toString().contains(name))
            .collect(Collectors.toList());
    }
}