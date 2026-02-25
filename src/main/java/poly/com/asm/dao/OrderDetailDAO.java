package poly.com.asm.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import poly.com.asm.entity.OrderDetail;
import poly.com.asm.model.ReportItem;
import java.util.List;

public interface OrderDetailDAO extends JpaRepository<OrderDetail, Long> {
    
    // Thống kê Doanh thu (Group, Sum, Count, Max, Min, Avg)
    @Query("SELECT new poly.com.asm.model.ReportItem(d.product.category.name, SUM(d.price * d.quantity), SUM(d.quantity), MAX(d.price), MIN(d.price), AVG(d.price)) "
         + "FROM OrderDetail d "
         + "WHERE d.order.status = 1 " 
         + "GROUP BY d.product.category.name")
    List<ReportItem> getRevenueByCategory();

    // Thống kê VIP (Giữ nguyên)
    @Query("SELECT new poly.com.asm.model.ReportItem(d.order.account.fullname, SUM(d.price * d.quantity), COUNT(DISTINCT d.order.id)) "
         + "FROM OrderDetail d "
         + "WHERE d.order.status = 1 "
         + "GROUP BY d.order.account.fullname "
         + "ORDER BY SUM(d.price * d.quantity) DESC")
    List<ReportItem> getTop10VIP(Pageable pageable);
}