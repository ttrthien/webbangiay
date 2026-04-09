package poly.com.asm.rest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import poly.com.asm.dao.ProductDAO;
import poly.com.asm.entity.Product;

// @CrossOrigin("*")
@RestController
@RequestMapping("/api/products")
public class ProductRestController {

	@Autowired
	ProductDAO productDao;

	@GetMapping
	public ResponseEntity<?> getAllProducts(@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size);
		Page<Product> data = productDao.findAll(pageable);
		return ResponseEntity.ok(data);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getProductById(@PathVariable("id") Integer id) {
		Optional<Product> product = productDao.findById(id);

		if (product.isPresent()) {
			return ResponseEntity.ok(product.get());
		}
		return ResponseEntity.status(404).body("{\"message\": \"Không tìm thấy sản phẩm!\"}");
	}

	@GetMapping("/search")
	public ResponseEntity<?> searchProducts(@RequestParam("keywords") String keywords) {
		List<Product> list = productDao.findByKeywords(keywords);
		return ResponseEntity.ok(list);
	}

	@GetMapping("/category/{cid}")
	public ResponseEntity<?> getProductsByCategory(@PathVariable("cid") String cid,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Product> data = productDao.findByCategoryId(cid, pageable);
		return ResponseEntity.ok(data);
	}
}