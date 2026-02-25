package poly.com.asm.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor // Tạo constructor 6 tham số cho Doanh thu
public class ReportItem implements Serializable {
    private Serializable group;
    private Double sum;
    private Long count;
    private Double max;
    private Double min;
    private Double avg;

    // Giữ lại Constructor 3 tham số này để hàm getTop10VIP không bị lỗi
    public ReportItem(Serializable group, Double sum, Long count) {
        this.group = group;
        this.sum = sum;
        this.count = count;
    }
}