package poly.com.asm.service;

import java.util.List;
import poly.com.asm.entity.Size;

public interface SizeService {
    List<Size> findAll();
    Size findById(Integer id);
}