package com.auca.VotingApp2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home() {
        return "landing";
    }

//    @GetMapping("/register")
//    public String showRegistrationForm() {
//        return "register"; // Should correspond to register.html in templates folder
//    }


//    @PostMapping("/register")
//    public String register() {
//        return "register";
//    }

}

