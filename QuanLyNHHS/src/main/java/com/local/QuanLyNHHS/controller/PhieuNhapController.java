package com.local.QuanLyNHHS.controller;

import com.local.QuanLyNHHS.model.PhieuNhap;
import com.local.QuanLyNHHS.service.PhieuNhapService;
import com.local.QuanLyNHHS.service.NhaCungCapService;
import com.local.QuanLyNHHS.service.NguyenLieuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Date;

@Controller
@RequestMapping("/phieu-nhap")
public class PhieuNhapController {

    @Autowired
    private PhieuNhapService phieuNhapService;

    @Autowired
    private NhaCungCapService nhaCungCapService;

    @Autowired
    private NguyenLieuService nguyenLieuService;

    // Hiển thị danh sách phiếu nhập
    @GetMapping
    public String danhSach(Model model) {
        model.addAttribute("danhSachPN", phieuNhapService.layTatCa());
        return "phieunhap/danh-sach";
    }

    // Hiển thị form tạo phiếu nhập mới
    @GetMapping("/them")
    public String formThem(Model model) {
        model.addAttribute("phieuNhap", new PhieuNhap());
        model.addAttribute("danhSachNCC", nhaCungCapService.layTatCa());
        return "phieunhap/form";
    }

    // Xử lý tạo phiếu nhập mới
    @PostMapping("/them")
    public String themMoi(@RequestParam Long maNcc,
                          @RequestParam(required = false) String ghiChu,
                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayNhap,
                          RedirectAttributes redirectAttributes) {
        try {
            PhieuNhap phieuNhap = phieuNhapService.taoPhieuNhap(maNcc, ghiChu, ngayNhap);
            redirectAttributes.addFlashAttribute("thongBao", "Tạo phiếu nhập thành công! Mã phiếu: " + phieuNhap.getMaPn());
            return "redirect:/phieu-nhap/chi-tiet/" + phieuNhap.getMaPn();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loi", "Lỗi: " + e.getMessage());
            return "redirect:/phieu-nhap/them";
        }
    }

    // Xem chi tiết phiếu nhập
    @GetMapping("/chi-tiet/{id}")
    public String chiTiet(@PathVariable Long id, Model model) {
        phieuNhapService.timTheoId(id).ifPresent(pn -> {
            model.addAttribute("phieuNhap", pn);
            model.addAttribute("danhSachChiTiet", phieuNhapService.layChiTietTheoPhieuNhap(id));
        });
        model.addAttribute("danhSachNL", nguyenLieuService.layTatCa());
        return "phieunhap/chi-tiet";
    }

    // Thêm chi tiết (nguyên liệu) vào phiếu nhập
    @PostMapping("/them-chi-tiet")
    public String themChiTiet(@RequestParam Long maPn,
                              @RequestParam Long maNl,
                              @RequestParam Double soLuong,
                              @RequestParam Double donGia,
                              RedirectAttributes redirectAttributes) {
        try {
            phieuNhapService.themChiTiet(maPn, maNl, soLuong, donGia);
            redirectAttributes.addFlashAttribute("thongBao", "Thêm nguyên liệu vào phiếu nhập thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loi", "Lỗi: " + e.getMessage());
        }
        return "redirect:/phieu-nhap/chi-tiet/" + maPn;
    }

    // Xóa chi tiết khỏi phiếu nhập
    @GetMapping("/xoa-chi-tiet/{maPn}/{maNl}")
    public String xoaChiTiet(@PathVariable Long maPn, @PathVariable Long maNl, RedirectAttributes redirectAttributes) {
        try {
            phieuNhapService.xoaChiTiet(maPn, maNl);
            redirectAttributes.addFlashAttribute("thongBao", "Xóa nguyên liệu khỏi phiếu nhập thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loi", "Lỗi: " + e.getMessage());
        }
        return "redirect:/phieu-nhap/chi-tiet/" + maPn;
    }

    // Xóa phiếu nhập
    @GetMapping("/xoa/{id}")
    public String xoaPhieuNhap(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            phieuNhapService.xoaPhieuNhap(id);
            redirectAttributes.addFlashAttribute("thongBao", "Xóa phiếu nhập thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loi", "Lỗi: " + e.getMessage());
        }
        return "redirect:/phieu-nhap";
    }

    // Lọc theo khoảng ngày
    @GetMapping("/loc")
    public String locTheoNgay(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date tuNgay,
                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date denNgay,
                              Model model) {
        model.addAttribute("danhSachPN", phieuNhapService.layTheoKhoangNgay(tuNgay, denNgay));
        model.addAttribute("tuNgay", tuNgay);
        model.addAttribute("denNgay", denNgay);
        return "phieunhap/danh-sach";
    }
}
