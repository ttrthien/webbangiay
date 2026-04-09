package poly.com.asm.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "Orderdetails")
public class OrderDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Double price;
	private Integer quantity;

	private Integer size;

	@ManyToOne
	@JoinColumn(name = "OrderId", referencedColumnName = "Id")
	private Order order;

	@ManyToOne
	@JoinColumn(name = "ProductId", referencedColumnName = "Id")
	private Product product;
}