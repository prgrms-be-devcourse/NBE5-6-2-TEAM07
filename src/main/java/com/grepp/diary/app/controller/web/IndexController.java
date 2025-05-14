package com.grepp.diary.app.controller.web;

import com.grepp.diary.app.controller.web.auth.form.SigninForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class IndexController {

    @GetMapping("/")
    public String index(Model model) {
        if (!model.containsAttribute("signinForm")) {
            model.addAttribute("signinForm", new SigninForm());
        }
        return "index";
    }
    
}
