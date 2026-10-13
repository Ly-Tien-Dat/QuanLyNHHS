-- =============================================================================
-- DỰ ÁN: HỆ THỐNG QUẢN LÝ NHÀ HÀNG HẢI SẢN (QuanLyNHHS)
-- PHÂN HỆ: KHO & NHẬP HÀNG (Warehouse & Import Management)
-- TẬP TIN: 01_create_tables.sql
-- HỆ QUẢN TRỊ CSDL: ORACLE DATABASE
-- MÔ TẢ: Khởi tạo cấu trúc bảng, sequence, trigger và nạp dữ liệu mẫu
-- =============================================================================

SET DEFINE OFF;

-- =============================================================================
-- 1. XÓA CÁC BẢNG VÀ SEQUENCE CŨ (NẾU ĐÃ TỒN TẠI - XỬ LÝ BỎ QUA LỖI)
-- =============================================================================
BEGIN
    -- Xóa các bảng theo thứ tự phụ thuộc (bảng con trước, bảng cha sau)
    FOR t IN (
        SELECT table_name 
        FROM user_tables 
        WHERE table_name IN ('CT_PHIEU_NHAP', 'PHIEU_NHAP', 'NGUYEN_LIEU', 'NHA_CUNG_CAP')
    ) LOOP
        EXECUTE IMMEDIATE 'DROP TABLE ' || t.table_name || ' CASCADE CONSTRAINTS';
    END LOOP;

    -- Xóa các sequence cũ
    FOR s IN (
        SELECT sequence_name 
        FROM user_sequences 
        WHERE sequence_name IN ('SEQ_NHA_CUNG_CAP', 'SEQ_NGUYEN_LIEU', 'SEQ_PHIEU_NHAP')
    ) LOOP
        EXECUTE IMMEDIATE 'DROP SEQUENCE ' || s.sequence_name;
    END LOOP;
END;
/

-- =============================================================================
-- 2. TẠO SEQUENCE CHO CÁC KHÓA CHÍNH TỰ TĂNG
-- =============================================================================

-- Sequence tự tăng cho mã nhà cung cấp
CREATE SEQUENCE SEQ_NHA_CUNG_CAP
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Sequence tự tăng cho mã nguyên liệu
CREATE SEQUENCE SEQ_NGUYEN_LIEU
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Sequence tự tăng cho mã phiếu nhập
CREATE SEQUENCE SEQ_PHIEU_NHAP
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- =============================================================================
-- 3. TẠO CẤU TRÚC CÁC BẢNG DỮ LIỆU
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Bảng 1: NHA_CUNG_CAP (Thông tin nhà cung cấp hải sản và nguyên liệu)
-- -----------------------------------------------------------------------------
CREATE TABLE NHA_CUNG_CAP (
    MA_NCC              NUMBER              CONSTRAINT PK_NHA_CUNG_CAP PRIMARY KEY,
    TEN_NCC             VARCHAR2(200)       NOT NULL,
    DIA_CHI             VARCHAR2(500),
    SDT                 VARCHAR2(15),
    EMAIL               VARCHAR2(100)
);

-- Chú thích (Comments) tiếng Việt cho bảng NHA_CUNG_CAP
COMMENT ON TABLE NHA_CUNG_CAP IS 'Bảng lưu trữ danh bạ thông tin các nhà cung cấp hải sản và nguyên liệu';
COMMENT ON COLUMN NHA_CUNG_CAP.MA_NCC IS 'Mã nhà cung cấp (Khóa chính, tự động tăng qua SEQ_NHA_CUNG_CAP)';
COMMENT ON COLUMN NHA_CUNG_CAP.TEN_NCC IS 'Tên nhà cung cấp / đối tác cung ứng';
COMMENT ON COLUMN NHA_CUNG_CAP.DIA_CHI IS 'Địa chỉ văn phòng / cơ sở kinh doanh của nhà cung cấp';
COMMENT ON COLUMN NHA_CUNG_CAP.SDT IS 'Số điện thoại liên lạc của nhà cung cấp';
COMMENT ON COLUMN NHA_CUNG_CAP.EMAIL IS 'Địa chỉ thư điện tử (email) của nhà cung cấp';

