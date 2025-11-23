package com.wowa.f1betting.domain.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private Long balance;

    public User(String username, String passwordHash, int initialBalance) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = UserRole.USER;
        this.balance  = (long) initialBalance;
    }
}
