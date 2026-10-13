-- ============================================================================
-- HỆ THỐNG QUẢN LÝ NHÀ HÀNG HẢI SẢN
-- MODULE: QUẢN LÝ KHO & NHẬP HÀNG (WAREHOUSE & IMPORT MODULE)
-- FILE: 02_procedures_triggers.sql
-- MÔ TẢ: CÁC TRIGGER VÀ STORED PROCEDURE QUẢN LÝ KHO, TÍNH TIỀN VÀ BÁO CÁO
-- ============================================================================

-- ============================================================================
-- 1. TRIGGER: TRG_TINH_THANH_TIEN
-- MỤC ĐÍCH: Tự động tính thành tiền trước khi thêm hoặc cập nhật chi tiết phiếu nhập
--           THANH_TIEN = SO_LUONG * DON_GIA
-- ============================================================================
CREATE OR REPLACE TRIGGER TRG_TINH_THANH_TIEN
BEFORE INSERT OR UPDATE ON CT_PHIEU_NHAP
FOR EACH ROW
BEGIN
    -- Kiểm tra tính hợp lệ của số lượng nhập
    IF :NEW.SO_LUONG < 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Lỗi: Số lượng nhập không được nhỏ hơn 0.');
    END IF;

    -- Kiểm tra tính hợp lệ của đơn giá nhập
    IF :NEW.DON_GIA < 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Lỗi: Đơn giá nhập không được nhỏ hơn 0.');
    END IF;

    -- Tự động tính thành tiền: THANH_TIEN = SO_LUONG * DON_GIA
    :NEW.THANH_TIEN := NVL(:NEW.SO_LUONG, 0) * NVL(:NEW.DON_GIA, 0);

EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE BETWEEN -20999 AND -20000 THEN
            RAISE;
        ELSE
            RAISE_APPLICATION_ERROR(-20003, 'Lỗi khi tự động tính thành tiền chi tiết phiếu nhập: ' || SQLERRM);
        END IF;
END TRG_TINH_THANH_TIEN;
/

-- ============================================================================
-- 2. TRIGGER: TRG_CAP_NHAT_TON_KHO_NHAP
-- MỤC ĐÍCH: Tự động cộng số lượng tồn kho và cập nhật giá nhập mới nhất cho
--           nguyên liệu sau khi chèn dòng chi tiết phiếu nhập mới.
-- ============================================================================
CREATE OR REPLACE TRIGGER TRG_CAP_NHAT_TON_KHO_NHAP
AFTER INSERT ON CT_PHIEU_NHAP
FOR EACH ROW
BEGIN
    -- Cộng số lượng nhập vào số lượng tồn và cập nhật giá nhập mới nhất
    UPDATE NGUYEN_LIEU
    SET SO_LUONG_TON = NVL(SO_LUONG_TON, 0) + NVL(:NEW.SO_LUONG, 0),
        GIA_NHAP     = :NEW.DON_GIA
    WHERE MA_NL = :NEW.MA_NL;

    -- Kiểm tra nguyên liệu có tồn tại trong hệ thống hay không
    IF SQL%NOTFOUND THEN
        RAISE_APPLICATION_ERROR(-20004, 'Lỗi: Mã nguyên liệu ' || :NEW.MA_NL || ' không tồn tại trong kho.');
    END IF;

EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE BETWEEN -20999 AND -20000 THEN
            RAISE;
        ELSE
            RAISE_APPLICATION_ERROR(-20005, 'Lỗi khi cập nhật tồn kho sau khi nhập hàng: ' || SQLERRM);
        END IF;
END TRG_CAP_NHAT_TON_KHO_NHAP;
/

-- ============================================================================
-- 3. TRIGGER: TRG_CAP_NHAT_TON_KHO_XOA
-- MỤC ĐÍCH: Tự động trừ số lượng tồn kho của nguyên liệu khi một dòng
--           chi tiết phiếu nhập bị xóa. Kiểm tra không để tồn kho bị âm.
-- ============================================================================
CREATE OR REPLACE TRIGGER TRG_CAP_NHAT_TON_KHO_XOA
AFTER DELETE ON CT_PHIEU_NHAP
FOR EACH ROW
DECLARE
    v_so_luong_ton NUMBER;