-- -----------------------------------------------------------------------------
-- Bảng 2: NGUYEN_LIEU (Thông tin nguyên liệu, hải sản và tồn kho)
-- -----------------------------------------------------------------------------
CREATE TABLE NGUYEN_LIEU (
    MA_NL               NUMBER              CONSTRAINT PK_NGUYEN_LIEU PRIMARY KEY,
    TEN_NL              VARCHAR2(200)       NOT NULL,
    DON_VI_TINH         VARCHAR2(50)        NOT NULL,
    SO_LUONG_TON        NUMBER(10,2)        DEFAULT 0,
    GIA_NHAP            NUMBER(15,2)        DEFAULT 0
);

-- Chú thích (Comments) tiếng Việt cho bảng NGUYEN_LIEU
COMMENT ON TABLE NGUYEN_LIEU IS 'Bảng danh mục nguyên vật liệu, hải sản tươi sống và gia vị dùng trong nhà hàng';
COMMENT ON COLUMN NGUYEN_LIEU.MA_NL IS 'Mã nguyên liệu (Khóa chính, tự động tăng qua SEQ_NGUYEN_LIEU)';
COMMENT ON COLUMN NGUYEN_LIEU.TEN_NL IS 'Tên nguyên liệu / loại hải sản (ví dụ: Tôm hùm, Cua hoàng đế,...)';
COMMENT ON COLUMN NGUYEN_LIEU.DON_VI_TINH IS 'Đơn vị đo lường (kg, con, lít, hộp, con,...)';
COMMENT ON COLUMN NGUYEN_LIEU.SO_LUONG_TON IS 'Số lượng nguyên liệu hiện còn tồn trữ trong kho';
COMMENT ON COLUMN NGUYEN_LIEU.GIA_NHAP IS 'Đơn giá nhập vào gần nhất tính theo đơn vị tính (VND)';

-- -----------------------------------------------------------------------------
-- Bảng 3: PHIEU_NHAP (Thông tin hóa đơn / phiếu nhập kho hải sản)
-- -----------------------------------------------------------------------------
CREATE TABLE PHIEU_NHAP (
    MA_PN               NUMBER              CONSTRAINT PK_PHIEU_NHAP PRIMARY KEY,
    MA_NCC              NUMBER              NOT NULL,
    NGAY_NHAP           DATE                DEFAULT SYSDATE,
    TONG_TIEN           NUMBER(15,2)        DEFAULT 0,
    GHI_CHU             VARCHAR2(500),
    CONSTRAINT FK_PHIEU_NHAP_NCC FOREIGN KEY (MA_NCC) REFERENCES NHA_CUNG_CAP(MA_NCC)
);

-- Chú thích (Comments) tiếng Việt cho bảng PHIEU_NHAP
COMMENT ON TABLE PHIEU_NHAP IS 'Bảng thông tin tổng quan các phiếu nhập kho nguyên liệu từ nhà cung cấp';
COMMENT ON COLUMN PHIEU_NHAP.MA_PN IS 'Mã phiếu nhập (Khóa chính, tự động tăng qua SEQ_PHIEU_NHAP)';
COMMENT ON COLUMN PHIEU_NHAP.MA_NCC IS 'Mã nhà cung cấp xuất hàng (Khóa ngoại tham chiếu NHA_CUNG_CAP)';
COMMENT ON COLUMN PHIEU_NHAP.NGAY_NHAP IS 'Ngày giờ thực hiện lập phiếu và nhập hàng vào kho';
COMMENT ON COLUMN PHIEU_NHAP.TONG_TIEN IS 'Tổng giá trị đơn nhập hàng (VND)';
COMMENT ON COLUMN PHIEU_NHAP.GHI_CHU IS 'Ghi chú về lô hàng nhập (chất lượng, tình trạng, xe giao hàng,...)';

-- -----------------------------------------------------------------------------
-- Bảng 4: CT_PHIEU_NHAP (Chi tiết các mặt hàng trong từng phiếu nhập)
-- -----------------------------------------------------------------------------
CREATE TABLE CT_PHIEU_NHAP (
    MA_PN               NUMBER              NOT NULL,
    MA_NL               NUMBER              NOT NULL,
    SO_LUONG            NUMBER(10,2)        NOT NULL,
    DON_GIA             NUMBER(15,2)        NOT NULL,
    THANH_TIEN          NUMBER(15,2),
    CONSTRAINT PK_CT_PHIEU_NHAP PRIMARY KEY (MA_PN, MA_NL),
    CONSTRAINT FK_CTPN_PHIEU_NHAP FOREIGN KEY (MA_PN) REFERENCES PHIEU_NHAP(MA_PN) ON DELETE CASCADE,
    CONSTRAINT FK_CTPN_NGUYEN_LIEU FOREIGN KEY (MA_NL) REFERENCES NGUYEN_LIEU(MA_NL)
);

