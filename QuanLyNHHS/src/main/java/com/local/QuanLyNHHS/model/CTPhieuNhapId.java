package com.local.QuanLyNHHS.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

// Embeddable composite key class
@Embeddable
public class CTPhieuNhapId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "MA_PN")
    private Long maPn;

    @Column(name = "MA_NL")
    private Long maNl;

    public CTPhieuNhapId() {}

    public CTPhieuNhapId(Long maPn, Long maNl) {
        this.maPn = maPn;
        this.maNl = maNl;
    }

    public Long getMaPn() {
        return maPn;
    }

    public void setMaPn(Long maPn) {
        this.maPn = maPn;
    }

    public Long getMaNl() {
        return maNl;
    }

    public void setMaNl(Long maNl) {
        this.maNl = maNl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CTPhieuNhapId that = (CTPhieuNhapId) o;
        return Objects.equals(maPn, that.maPn) && Objects.equals(maNl, that.maNl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPn, maNl);
    }

    @Override
    public String toString() {
        return "CTPhieuNhapId{" +
                "maPn=" + maPn +
                ", maNl=" + maNl +
                '}';
    }
}
