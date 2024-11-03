package com.auca.VotingApp2.model;



import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;

    private String phoneNumber;
    private String firstName;
    private String lastName;

    private String profilePicture;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20)
    private Role role;

    private String resetToken;

    // Getters and setters

}