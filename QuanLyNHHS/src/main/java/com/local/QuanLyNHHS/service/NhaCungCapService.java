package com.local.QuanLyNHHS.service;

import com.local.QuanLyNHHS.model.NhaCungCap;
import com.local.QuanLyNHHS.repository.NhaCungCapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class NhaCungCapService {

    @Autowired
    private NhaCungCapRepository nhaCungCapRepository;

    // Lấy tất cả nhà cung cấp
    public List<NhaCungCap> layTatCa() {
        return nhaCungCapRepository.findAll();
    }

    // Tìm theo ID
    public Optional<NhaCungCap> timTheoId(Long id) {
        return nhaCungCapRepository.findById(id);
    }

    // Tìm theo tên
    public List<NhaCungCap> timTheoTen(String tenNcc) {
        return nhaCungCapRepository.findByTenNccContainingIgnoreCase(tenNcc);
    }

    // Thêm mới
    public NhaCungCap themMoi(NhaCungCap nhaCungCap) {
        return nhaCungCapRepository.save(nhaCungCap);
    }

    // Cập nhật
    public NhaCungCap capNhat(Long id, NhaCungCap nhaCungCapMoi) {
        return nhaCungCapRepository.findById(id)
                .map(ncc -> {
                    ncc.setTenNcc(nhaCungCapMoi.getTenNcc());
                    ncc.setDiaChi(nhaCungCapMoi.getDiaChi());
                    ncc.setSdt(nhaCungCapMoi.getSdt());
                    ncc.setEmail(nhaCungCapMoi.getEmail());
                    return nhaCungCapRepository.save(ncc);
                })
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp với mã: " + id));
    }

    // Xóa
    public void xoa(Long id) {
        if (!nhaCungCapRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy nhà cung cấp với mã: " + id);
        }
        nhaCungCapRepository.deleteById(id);
    }
}
