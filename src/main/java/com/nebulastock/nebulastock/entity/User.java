package com.nebulastock.nebulastock.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity                          // Tells JPA: "This class maps to a database table"
@Table(name = "users")           // Maps to the "users" table specifically
@Data                            // Lombok: auto-generates getters, setters, toString, equals
@Builder                         // Lombok: build objects like User.builder().username("x").build()
@NoArgsConstructor               // Lombok: generates empty constructor (JPA requires this)
@AllArgsConstructor              // Lombok: generates constructor with all fields
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment (SERIAL in PostgreSQL)
    private Integer id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;  // Will store HASHED password, never plain text!

    @Enumerated(EnumType.STRING) // Store enum as "ADMIN", "MANAGER" string in DB (not 0,1,2)
    @Column(nullable = false)
    private Role role;

    // Enum defined inside the entity — clean and organized
    public enum Role {
        ADMIN, MANAGER, VIEWER
    }
}