BEGIN
    -- Khóa và lấy số lượng tồn hiện tại của nguyên liệu để kiểm tra
    SELECT NVL(SO_LUONG_TON, 0)
    INTO v_so_luong_ton
    FROM NGUYEN_LIEU
    WHERE MA_NL = :OLD.MA_NL
    FOR UPDATE;

    -- Kiểm tra nếu số lượng tồn hiện tại nhỏ hơn số lượng cần trừ
    IF v_so_luong_ton < NVL(:OLD.SO_LUONG, 0) THEN
        RAISE_APPLICATION_ERROR(-20006, 'Lỗi: Không thể xóa chi tiết nhập. Số lượng tồn kho hiện tại (' 
            || v_so_luong_ton || ') nhỏ hơn số lượng cần trừ (' || :OLD.SO_LUONG || ').');
    END IF;

    -- Cập nhật giảm số lượng tồn kho tương ứng
    UPDATE NGUYEN_LIEU
    SET SO_LUONG_TON = SO_LUONG_TON - NVL(:OLD.SO_LUONG, 0)
    WHERE MA_NL = :OLD.MA_NL;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20007, 'Lỗi: Mã nguyên liệu ' || :OLD.MA_NL || ' không tồn tại trong hệ thống.');
    WHEN OTHERS THEN
        IF SQLCODE BETWEEN -20999 AND -20000 THEN
            RAISE;
        ELSE
            RAISE_APPLICATION_ERROR(-20008, 'Lỗi khi cập nhật tồn kho sau khi xóa chi tiết phiếu nhập: ' || SQLERRM);
        END IF;
END TRG_CAP_NHAT_TON_KHO_XOA;
/

-- ============================================================================
-- 4. TRIGGER: TRG_CAP_NHAT_TONG_TIEN (COMPOUND TRIGGER)
-- MỤC ĐÍCH: Cập nhật PHIEU_NHAP.TONG_TIEN = SUM(THANH_TIEN) cho các MA_PN bị ảnh hưởng.
--           Sử dụng Compound Trigger để khắc phục triệt để lỗi Mutating Table (ORA-04091).
-- ============================================================================
CREATE OR REPLACE TRIGGER TRG_CAP_NHAT_TONG_TIEN
FOR INSERT OR UPDATE OR DELETE ON CT_PHIEU_NHAP
COMPOUND TRIGGER

    -- Bảng băm lưu trữ danh sách các MA_PN bị tác động (tránh trùng lặp)
    TYPE t_mapn_set IS TABLE OF BOOLEAN INDEX BY VARCHAR2(50);
    g_mapn_list t_mapn_set;

    -- 1. Trước khi thực thi câu lệnh DML: Khởi tạo lại danh sách rỗng
    BEFORE STATEMENT IS
    BEGIN
        g_mapn_list.DELETE;
    END BEFORE STATEMENT;

    -- 2. Sau mỗi dòng bị tác động: Thu thập MA_PN vào danh sách
    AFTER EACH ROW IS
    BEGIN
        IF INSERTING OR UPDATING THEN
            IF :NEW.MA_PN IS NOT NULL THEN
                g_mapn_list(TO_CHAR(:NEW.MA_PN)) := TRUE;
            END IF;
        END IF;

        IF DELETING OR UPDATING THEN
            IF :OLD.MA_PN IS NOT NULL THEN
                g_mapn_list(TO_CHAR(:OLD.MA_PN)) := TRUE;
            END IF;
        END IF;
    END AFTER EACH ROW;

    -- 3. Sau khi toàn bộ câu lệnh DML kết thúc: Tính lại tổng tiền và cập nhật PHIEU_NHAP
    AFTER STATEMENT IS
        v_ma_pn_str VARCHAR2(50);
        v_tong_tien NUMBER;
    BEGIN
        v_ma_pn_str := g_mapn_list.FIRST;
        WHILE v_ma_pn_str IS NOT NULL LOOP
            -- Tính tổng thành tiền hiện tại của phiếu nhập từ bảng chi tiết
            SELECT NVL(SUM(THANH_TIEN), 0)
            INTO v_tong_tien
            FROM CT_PHIEU_NHAP
            WHERE MA_PN = TO_NUMBER(v_ma_pn_str);

            -- Cập nhật TONG_TIEN vào bảng phiếu nhập
            UPDATE PHIEU_NHAP
            SET TONG_TIEN = v_tong_tien
            WHERE MA_PN = TO_NUMBER(v_ma_pn_str);

            v_ma_pn_str := g_mapn_list.NEXT(v_ma_pn_str);
        END LOOP;
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE BETWEEN -20999 AND -20000 THEN
                RAISE;
            ELSE
                RAISE_APPLICATION_ERROR(-20010, 'Lỗi khi cập nhật tổng tiền phiếu nhập (Compound Trigger): ' || SQLERRM);
            END IF;
    END AFTER STATEMENT;

