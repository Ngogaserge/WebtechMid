package com.auca.VotingApp2.service;


import com.auca.VotingApp2.model.ResetToken;
import com.auca.VotingApp2.model.Role;
import com.auca.VotingApp2.model.User;
import com.auca.VotingApp2.repository.ResetTokenRepository;
import com.auca.VotingApp2.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResetTokenRepository resetTokenRepository;

    @Autowired
    private JavaMailSender mailSender;

//    @PostConstruct
//    public void init() {
//        addAdminUser(); // Automatically add the admin user on application startup
//    }



    // Send Password Reset Email
    @Transactional // Ensure transactional context
    public boolean sendPasswordResetEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return false; // User not found
        }

        // Delete any existing reset token for this user before generating a new one
        deleteExistingResetTokenByEmail(email);

        // Generate a new token and save it
        String token = UUID.randomUUID().toString();
        saveResetTokenForUser(user, token);

        // Prepare and send email
        String resetUrl = "http://localhost:8080/reset-password?token=" + token;
        String message = "To reset your password, click the link below:\n" + resetUrl;
        sendEmail(email, "Password Reset", message);

        return true;
    }

    @Transactional // Ensure transactional context
    protected void saveResetTokenForUser(User user, String token) {
        ResetToken resetToken = new ResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15)); // Set token expiry to 15 minutes

        resetTokenRepository.save(resetToken);
    }

    @Transactional // Ensure transactional context
    public void deleteExistingResetTokenByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            resetTokenRepository.findByUser(user).ifPresent(resetToken -> {
                resetTokenRepository.delete(resetToken);
                System.out.println("Deleted existing token: " + resetToken.getToken());
            });
        }
    }

    // Send email utility
    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    // Check if email exists
    public boolean doesEmailExist(String email) {
        return userRepository.findByEmail(email) != null;
    }

    // Register a user
    @Transactional // Ensure transactional context
    public User registerUser(User user) {
        return userRepository.save(user); // Save the user without password encoding
    }

    // Login user by username
    public User loginUser(String username) {
        return userRepository.findByUsername(username);
    }

    // Find user by token
    public Optional<User> findUserByResetToken(String token) {
        return resetTokenRepository.findByToken(token)
                .map(ResetToken::getUser); // Directly map the ResetToken to User if present
    }

    public boolean validatePasswordResetToken(String token) {
        Optional<ResetToken> resetTokenOptional = resetTokenRepository.findByToken(token);
        if (resetTokenOptional.isPresent()) {
            ResetToken resetToken = resetTokenOptional.get();
            boolean isValid = resetToken.getExpiryDate().isAfter(LocalDateTime.now());
            System.out.println("Token: " + token + ", Valid: " + isValid); // Log the token status
            return isValid;
        }
        System.out.println("Token: " + token + " not found."); // Log if token is not found
        return false;
    }


    @Transactional // Ensure transactional context
    public boolean resetUserPassword(String token, String newPassword) {
        // Validate the token
        if (!validatePasswordResetToken(token)) {
            System.out.println("Invalid or expired token: " + token);
            return false; // Token is invalid or expired
        }

        // Find the user associated with the token
        Optional<User> userOptional = findUserByResetToken(token);
        if (!userOptional.isPresent()) {
            System.out.println("No user found for the token: " + token);
            return false;
        }

        User user = userOptional.get();
        user.setPassword(newPassword); // Update the user's password
        userRepository.save(user);

        // Invalidate the token after successful password reset
        resetTokenRepository.deleteByToken(token);

        return true;
    }


    //=================================================//
//    ================CRUD=====================



    public List<User> getAllUsers() {
        return userRepository.findAll(); // Fetch all users from the database
    }

    public void addAdminUser() {
        // Check if admin user already exists
        if (userRepository.findByUsername("admin") == null) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword("admin"); // Hash the password
            adminUser.setEmail("ishimwedivin2@gmail.com");
            adminUser.setRole(Role.ROLE_ADMIN); // Ensure Role is properly defined

            userRepository.save(adminUser);
            System.out.println("Admin user created successfully.");
        } else {
            System.out.println("Admin user already exists.");
        }
    }

    @Transactional // Ensure transactional context
    public void updateUser (User user) {
        userRepository.save(user); // Save the updated user
    }

    @Transactional // Ensure transactional context
    public void deleteUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            userRepository.deleteById(id);
            System.out.println("User with ID " + id + " deleted successfully.");
        } else {
            System.out.println("User with ID " + id + " not found.");
        }
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> searchUsers(String username, String email) {
        // Logic to search for users based on username and/or email
        // This could involve querying the database with a repository method
        return userRepository.findByUsernameContainingOrEmailContaining(username, email);
    }
    public  void saveAll(List<User> userList) {
        userRepository.saveAll(userList);
    }


    // Add this method to your UserService
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }




    public User loginUser(String username, HttpSession session) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            session.setAttribute("loggedInUser", user); // Store the user in session
        }
        return user;
    }

}