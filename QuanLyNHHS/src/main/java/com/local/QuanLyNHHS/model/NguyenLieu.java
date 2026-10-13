package com.local.QuanLyNHHS.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "NGUYEN_LIEU")
public class NguyenLieu {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_nguyen_lieu")
    @SequenceGenerator(name = "seq_nguyen_lieu", sequenceName = "SEQ_NGUYEN_LIEU", allocationSize = 1)
    @Column(name = "MA_NL")
    private Long maNl;

    @Column(name = "TEN_NL", nullable = false, length = 200)
    private String tenNl;

    @Column(name = "DON_VI_TINH", nullable = false, length = 50)
    private String donViTinh;

    @Column(name = "SO_LUONG_TON", precision = 10, scale = 2)
    private Double soLuongTon = 0.0;

    @Column(name = "GIA_NHAP", precision = 15, scale = 2)
    private Double giaNhap = 0.0;

    @OneToMany(mappedBy = "nguyenLieu")
    private List<CTPhieuNhap> danhSachCTPhieuNhap;

    // Default constructor
    public NguyenLieu() {}

    // Full constructor (without ID and relationships)
    public NguyenLieu(String tenNl, String donViTinh, Double soLuongTon, Double giaNhap) {
        this.tenNl = tenNl;
        this.donViTinh = donViTinh;
        this.soLuongTon = soLuongTon;
        this.giaNhap = giaNhap;
    }

    public Long getMaNl() {
        return maNl;
    }

    public void setMaNl(Long maNl) {
        this.maNl = maNl;
    }

    public String getTenNl() {
        return tenNl;
    }

    public void setTenNl(String tenNl) {
        this.tenNl = tenNl;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public Double getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(Double soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public Double getGiaNhap() {
        return giaNhap;
    }

    public void setGiaNhap(Double giaNhap) {
        this.giaNhap = giaNhap;
    }

    public List<CTPhieuNhap> getDanhSachCTPhieuNhap() {
        return danhSachCTPhieuNhap;
    }

    public void setDanhSachCTPhieuNhap(List<CTPhieuNhap> danhSachCTPhieuNhap) {
        this.danhSachCTPhieuNhap = danhSachCTPhieuNhap;
    }

    @Override
    public String toString() {
        return "NguyenLieu{" +
                "maNl=" + maNl +
                ", tenNl='" + tenNl + '\'' +
                ", donViTinh='" + donViTinh + '\'' +
                ", soLuongTon=" + soLuongTon +
                ", giaNhap=" + giaNhap +
                '}';
    }
}
