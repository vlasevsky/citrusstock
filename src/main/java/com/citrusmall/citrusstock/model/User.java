package com.citrusmall.citrusstock.model;

import com.citrusmall.citrusstock.model.enums.Role;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password; // хранить в зашифрованном виде

    @Enumerated(EnumType.STRING)
    private Role role;
}