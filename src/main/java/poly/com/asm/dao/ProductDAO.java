package poly.com.asm.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import poly.com.asm.entity.Product;

public interface ProductDAO extends JpaRepository<Product, Integer> {
	@Query("SELECT p FROM Product p WHERE p.category.id=?1")
	Page<Product> findByCategoryId(String cid, Pageable pageable);

	@Query("SELECT p FROM Product p WHERE p.price < ?1")
	List<Product> TimspDuoiGia(Double price);

	Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

	Page<Product> findByPriceBetween(Double min, Double max, Pageable pageable);

	@Query("SELECT p FROM Product p LEFT JOIN p.orderDetails od GROUP BY p ORDER BY COUNT(od) DESC")
	Page<Product> findTopSelling(Pageable pageable);

	@Query("SELECT p FROM Product p WHERE p.name LIKE %?1%")
	List<Product> findByKeywords(String keywords);

	@Query("""
			SELECT DISTINCT p FROM Product p
			LEFT JOIN p.sizes s
			WHERE
			(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
			AND (:minPrice IS NULL OR p.price >= :minPrice)
			AND (:maxPrice IS NULL OR p.price <= :maxPrice)
			AND (:categoryId IS NULL OR p.category.id = :categoryId)
			AND (:sizeId IS NULL OR s.id = :sizeId)
			""")
	List<Product> searchAdvanced(@Param("keyword") String keyword, @Param("minPrice") Double minPrice,
			@Param("maxPrice") Double maxPrice, @Param("categoryId") String categoryId,
			@Param("sizeId") Integer sizeId);
}