package com.sinaukoding.librarymanagementsystem.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerRedirectController {

    @GetMapping("/swagger")
    public String redirect(HttpServletRequest request) {
        return "redirect:/swagger-ui.html";
    }

    @GetMapping("/swagger/")
    public String redirectWithSlash(HttpServletRequest request) {
        return "redirect:/swagger-ui.html";
    }
}