-- Chú thích (Comments) tiếng Việt cho bảng CT_PHIEU_NHAP
COMMENT ON TABLE CT_PHIEU_NHAP IS 'Bảng chi tiết nguyên liệu và số lượng của từng phiếu nhập kho';
COMMENT ON COLUMN CT_PHIEU_NHAP.MA_PN IS 'Mã phiếu nhập (Khóa chính kết hợp, khóa ngoại tham chiếu PHIEU_NHAP)';
COMMENT ON COLUMN CT_PHIEU_NHAP.MA_NL IS 'Mã nguyên liệu (Khóa chính kết hợp, khóa ngoại tham chiếu NGUYEN_LIEU)';
COMMENT ON COLUMN CT_PHIEU_NHAP.SO_LUONG IS 'Số lượng nguyên liệu nhập về trong lần này';
COMMENT ON COLUMN CT_PHIEU_NHAP.DON_GIA IS 'Đơn giá nhập thực tế tại thời điểm lập phiếu (VND)';
COMMENT ON COLUMN CT_PHIEU_NHAP.THANH_TIEN IS 'Thành tiền = Số lượng * Đơn giá (VND)';

-- =============================================================================
-- 4. TẠO CÁC TRIGGER TỰ ĐỘNG HÓA
-- =============================================================================

-- Trigger tự động gán ID cho NHA_CUNG_CAP nếu không truyền vào
CREATE OR REPLACE TRIGGER TRG_NHA_CUNG_CAP_BI
BEFORE INSERT ON NHA_CUNG_CAP
FOR EACH ROW
BEGIN
    IF :NEW.MA_NCC IS NULL THEN
        SELECT SEQ_NHA_CUNG_CAP.NEXTVAL INTO :NEW.MA_NCC FROM DUAL;
    END IF;
END;
/

-- Trigger tự động gán ID cho NGUYEN_LIEU nếu không truyền vào
CREATE OR REPLACE TRIGGER TRG_NGUYEN_LIEU_BI
BEFORE INSERT ON NGUYEN_LIEU
FOR EACH ROW
BEGIN
    IF :NEW.MA_NL IS NULL THEN
        SELECT SEQ_NGUYEN_LIEU.NEXTVAL INTO :NEW.MA_NL FROM DUAL;
    END IF;
END;
/

-- Trigger tự động gán ID cho PHIEU_NHAP nếu không truyền vào
CREATE OR REPLACE TRIGGER TRG_PHIEU_NHAP_BI
BEFORE INSERT ON PHIEU_NHAP
FOR EACH ROW
BEGIN
    IF :NEW.MA_PN IS NULL THEN
        SELECT SEQ_PHIEU_NHAP.NEXTVAL INTO :NEW.MA_PN FROM DUAL;
    END IF;
END;
/

-- Trigger tự động tính THANH_TIEN trong CT_PHIEU_NHAP nếu chưa được gán
CREATE OR REPLACE TRIGGER TRG_CT_PHIEU_NHAP_BI
BEFORE INSERT OR UPDATE ON CT_PHIEU_NHAP
FOR EACH ROW
BEGIN
    IF :NEW.THANH_TIEN IS NULL THEN
        :NEW.THANH_TIEN := :NEW.SO_LUONG * :NEW.DON_GIA;
    END IF;
END;
/

-- =============================================================================
-- 5. CHÈN DỮ LIỆU MẪU (SAMPLE DATA)
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 5.1. Dữ liệu mẫu 5 Nhà Cung Cấp (NHA_CUNG_CAP)
-- -----------------------------------------------------------------------------
INSERT INTO NHA_CUNG_CAP (MA_NCC, TEN_NCC, DIA_CHI, SDT, EMAIL)
VALUES (SEQ_NHA_CUNG_CAP.NEXTVAL, 'Hải Sản Tươi Sống Vũng Tàu', '123 Thùy Vân, Phường Thắng Tam, TP. Vũng Tàu', '0901234567', 'haisanvungtau@gmail.com');

INSERT INTO NHA_CUNG_CAP (MA_NCC, TEN_NCC, DIA_CHI, SDT, EMAIL)
VALUES (SEQ_NHA_CUNG_CAP.NEXTVAL, 'Vựa Cua Biển Năm Căn Cà Mau', '45 Khóm 2, TT. Năm Căn, Huyện Năm Căn, Cà Mau', '0912345678', 'cuanamcan.camau@gmail.com');

