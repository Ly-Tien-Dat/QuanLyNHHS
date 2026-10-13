package com.local.QuanLyNHHS.repository;

import com.local.QuanLyNHHS.model.PhieuNhap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface PhieuNhapRepository extends JpaRepository<PhieuNhap, Long> {

    // Lấy phiếu nhập theo nhà cung cấp
    List<PhieuNhap> findByNhaCungCapMaNcc(Long maNcc);

    // Lấy phiếu nhập theo khoảng ngày
    @Query("SELECT pn FROM PhieuNhap pn WHERE pn.ngayNhap BETWEEN :tuNgay AND :denNgay ORDER BY pn.ngayNhap DESC")
    List<PhieuNhap> findByKhoangNgay(@Param("tuNgay") Date tuNgay, @Param("denNgay") Date denNgay);

    // Lấy tất cả phiếu nhập sắp xếp theo ngày mới nhất
    List<PhieuNhap> findAllByOrderByNgayNhapDesc();
}
