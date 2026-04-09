package poly.com.asm.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Entity
@Table(name = "Orders")
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "Createdate")
	private Date createDate = new Date();

	@NotBlank(message = "Địa chỉ nhận hàng không được để trống")
	private String address;

	@ManyToOne
	@JoinColumn(name = "Username")
	private Account account;

	@JsonIgnore
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<OrderDetail> orderDetails;
	@Column(name = "status")
	private Integer status = 0;
	@Column(name = "Paymentstatus")
	private String paymentStatus;
	@Column(name = "Paymentmethod")
	private String paymentMethod;
}