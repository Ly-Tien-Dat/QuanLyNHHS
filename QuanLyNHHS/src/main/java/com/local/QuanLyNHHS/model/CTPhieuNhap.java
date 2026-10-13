package com.local.QuanLyNHHS.model;

import jakarta.persistence.*;

@Entity
@Table(name = "CT_PHIEU_NHAP")
public class CTPhieuNhap {

    @EmbeddedId
    private CTPhieuNhapId id;

    @ManyToOne
    @MapsId("maPn")
    @JoinColumn(name = "MA_PN")
    private PhieuNhap phieuNhap;

    @ManyToOne
    @MapsId("maNl")
    @JoinColumn(name = "MA_NL")
    private NguyenLieu nguyenLieu;

    @Column(name = "SO_LUONG", nullable = false, precision = 10, scale = 2)
    private Double soLuong;

    @Column(name = "DON_GIA", nullable = false, precision = 15, scale = 2)
    private Double donGia;

    @Column(name = "THANH_TIEN", precision = 15, scale = 2)
    private Double thanhTien;

    // Default constructor
    public CTPhieuNhap() {}

    public CTPhieuNhap(PhieuNhap phieuNhap, NguyenLieu nguyenLieu, Double soLuong, Double donGia) {
        Long maPn = (phieuNhap != null) ? phieuNhap.getMaPn() : null;
        Long maNl = (nguyenLieu != null) ? nguyenLieu.getMaNl() : null;
        this.id = new CTPhieuNhapId(maPn, maNl);
        this.phieuNhap = phieuNhap;
        this.nguyenLieu = nguyenLieu;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = (soLuong != null && donGia != null) ? soLuong * donGia : null;
    }

    public CTPhieuNhapId getId() {
        return id;
    }

    public void setId(CTPhieuNhapId id) {
        this.id = id;
    }

    public PhieuNhap getPhieuNhap() {
        return phieuNhap;
    }

    public void setPhieuNhap(PhieuNhap phieuNhap) {
        this.phieuNhap = phieuNhap;
        if (this.id == null) {
            this.id = new CTPhieuNhapId();
        }
        if (phieuNhap != null) {
            this.id.setMaPn(phieuNhap.getMaPn());
        }
    }

    public NguyenLieu getNguyenLieu() {
        return nguyenLieu;
    }

    public void setNguyenLieu(NguyenLieu nguyenLieu) {
        this.nguyenLieu = nguyenLieu;
        if (this.id == null) {
            this.id = new CTPhieuNhapId();
        }
        if (nguyenLieu != null) {
            this.id.setMaNl(nguyenLieu.getMaNl());
        }
    }

    public Double getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Double soLuong) {
        this.soLuong = soLuong;
    }

    public Double getDonGia() {
        return donGia;
    }

    public void setDonGia(Double donGia) {
        this.donGia = donGia;
    }

    public Double getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(Double thanhTien) {
        this.thanhTien = thanhTien;
    }

    @Override
    public String toString() {
        return "CTPhieuNhap{" +
                "id=" + id +
                ", soLuong=" + soLuong +
                ", donGia=" + donGia +
                ", thanhTien=" + thanhTien +
                '}';
    }
}
