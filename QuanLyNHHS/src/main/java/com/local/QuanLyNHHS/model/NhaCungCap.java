package com.local.QuanLyNHHS.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "NHA_CUNG_CAP")
public class NhaCungCap {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_nha_cung_cap")
    @SequenceGenerator(name = "seq_nha_cung_cap", sequenceName = "SEQ_NHA_CUNG_CAP", allocationSize = 1)
    @Column(name = "MA_NCC")
    private Long maNcc;

    @Column(name = "TEN_NCC", nullable = false, length = 200)
    private String tenNcc;

    @Column(name = "DIA_CHI", length = 500)
    private String diaChi;

    @Column(name = "SDT", length = 15)
    private String sdt;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @OneToMany(mappedBy = "nhaCungCap", cascade = CascadeType.ALL)
    private List<PhieuNhap> danhSachPhieuNhap;

    // Default constructor
    public NhaCungCap() {}

    // Full constructor (without ID and relationships)
    public NhaCungCap(String tenNcc, String diaChi, String sdt, String email) {
        this.tenNcc = tenNcc;
        this.diaChi = diaChi;
        this.sdt = sdt;
        this.email = email;
    }

    public Long getMaNcc() {
        return maNcc;
    }

    public void setMaNcc(Long maNcc) {
        this.maNcc = maNcc;
    }

    public String getTenNcc() {
        return tenNcc;
    }

    public void setTenNcc(String tenNcc) {
        this.tenNcc = tenNcc;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<PhieuNhap> getDanhSachPhieuNhap() {
        return danhSachPhieuNhap;
    }

    public void setDanhSachPhieuNhap(List<PhieuNhap> danhSachPhieuNhap) {
        this.danhSachPhieuNhap = danhSachPhieuNhap;
    }

    @Override
    public String toString() {
        return "NhaCungCap{" +
                "maNcc=" + maNcc +
                ", tenNcc='" + tenNcc + '\'' +
                ", diaChi='" + diaChi + '\'' +
                ", sdt='" + sdt + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
