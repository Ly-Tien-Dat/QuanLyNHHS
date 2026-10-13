package com.local.QuanLyNHHS.controller;

import com.local.QuanLyNHHS.model.NguyenLieu;
import com.local.QuanLyNHHS.service.NguyenLieuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/nguyen-lieu")
public class NguyenLieuController {

    @Autowired
    private NguyenLieuService nguyenLieuService;

    // Hiển thị danh sách nguyên liệu (tồn kho)
    @GetMapping
    public String danhSach(Model model) {
        model.addAttribute("danhSachNL", nguyenLieuService.layTatCa());
        return "nguyenlieu/danh-sach";
    }

    // Hiển thị form thêm mới
    @GetMapping("/them")
    public String formThem(Model model) {
        model.addAttribute("nguyenLieu", new NguyenLieu());
        return "nguyenlieu/form";
    }

    // Xử lý thêm mới
    @PostMapping("/them")
    public String themMoi(@ModelAttribute NguyenLieu nguyenLieu, RedirectAttributes redirectAttributes) {
        try {
            nguyenLieuService.themMoi(nguyenLieu);
            redirectAttributes.addFlashAttribute("thongBao", "Thêm nguyên liệu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loi", "Lỗi: " + e.getMessage());
        }
        return "redirect:/nguyen-lieu";
    }

    // Hiển thị form sửa
    @GetMapping("/sua/{id}")
    public String formSua(@PathVariable Long id, Model model) {
        nguyenLieuService.timTheoId(id).ifPresent(nl -> model.addAttribute("nguyenLieu", nl));
        return "nguyenlieu/form";
    }

    // Xử lý cập nhật
    @PostMapping("/sua/{id}")
    public String capNhat(@PathVariable Long id, @ModelAttribute NguyenLieu nguyenLieu, RedirectAttributes redirectAttributes) {
        try {
            nguyenLieuService.capNhat(id, nguyenLieu);
            redirectAttributes.addFlashAttribute("thongBao", "Cập nhật nguyên liệu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loi", "Lỗi: " + e.getMessage());
        }
        return "redirect:/nguyen-lieu";
    }

    // Xóa
    @GetMapping("/xoa/{id}")
    public String xoa(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            nguyenLieuService.xoa(id);
            redirectAttributes.addFlashAttribute("thongBao", "Xóa nguyên liệu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loi", "Lỗi: " + e.getMessage());
        }
        return "redirect:/nguyen-lieu";
    }

    // Tìm kiếm
    @GetMapping("/tim-kiem")
    public String timKiem(@RequestParam String tuKhoa, Model model) {
        model.addAttribute("danhSachNL", nguyenLieuService.timTheoTen(tuKhoa));
        model.addAttribute("tuKhoa", tuKhoa);
        return "nguyenlieu/danh-sach";
    }

    // Xem nguyên liệu sắp hết
    @GetMapping("/sap-het")
    public String sapHet(Model model) {
        model.addAttribute("danhSachNL", nguyenLieuService.laySapHetHang(10.0));
        model.addAttribute("canhBao", true);
        return "nguyenlieu/danh-sach";
    }
}
