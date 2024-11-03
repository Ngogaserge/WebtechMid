package com.auca.VotingApp2.controller;


import com.auca.VotingApp2.model.Role;
import com.auca.VotingApp2.model.User;
import com.auca.VotingApp2.service.UserService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        return "add-user";
    }

    @PostMapping("/admin/users")
    public String addUser(@ModelAttribute User user) {
        userService.registerUser(user);
        return "redirect:/admin";
    }

    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/users/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "edit-user";
    }

    @PostMapping("/admin/users/update")
    public String updateUser(@ModelAttribute User user) {
        userService.updateUser(user);
        return "redirect:/admin";
    }

    @GetMapping("/admin/search")
    public String showSearchForm() {
        return "search-user";
    }

    @GetMapping("/admin/search/results")
    public String searchUsers(@RequestParam(required = false) String username,
                              @RequestParam(required = false) String email,
                              Model model) {
        List<User> users = userService.searchUsers(username, email);
        model.addAttribute("users", users);
        return "user-list";
    }

    @GetMapping("/admin/upload")
    public String showUploadPage() {
        return "upload"; // This should show the upload page
    }

    // Handle POST requests for user upload
    @PostMapping("/admin/upload/users")
    public String uploadUsers(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("userMessage", "Please select a file to upload.");
            return "upload";
        }

        try {
            List<User> userList = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String line;
            reader.readLine(); // Skip header line

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                User user = new User();
                user.setUsername(data[1]);
                user.setFirstName(data[2]);
                user.setLastName(data[3]);
                user.setEmail(data[4]);
//                user.setPhoneNumber(data[4]);
//                user.setProfilePicture(data[5]);
//                user.setRole(Role.valueOf(data[6]));
                userList.add(user);
            }

            userService.saveAll(userList);
            model.addAttribute("userMessage", "User file uploaded successfully!");
            return "redirect:/admin";
        } catch (IOException | IllegalArgumentException e) {
            model.addAttribute("userMessage", "Failed to upload user file: " + e.getMessage());
        }

        return "upload";
    }

    @GetMapping("/admin/download/users")
    @ResponseBody
    public ResponseEntity<ByteArrayResource> downloadUsers() throws IOException {
        List<User> users = userService.getAllUsers();

        // Create CSV content
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream);

        // Write CSV header with all columns
        writer.println("ID,Username,Email,First Name,Last Name,Phone Number");

        // Write user data with additional fields
        for (User user : users) {
            writer.printf(
                    "%d,%s,%s,%s,%s,%s%n",
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getPhoneNumber()
                   // user.getProfilePicture(),
                   // user.getRole().name()
            );
        }
        writer.flush();
        writer.close();

        ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

        // Set the content type and attachment header
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }


    @GetMapping("/admin/user-role-stats")
    @ResponseBody
    public Map<String, Integer> getUserRoleStatistics() {
        List<User> users = userService.getAllUsers(); // Fetch all users from the service
        Map<String, Integer> roleStats = new HashMap<>();

        // Count users per role
        for (User  user : users) {
            String role = user.getRole().name(); // Assuming getRole() returns a Role enum
            roleStats.put(role, roleStats.getOrDefault(role, 0) + 1);
        }

        return roleStats; // Return the statistics as a JSON response
    }
}
