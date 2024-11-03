package com.auca.VotingApp2.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@Controller
public class LanguageController {

    private final LocaleResolver localeResolver;

    public LanguageController(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    @GetMapping("/changeLanguage")
    public String changeLanguage(@RequestParam("lang") String lang,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        // Validate the language parameter
        if (lang == null || lang.isEmpty()) {
            lang = "en"; // Default to English if no valid language is provided
        }

        Locale locale = new Locale(lang);
        localeResolver.setLocale(request, response, locale); // Set the locale
        return "redirect:/"; // Redirect to the homepage or the desired page
    }
}