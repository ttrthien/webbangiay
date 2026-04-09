package poly.com.asm.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import poly.com.asm.entity.OrderDetail;
import poly.com.asm.model.ReportItem;
import java.util.List;

public interface OrderDetailDAO extends JpaRepository<OrderDetail, Long> {
    
    @Query("SELECT new poly.com.asm.model.ReportItem(d.product.category.name, SUM(d.price * d.quantity), COUNT(d), MAX(d.price), MIN(d.price), AVG(d.price)) "
         + "FROM OrderDetail d WHERE d.order.status = 1 GROUP BY d.product.category.name")
    List<ReportItem> getRevenueByCategory();

    @Query("SELECT new poly.com.asm.model.ReportItem(d.order.account.fullname, SUM(d.price * d.quantity), COUNT(DISTINCT d.order.id)) "
         + "FROM OrderDetail d WHERE d.order.status = 1 GROUP BY d.order.account.fullname ORDER BY SUM(d.price * d.quantity) DESC")
    List<ReportItem> getTop10VIP(Pageable pageable);
}