INSERT INTO NHA_CUNG_CAP (MA_NCC, TEN_NCC, DIA_CHI, SDT, EMAIL)
VALUES (SEQ_NHA_CUNG_CAP.NEXTVAL, 'Vựa Hải Sản Đảo Phú Quốc', '78 Trần Phú, Phường Dương Đông, TP. Phú Quốc', '0987654321', 'phuquocseafood@phuquoc.vn');

INSERT INTO NHA_CUNG_CAP (MA_NCC, TEN_NCC, DIA_CHI, SDT, EMAIL)
VALUES (SEQ_NHA_CUNG_CAP.NEXTVAL, 'Công Ty TNHH Thực Phẩm Biển Xanh Nha Trang', '234 Lê Lợi, Phường Xương Huân, TP. Nha Trang', '0945678901', 'bienxanh.nhatrang@seafood.vn');

INSERT INTO NHA_CUNG_CAP (MA_NCC, TEN_NCC, DIA_CHI, SDT, EMAIL)
VALUES (SEQ_NHA_CUNG_CAP.NEXTVAL, 'Hợp Tác Xã Nông Sản & Gia Vị Đà Lạt', 'Lô B4 Chợ Đầu Mối Nông Sản, TP. Đà Lạt, Lâm Đồng', '0933456789', 'giavi.dalatfarm@dalat.vn');

-- -----------------------------------------------------------------------------
-- 5.2. Dữ liệu mẫu 8 Nguyên Liệu Hải Sản (NGUYEN_LIEU)
-- -----------------------------------------------------------------------------
INSERT INTO NGUYEN_LIEU (MA_NL, TEN_NL, DON_VI_TINH, SO_LUONG_TON, GIA_NHAP)
VALUES (SEQ_NGUYEN_LIEU.NEXTVAL, 'Tôm hùm bông sống', 'kg', 25.50, 1500000);

INSERT INTO NGUYEN_LIEU (MA_NL, TEN_NL, DON_VI_TINH, SO_LUONG_TON, GIA_NHAP)
VALUES (SEQ_NGUYEN_LIEU.NEXTVAL, 'Cua hoàng đế (King Crab)', 'kg', 15.00, 2200000);

INSERT INTO NGUYEN_LIEU (MA_NL, TEN_NL, DON_VI_TINH, SO_LUONG_TON, GIA_NHAP)
VALUES (SEQ_NGUYEN_LIEU.NEXTVAL, 'Cua gạch Cà Mau loại 1', 'kg', 40.00, 450000);

INSERT INTO NGUYEN_LIEU (MA_NL, TEN_NL, DON_VI_TINH, SO_LUONG_TON, GIA_NHAP)
VALUES (SEQ_NGUYEN_LIEU.NEXTVAL, 'Mực lá tươi Phú Quốc', 'kg', 60.00, 320000);

INSERT INTO NGUYEN_LIEU (MA_NL, TEN_NL, DON_VI_TINH, SO_LUONG_TON, GIA_NHAP)
VALUES (SEQ_NGUYEN_LIEU.NEXTVAL, 'Ốc hương biển sống', 'kg', 50.00, 420000);

INSERT INTO NGUYEN_LIEU (MA_NL, TEN_NL, DON_VI_TINH, SO_LUONG_TON, GIA_NHAP)
VALUES (SEQ_NGUYEN_LIEU.NEXTVAL, 'Cá mú đỏ sống (Cá song)', 'kg', 30.00, 680000);

INSERT INTO NGUYEN_LIEU (MA_NL, TEN_NL, DON_VI_TINH, SO_LUONG_TON, GIA_NHAP)
VALUES (SEQ_NGUYEN_LIEU.NEXTVAL, 'Sò dương tươi sống', 'kg', 80.00, 180000);

INSERT INTO NGUYEN_LIEU (MA_NL, TEN_NL, DON_VI_TINH, SO_LUONG_TON, GIA_NHAP)
VALUES (SEQ_NGUYEN_LIEU.NEXTVAL, 'Ghẹ xanh sống Phan Thiết', 'kg', 45.00, 380000);

