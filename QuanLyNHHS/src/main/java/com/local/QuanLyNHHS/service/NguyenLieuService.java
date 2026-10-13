package com.local.QuanLyNHHS.service;

import com.local.QuanLyNHHS.model.NguyenLieu;
import com.local.QuanLyNHHS.repository.NguyenLieuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class NguyenLieuService {

    @Autowired
    private NguyenLieuRepository nguyenLieuRepository;

    // Lấy tất cả nguyên liệu
    public List<NguyenLieu> layTatCa() {
        return nguyenLieuRepository.findAll();
    }

    // Tìm theo ID
    public Optional<NguyenLieu> timTheoId(Long id) {
        return nguyenLieuRepository.findById(id);
    }

    // Tìm theo tên
    public List<NguyenLieu> timTheoTen(String tenNl) {
        return nguyenLieuRepository.findByTenNlContainingIgnoreCase(tenNl);
    }

    // Lấy nguyên liệu còn hàng trong kho
    public List<NguyenLieu> layConHang() {
        return nguyenLieuRepository.findByTonKhoConHang();
    }

    // Lấy nguyên liệu sắp hết (đặt ngưỡng cảnh báo)
    public List<NguyenLieu> laySapHetHang(Double nguong) {
        return nguyenLieuRepository.findBySapHetHang(nguong);
    }

    // Thêm mới
    public NguyenLieu themMoi(NguyenLieu nguyenLieu) {
        return nguyenLieuRepository.save(nguyenLieu);
    }

    // Cập nhật
    public NguyenLieu capNhat(Long id, NguyenLieu nguyenLieuMoi) {
        return nguyenLieuRepository.findById(id)
                .map(nl -> {
                    nl.setTenNl(nguyenLieuMoi.getTenNl());
                    nl.setDonViTinh(nguyenLieuMoi.getDonViTinh());
                    // Note: soLuongTon and giaNhap are managed by triggers, not manual updates
                    return nguyenLieuRepository.save(nl);
                })
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu với mã: " + id));
    }

    // Xóa
    public void xoa(Long id) {
        if (!nguyenLieuRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy nguyên liệu với mã: " + id);
        }
        nguyenLieuRepository.deleteById(id);
    }

    // Cập nhật tồn kho (gọi sau khi nhập hàng - backup nếu trigger không hoạt động)
    public void capNhatTonKho(Long maNl, Double soLuongNhap, Double giaNhap) {
        nguyenLieuRepository.findById(maNl).ifPresent(nl -> {
            nl.setSoLuongTon(nl.getSoLuongTon() + soLuongNhap);
            nl.setGiaNhap(giaNhap);
            nguyenLieuRepository.save(nl);
        });
    }
}
