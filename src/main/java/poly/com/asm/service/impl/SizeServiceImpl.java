package poly.com.asm.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poly.com.asm.dao.SizeDAO;
import poly.com.asm.entity.Size;
import poly.com.asm.service.SizeService;

@Service
public class SizeServiceImpl implements SizeService {

    @Autowired
    SizeDAO dao;

    @Override
    public List<Size> findAll() {
        return dao.findAll();
    }

    @Override
    public Size findById(Integer id) {
        return dao.findById(id).get();
    }
}