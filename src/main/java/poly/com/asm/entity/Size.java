package poly.com.asm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "Sizes")
public class Size {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "Sizevalue")
	private Integer sizeValue;
	@JsonIgnore
	@ManyToMany(mappedBy = "sizes")
	private List<Product> products;
}