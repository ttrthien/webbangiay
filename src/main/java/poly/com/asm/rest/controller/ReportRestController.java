package poly.com.asm.rest.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poly.com.asm.model.ReportItem;

@RestController
@RequestMapping("/api/admin/report")
// @CrossOrigin(origins = "*")
public class ReportRestController {
    
    @Autowired
    poly.com.asm.dao.OrderDetailDAO dao;
    
    @GetMapping("/revenue")
    public List<ReportItem> getRevenueByCategory() {
        return dao.getRevenueByCategory();
    }
    
    @GetMapping("/vip")
    public List<ReportItem> getTop10VIP() {
        return dao.getTop10VIP(org.springframework.data.domain.PageRequest.of(0, 10));
    }
}