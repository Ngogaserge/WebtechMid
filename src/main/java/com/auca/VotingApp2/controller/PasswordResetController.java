package com.auca.VotingApp2.controller;



import com.auca.VotingApp2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PasswordResetController {

    @Autowired
    private UserService userService;

    // Forgot Password Form
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password"; // Forgot password form page
    }

    // Handle Forgot Password Request
    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam("email") String email, Model model) {
        // Check if the user exists
        if (!userService.doesEmailExist(email)) {
            model.addAttribute("error", "Email address not found.");
            return "forgot-password"; // Redirect back to forgot password page
        }

        // Delete existing token if it exists
        userService.deleteExistingResetTokenByEmail(email);

        // Generate password reset token and send email
        boolean emailSent = userService.sendPasswordResetEmail(email);

        if (emailSent) {
            model.addAttribute("message", "A reset link has been sent to your email.");
        } else {
            model.addAttribute("error", "Failed to send email. Please try again.");
        }

        return "forgot-password"; // Redirect back to forgot password page
    }

    // Password Reset Page
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        boolean isValidToken = userService.validatePasswordResetToken(token);

        if (!isValidToken) {
            model.addAttribute("error", "Invalid or expired password reset token.");
            return "forgot-password"; // Redirect back to forgot password page
        }

        model.addAttribute("token", token);
        return "reset-password"; // Password reset form page
    }

    @PostMapping("/reset-password")
    public String handlePasswordReset(@RequestParam("token") String token,
                                      @RequestParam("newPassword") String newPassword,
                                      @RequestParam("confirmNewPassword") String confirmNewPassword,
                                      Model model) {
        System.out.println("Received token for validation: " + token); // Log the token value

        // Check if newPassword matches confirmNewPassword
        if (!newPassword.equals(confirmNewPassword)) {
            model.addAttribute("error", "Passwords do not match. Please try again.");
            return "reset-password"; // Show reset password page again with error
        }

        boolean isResetSuccessful = userService.resetUserPassword(token, newPassword);

        if (isResetSuccessful) {
            model.addAttribute("message", "Your password has been successfully reset. You can now log in.");
            return "login"; // Redirect to login page
        } else {
            model.addAttribute("error", "Failed to reset password. Please try again.");
            return "reset-password"; // Show reset password page again with error
        }
    }


}
