package com.local.QuanLyNHHS.service;

import com.local.QuanLyNHHS.model.*;
import com.local.QuanLyNHHS.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PhieuNhapService {

    @Autowired
    private PhieuNhapRepository phieuNhapRepository;

    @Autowired
    private CTPhieuNhapRepository ctPhieuNhapRepository;

    @Autowired
    private NguyenLieuRepository nguyenLieuRepository;

    @Autowired
    private NhaCungCapRepository nhaCungCapRepository;

    // Lấy tất cả phiếu nhập (sắp xếp theo ngày mới nhất)
    public List<PhieuNhap> layTatCa() {
        return phieuNhapRepository.findAllByOrderByNgayNhapDesc();
    }

    // Tìm theo ID
    public Optional<PhieuNhap> timTheoId(Long id) {
        return phieuNhapRepository.findById(id);
    }

    // Lấy phiếu nhập theo nhà cung cấp
    public List<PhieuNhap> layTheoNhaCungCap(Long maNcc) {
        return phieuNhapRepository.findByNhaCungCapMaNcc(maNcc);
    }

    // Lấy phiếu nhập theo khoảng ngày
    public List<PhieuNhap> layTheoKhoangNgay(Date tuNgay, Date denNgay) {
        return phieuNhapRepository.findByKhoangNgay(tuNgay, denNgay);
    }

    // Tạo phiếu nhập mới
    @Transactional
    public PhieuNhap taoPhieuNhap(Long maNcc, String ghiChu, Date ngayNhap) {
        NhaCungCap ncc = nhaCungCapRepository.findById(maNcc)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp với mã: " + maNcc));

        PhieuNhap phieuNhap = new PhieuNhap();
        phieuNhap.setNhaCungCap(ncc);
        phieuNhap.setNgayNhap(ngayNhap != null ? ngayNhap : new Date());
        phieuNhap.setGhiChu(ghiChu);
        phieuNhap.setTongTien(0.0);

        return phieuNhapRepository.save(phieuNhap);
    }

    // Thêm chi tiết vào phiếu nhập + cập nhật tồn kho
    @Transactional
    public CTPhieuNhap themChiTiet(Long maPn, Long maNl, Double soLuong, Double donGia) {
        PhieuNhap phieuNhap = phieuNhapRepository.findById(maPn)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu nhập với mã: " + maPn));

        NguyenLieu nguyenLieu = nguyenLieuRepository.findById(maNl)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nguyên liệu với mã: " + maNl));

        // Tạo chi tiết phiếu nhập
        CTPhieuNhap chiTiet = new CTPhieuNhap();
        chiTiet.setId(new CTPhieuNhapId(maPn, maNl));
        chiTiet.setPhieuNhap(phieuNhap);
        chiTiet.setNguyenLieu(nguyenLieu);
        chiTiet.setSoLuong(soLuong);
        chiTiet.setDonGia(donGia);
        chiTiet.setThanhTien(soLuong * donGia);

        CTPhieuNhap saved = ctPhieuNhapRepository.save(chiTiet);

        // Cập nhật tồn kho nguyên liệu (backup - nếu trigger Oracle không chạy)
        nguyenLieu.setSoLuongTon(nguyenLieu.getSoLuongTon() + soLuong);
        nguyenLieu.setGiaNhap(donGia);
        nguyenLieuRepository.save(nguyenLieu);

        // Cập nhật tổng tiền phiếu nhập
        capNhatTongTien(maPn);

        return saved;
    }

    // Cập nhật tổng tiền phiếu nhập
    @Transactional
    public void capNhatTongTien(Long maPn) {
        Double tongTien = ctPhieuNhapRepository.tinhTongTienPhieuNhap(maPn);
        phieuNhapRepository.findById(maPn).ifPresent(pn -> {
            pn.setTongTien(tongTien);
            phieuNhapRepository.save(pn);
        });
    }

    // Lấy danh sách chi tiết theo phiếu nhập
    public List<CTPhieuNhap> layChiTietTheoPhieuNhap(Long maPn) {
        return ctPhieuNhapRepository.findByPhieuNhapMaPn(maPn);
    }

    // Xóa chi tiết phiếu nhập (cập nhật lại tồn kho)
    @Transactional
    public void xoaChiTiet(Long maPn, Long maNl) {
        CTPhieuNhapId id = new CTPhieuNhapId(maPn, maNl);
        CTPhieuNhap chiTiet = ctPhieuNhapRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết phiếu nhập"));

        // Trừ lại tồn kho
        NguyenLieu nl = chiTiet.getNguyenLieu();
        nl.setSoLuongTon(nl.getSoLuongTon() - chiTiet.getSoLuong());
        nguyenLieuRepository.save(nl);

        ctPhieuNhapRepository.delete(chiTiet);

        // Cập nhật lại tổng tiền
        capNhatTongTien(maPn);
    }

    // Xóa phiếu nhập (xóa cả chi tiết và trừ tồn kho)
    @Transactional
    public void xoaPhieuNhap(Long maPn) {
        PhieuNhap phieuNhap = phieuNhapRepository.findById(maPn)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu nhập với mã: " + maPn));

        // Trừ tồn kho cho từng chi tiết
        List<CTPhieuNhap> chiTietList = ctPhieuNhapRepository.findByPhieuNhapMaPn(maPn);
        for (CTPhieuNhap ct : chiTietList) {
            NguyenLieu nl = ct.getNguyenLieu();
            nl.setSoLuongTon(nl.getSoLuongTon() - ct.getSoLuong());
            nguyenLieuRepository.save(nl);
        }

        phieuNhapRepository.delete(phieuNhap); // cascades to CT_PHIEU_NHAP
    }
}
