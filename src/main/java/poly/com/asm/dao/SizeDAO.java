package poly.com.asm.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import poly.com.asm.entity.Size;

public interface SizeDAO extends JpaRepository<Size, Integer> {
}