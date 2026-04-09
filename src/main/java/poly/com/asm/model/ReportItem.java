package poly.com.asm.model;

import java.io.Serializable;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor 
public class ReportItem implements Serializable {
    private Serializable group;
    private Double sum;
    private Long count;
    private Double max;
    private Double min;
    private Double avg;

    // Constructor 3 tham số cực kỳ quan trọng cho trang VIP
    public ReportItem(Serializable group, Double sum, Long count) {
        this.group = group;
        this.sum = sum;
        this.count = count;
    }
}