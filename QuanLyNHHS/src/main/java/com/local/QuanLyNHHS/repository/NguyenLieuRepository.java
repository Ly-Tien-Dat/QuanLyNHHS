package com.local.QuanLyNHHS.repository;

import com.local.QuanLyNHHS.model.NguyenLieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NguyenLieuRepository extends JpaRepository<NguyenLieu, Long> {

    // Tìm nguyên liệu theo tên
    List<NguyenLieu> findByTenNlContainingIgnoreCase(String tenNl);

    // Lấy nguyên liệu có tồn kho > 0
    @Query("SELECT nl FROM NguyenLieu nl WHERE nl.soLuongTon > 0 ORDER BY nl.tenNl")
    List<NguyenLieu> findByTonKhoConHang();

    // Lấy nguyên liệu sắp hết (tồn kho < ngưỡng)
    @Query("SELECT nl FROM NguyenLieu nl WHERE nl.soLuongTon < :nguong ORDER BY nl.soLuongTon ASC")
    List<NguyenLieu> findBySapHetHang(@Param("nguong") Double nguong);

    // Lấy nguyên liệu theo đơn vị tính
    List<NguyenLieu> findByDonViTinh(String donViTinh);
}
