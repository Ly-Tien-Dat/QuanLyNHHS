package com.local.QuanLyNHHS.model;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "PHIEU_NHAP")
public class PhieuNhap {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_phieu_nhap")
    @SequenceGenerator(name = "seq_phieu_nhap", sequenceName = "SEQ_PHIEU_NHAP", allocationSize = 1)
    @Column(name = "MA_PN")
    private Long maPn;

    @ManyToOne
    @JoinColumn(name = "MA_NCC", nullable = false)
    private NhaCungCap nhaCungCap;

    @Column(name = "NGAY_NHAP")
    @Temporal(TemporalType.DATE)
    private Date ngayNhap = new Date();

    @Column(name = "TONG_TIEN", precision = 15, scale = 2)
    private Double tongTien = 0.0;

    @Column(name = "GHI_CHU", length = 500)
    private String ghiChu;

    @OneToMany(mappedBy = "phieuNhap", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CTPhieuNhap> danhSachChiTiet;

    // Default constructor
    public PhieuNhap() {}

    // Constructor with nhaCungCap, ngayNhap and ghiChu
    public PhieuNhap(NhaCungCap nhaCungCap, Date ngayNhap, String ghiChu) {
        this.nhaCungCap = nhaCungCap;
        this.ngayNhap = ngayNhap;
        this.ghiChu = ghiChu;
    }

    public Long getMaPn() {
        return maPn;
    }

    public void setMaPn(Long maPn) {
        this.maPn = maPn;
    }

    public NhaCungCap getNhaCungCap() {
        return nhaCungCap;
    }

    public void setNhaCungCap(NhaCungCap nhaCungCap) {
        this.nhaCungCap = nhaCungCap;
    }

    public Date getNgayNhap() {
        return ngayNhap;
    }

    public void setNgayNhap(Date ngayNhap) {
        this.ngayNhap = ngayNhap;
    }

    public Double getTongTien() {
        return tongTien;
    }

    public void setTongTien(Double tongTien) {
        this.tongTien = tongTien;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public List<CTPhieuNhap> getDanhSachChiTiet() {
        return danhSachChiTiet;
    }

    public void setDanhSachChiTiet(List<CTPhieuNhap> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet;
    }

    @Override
    public String toString() {
        return "PhieuNhap{" +
                "maPn=" + maPn +
                ", nhaCungCap=" + (nhaCungCap != null ? nhaCungCap.getMaNcc() : null) +
                ", ngayNhap=" + ngayNhap +
                ", tongTien=" + tongTien +
                ", ghiChu='" + ghiChu + '\'' +
                '}';
    }
}