-- -----------------------------------------------------------------------------
-- 5.3. Dữ liệu mẫu 3 Phiếu Nhập Hàng (PHIEU_NHAP)
-- -----------------------------------------------------------------------------
-- Phiếu 1: Nhập từ Hải Sản Vũng Tàu (MA_NCC = 1)
INSERT INTO PHIEU_NHAP (MA_PN, MA_NCC, NGAY_NHAP, TONG_TIEN, GHI_CHU)
VALUES (
    SEQ_PHIEU_NHAP.NEXTVAL,
    1,
    TO_DATE('2026-10-10 08:30:00', 'YYYY-MM-DD HH24:MI:SS'),
    32900000,
    'Nhập hải sản tươi sống sáng sớm ngày 10/10/2026'
);

-- Phiếu 2: Nhập từ Cua Biển Cà Mau (MA_NCC = 2)
INSERT INTO PHIEU_NHAP (MA_PN, MA_NCC, NGAY_NHAP, TONG_TIEN, GHI_CHU)
VALUES (
    SEQ_PHIEU_NHAP.NEXTVAL,
    2,
    TO_DATE('2026-10-11 09:15:00', 'YYYY-MM-DD HH24:MI:SS'),
    13500000,
    'Nhập cua gạch Cà Mau tuyển chọn phục vụ khách đặt tiệc'
);

-- Phiếu 3: Nhập từ Biển Xanh Nha Trang (MA_NCC = 4)
INSERT INTO PHIEU_NHAP (MA_PN, MA_NCC, NGAY_NHAP, TONG_TIEN, GHI_CHU)
VALUES (
    SEQ_PHIEU_NHAP.NEXTVAL,
    4,
    TO_DATE('2026-10-12 14:00:00', 'YYYY-MM-DD HH24:MI:SS'),
    50600000,
    'Nhập bổ sung hải sản cao cấp King Crab, cá mú và mực lá chuẩn bị cho dịp cuối tuần'
);

-- -----------------------------------------------------------------------------
-- 5.4. Dữ liệu mẫu Chi Tiết Phiếu Nhập (CT_PHIEU_NHAP)
-- -----------------------------------------------------------------------------

-- Chi tiết Phiếu nhập 1 (Tổng cộng = 15.000.000 + 8.400.000 + 9.500.000 = 32.900.000 VND)
INSERT INTO CT_PHIEU_NHAP (MA_PN, MA_NL, SO_LUONG, DON_GIA, THANH_TIEN)
VALUES (1, 1, 10.00, 1500000, 15000000);  -- 10 kg Tôm hùm bông

INSERT INTO CT_PHIEU_NHAP (MA_PN, MA_NL, SO_LUONG, DON_GIA, THANH_TIEN)
VALUES (1, 5, 20.00, 420000, 8400000);    -- 20 kg Ốc hương biển

INSERT INTO CT_PHIEU_NHAP (MA_PN, MA_NL, SO_LUONG, DON_GIA, THANH_TIEN)
VALUES (1, 8, 25.00, 380000, 9500000);    -- 25 kg Ghẹ xanh

-- Chi tiết Phiếu nhập 2 (Tổng cộng = 13.500.000 VND)
INSERT INTO CT_PHIEU_NHAP (MA_PN, MA_NL, SO_LUONG, DON_GIA, THANH_TIEN)
VALUES (2, 3, 30.00, 450000, 13500000);   -- 30 kg Cua gạch Cà Mau

-- Chi tiết Phiếu nhập 3 (Tổng cộng = 22.000.000 + 11.200.000 + 10.200.000 + 7.200.000 = 50.600.000 VND)
INSERT INTO CT_PHIEU_NHAP (MA_PN, MA_NL, SO_LUONG, DON_GIA, THANH_TIEN)
VALUES (3, 2, 10.00, 2200000, 22000000);  -- 10 kg Cua hoàng đế

INSERT INTO CT_PHIEU_NHAP (MA_PN, MA_NL, SO_LUONG, DON_GIA, THANH_TIEN)
VALUES (3, 4, 35.00, 320000, 11200000);   -- 35 kg Mực lá Phú Quốc

INSERT INTO CT_PHIEU_NHAP (MA_PN, MA_NL, SO_LUONG, DON_GIA, THANH_TIEN)
VALUES (3, 6, 15.00, 680000, 10200000);   -- 15 kg Cá mú đỏ

INSERT INTO CT_PHIEU_NHAP (MA_PN, MA_NL, SO_LUONG, DON_GIA, THANH_TIEN)
VALUES (3, 7, 40.00, 180000, 7200000);    -- 40 kg Sò dương sống

-- =============================================================================
-- 6. XÁC NHẬN GIAO DỊCH (COMMIT)
-- =============================================================================
COMMIT;
