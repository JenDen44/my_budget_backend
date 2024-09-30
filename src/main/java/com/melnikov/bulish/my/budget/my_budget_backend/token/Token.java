package com.melnikov.bulish.my.budget.my_budget_backend.token;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.AbstractEntity;
import com.melnikov.bulish.my.budget.my_budget_backend.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token extends AbstractEntity {

    @Column(unique = true)
    private String token;
    private Instant expiryDate;

    @Enumerated(EnumType.STRING)
    public TokenType tokenType = TokenType.BEARER;

    public boolean revoked;

    public boolean expired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}