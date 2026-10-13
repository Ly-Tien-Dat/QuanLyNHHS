package com.local.QuanLyNHHS.repository;

import com.local.QuanLyNHHS.model.CTPhieuNhap;
import com.local.QuanLyNHHS.model.CTPhieuNhapId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CTPhieuNhapRepository extends JpaRepository<CTPhieuNhap, CTPhieuNhapId> {

    // Lấy danh sách chi tiết theo mã phiếu nhập
    List<CTPhieuNhap> findByPhieuNhapMaPn(Long maPn);

    // Lấy danh sách chi tiết theo mã nguyên liệu
    List<CTPhieuNhap> findByNguyenLieuMaNl(Long maNl);

    // Tính tổng tiền phiếu nhập
    @Query("SELECT COALESCE(SUM(ct.thanhTien), 0) FROM CTPhieuNhap ct WHERE ct.phieuNhap.maPn = :maPn")
    Double tinhTongTienPhieuNhap(@Param("maPn") Long maPn);
}