END TRG_CAP_NHAT_TONG_TIEN;
/

-- ============================================================================
-- 5. PROCEDURE: SP_TINH_TONG_PHIEU_NHAP
-- MỤC ĐÍCH: Tính tổng tiền các chi tiết của một phiếu nhập dựa vào MA_PN
-- THAM SỐ:
--   - IN:  p_ma_pn NUMBER      (Mã phiếu nhập cần tính)
--   - OUT: p_tong_tien NUMBER   (Tổng tiền trả về)
-- ============================================================================
CREATE OR REPLACE PROCEDURE SP_TINH_TONG_PHIEU_NHAP (
    p_ma_pn     IN  NUMBER,
    p_tong_tien OUT NUMBER
) AS
BEGIN
    -- Kiểm tra tính hợp lệ của tham số
    IF p_ma_pn IS NULL THEN
        RAISE_APPLICATION_ERROR(-20011, 'Lỗi: Mã phiếu nhập (p_ma_pn) không được để trống.');
    END IF;

    -- Tính tổng thành tiền từ chi tiết phiếu nhập
    SELECT NVL(SUM(THANH_TIEN), 0)
    INTO p_tong_tien
    FROM CT_PHIEU_NHAP
    WHERE MA_PN = p_ma_pn;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_tong_tien := 0;
    WHEN OTHERS THEN
        IF SQLCODE BETWEEN -20999 AND -20000 THEN
            RAISE;
        ELSE
            RAISE_APPLICATION_ERROR(-20012, 'Lỗi khi tính tổng tiền phiếu nhập: ' || SQLERRM);
        END IF;
END SP_TINH_TONG_PHIEU_NHAP;
/

-- ============================================================================
-- 6. PROCEDURE: SP_BAO_CAO_TON_KHO
-- MỤC ĐÍCH: Trả về con trỏ danh sách nguyên liệu còn tồn kho (SO_LUONG_TON > 0),
--           sắp xếp tăng dần theo tên nguyên liệu (TEN_NL).
-- THAM SỐ:
--   - OUT: p_cursor SYS_REFCURSOR (Con trỏ kết quả)
-- ============================================================================
CREATE OR REPLACE PROCEDURE SP_BAO_CAO_TON_KHO (
    p_cursor OUT SYS_REFCURSOR
) AS
BEGIN
    -- Mở con trỏ trả về danh sách nguyên liệu còn tồn kho > 0
    OPEN p_cursor FOR
        SELECT *
        FROM NGUYEN_LIEU
        WHERE SO_LUONG_TON > 0
        ORDER BY TEN_NL ASC;

EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE BETWEEN -20999 AND -20000 THEN
            RAISE;
        ELSE
            RAISE_APPLICATION_ERROR(-20013, 'Lỗi khi lấy báo cáo tồn kho: ' || SQLERRM);
        END IF;
END SP_BAO_CAO_TON_KHO;
/
