package com.local.QuanLyNHHS.controller;

import com.local.QuanLyNHHS.model.NhaCungCap;
import com.local.QuanLyNHHS.service.NhaCungCapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/nha-cung-cap")
public class NhaCungCapController {

    @Autowired
    private NhaCungCapService nhaCungCapService;

    // Hiển thị danh sách nhà cung cấp
    @GetMapping
    public String danhSach(Model model) {
        model.addAttribute("danhSachNCC", nhaCungCapService.layTatCa());
        return "nhacungcap/danh-sach";
    }

    // Hiển thị form thêm mới
    @GetMapping("/them")
    public String formThem(Model model) {
        model.addAttribute("nhaCungCap", new NhaCungCap());
        return "nhacungcap/form";
    }

    // Xử lý thêm mới
    @PostMapping("/them")
    public String themMoi(@ModelAttribute NhaCungCap nhaCungCap, RedirectAttributes redirectAttributes) {
        try {
            nhaCungCapService.themMoi(nhaCungCap);
            redirectAttributes.addFlashAttribute("thongBao", "Thêm nhà cung cấp thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loi", "Lỗi: " + e.getMessage());
        }
        return "redirect:/nha-cung-cap";
    }

    // Hiển thị form sửa
    @GetMapping("/sua/{id}")
    public String formSua(@PathVariable Long id, Model model) {
        nhaCungCapService.timTheoId(id).ifPresent(ncc -> model.addAttribute("nhaCungCap", ncc));
        return "nhacungcap/form";
    }

    // Xử lý cập nhật
    @PostMapping("/sua/{id}")
    public String capNhat(@PathVariable Long id, @ModelAttribute NhaCungCap nhaCungCap, RedirectAttributes redirectAttributes) {
        try {
            nhaCungCapService.capNhat(id, nhaCungCap);
            redirectAttributes.addFlashAttribute("thongBao", "Cập nhật nhà cung cấp thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loi", "Lỗi: " + e.getMessage());
        }
        return "redirect:/nha-cung-cap";
    }

    // Xóa
    @GetMapping("/xoa/{id}")
    public String xoa(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            nhaCungCapService.xoa(id);
            redirectAttributes.addFlashAttribute("thongBao", "Xóa nhà cung cấp thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("loi", "Lỗi: " + e.getMessage());
        }
        return "redirect:/nha-cung-cap";
    }

    // Tìm kiếm
    @GetMapping("/tim-kiem")
    public String timKiem(@RequestParam String tuKhoa, Model model) {
        model.addAttribute("danhSachNCC", nhaCungCapService.timTheoTen(tuKhoa));
        model.addAttribute("tuKhoa", tuKhoa);
        return "nhacungcap/danh-sach";
    }
}
