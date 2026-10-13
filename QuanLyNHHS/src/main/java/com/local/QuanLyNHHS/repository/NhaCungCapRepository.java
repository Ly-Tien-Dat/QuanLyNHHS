package com.local.QuanLyNHHS.repository;

import com.local.QuanLyNHHS.model.NhaCungCap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NhaCungCapRepository extends JpaRepository<NhaCungCap, Long> {

    // Tìm nhà cung cấp theo tên (LIKE)
    List<NhaCungCap> findByTenNccContainingIgnoreCase(String tenNcc);

    // Tìm nhà cung cấp theo số điện thoại
    NhaCungCap findBySdt(String sdt);

    // Kiểm tra email đã tồn tại
    boolean existsByEmail(String email);
}
