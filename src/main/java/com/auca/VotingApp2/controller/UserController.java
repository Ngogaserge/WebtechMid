package com.auca.VotingApp2.controller;



import com.auca.VotingApp2.model.Role;
import com.auca.VotingApp2.model.User;
import com.auca.VotingApp2.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;


@Controller
public class UserController {

    @Autowired
    private UserService userService;



    @Autowired
    private MessageSource messageSource; // Inject MessageSource for localization


    @GetMapping("/home")
    public String home() {
        return "index"; // Home page
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; // Template name for registration
    }

    @PostMapping("/register")
    public String registerUser (@ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register"; // If form validation fails, return the registration page
        }

        // Register user
        userService.registerUser (user);
        model.addAttribute("message", "Registration successful! You can log in now.");
        return "login"; // Redirect to login page
    }



    @GetMapping("/login")
    public String showLoginForm(Model model) {
        // Add an error message if it exists
        model.addAttribute("error", model.getAttribute("error"));
        return "login"; // Template name for login
    }

    @PostMapping("/login")
    public String loginUser (@RequestParam String username,
                             @RequestParam String password,
                             HttpSession session,
                             Model model) {
        User user = userService.loginUser (username);

        if (user == null || !user.getPassword().equals(password)) {
            model.addAttribute("error", "Invalid username or password");
            return "login"; // Show login page again with error message
        }

        // Set user information in session
        session.setAttribute("loggedInUser ", user); // Ensure no space in the attribute name

        // Redirect based on the role
        switch (user.getRole()) {
            case ROLE_ADMIN:
                return "redirect:/admin"; // Redirect to admin dashboard
            case ROLE_SELLER:
                return "redirect:/seller"; // Redirect to seller page
            case ROLE_USER:
                return "redirect:/voter"; // Redirect to customer page
            default:
                return "redirect:/login"; // Default redirect if no role matches
        }
    }



//    @GetMapping("/admin")
//    public String showAdminDashboard(HttpSession session, Model model) {
//        User user = (User ) session.getAttribute("loggedInUser ");
//        if (user == null || user.getRole() != Role.ROLE_ADMIN) {
//            return "redirect:/login"; // Redirect to login if not logged in or not admin
//        }
//
//        // Retrieve all users for the admin dashboard
//        List<User> users = userService.getAllUsers();
//        model.addAttribute("users", users); // Add the users list to the model
//        return "admin-dashboard"; // Return the admin dashboard template
//    }

    @GetMapping("/admin")
    public String showAdminDashboard(HttpSession session, Model model,
                                     @RequestParam(defaultValue = "0") int pageNo,
                                     @RequestParam(defaultValue = "10") int pageSize,
                                     @RequestParam(defaultValue = "id") String sortBy) {
        User user = (User ) session.getAttribute("loggedInUser ");
        if (user == null || user.getRole() != Role.ROLE_ADMIN) {
            return "redirect:/login"; // Redirect to login if not logged in or not admin
        }

        // Create a Pageable object for pagination and sorting
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        Page<User> userPage = userService.getAllUsers(pageable); // Fetch paginated users

        model.addAttribute("users", userPage.getContent()); // Add the users list to the model
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalUsers", userPage.getTotalElements());
        model.addAttribute("sortBy", sortBy); // Add sortBy to the model for UI

        return "admin-dashboard"; // Return the admin dashboard template
    }



    @GetMapping("/seller")
    public String sellerPage(HttpSession session) {
        User user = (User ) session.getAttribute("loggedInUser ");
        if (user == null || user.getRole() != Role.ROLE_SELLER) {
            return "redirect:/login"; // Redirect to login if not logged in or not seller
        }
        return "seller"; // Seller dashboard template
    }

    @GetMapping("/customer")
    public String customerPage(HttpSession session) {
        User user = (User ) session.getAttribute("loggedInUser ");
        if (user == null || user.getRole() != Role.ROLE_USER) {
            return "redirect:/login"; // Redirect to log in if not logged in or not customer
        }
        return "customer"; // Customer dashboard template
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        // Log the logout action
        System.out.println("Logging out user: " + session.getAttribute("loggedInUser "));

        session.invalidate(); // Invalidate session on logout

        // Log after invalidation
        System.out.println("Session invalidated.");

        // Add a flash attribute for the logout message
        redirectAttributes.addFlashAttribute("message", "You are logged out.");

        return "redirect:/login"; // Redirect to login after logout
    }



    @GetMapping("/e-showroom")
    public String showEshowroomPage() {
        return "e-showroom"; // Home page
    }





}