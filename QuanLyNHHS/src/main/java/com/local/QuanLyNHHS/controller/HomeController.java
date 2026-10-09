package com.local.QuanLyNHHS.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/hello")
    public String helloPage(Model model) {
        // Truyền một biến tên là "message" sang cho file HTML
        model.addAttribute("message", "Hello World từ Spring Boot & Thymeleaf!");
        
        // Trả về tên file HTML (không cần đuôi .html) nằm trong thư mục templates
        return "hello";
    }
}