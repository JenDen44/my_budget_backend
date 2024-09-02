package com.melnikov.bulish.my.budget.my_budget_backend.user;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.AbstractEntity;
import com.melnikov.bulish.my.budget.my_budget_backend.token.Token;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class User extends AbstractEntity implements UserDetails {

    @NotBlank(message = "username should be populated")
    @Min(value = 7, message = "username can't be shorter 7 characters")
    @Max(value = 14, message = "username can't be longer 14 characters")
    private String username;

    @NotBlank(message = "password should be populated")
    @Min(value = 8, message = "password can't be shorter 8 characters")
    @Max(value = 20, message = "password can't be longer 20 characters")
    private String password;

    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<GrantedAuthority>();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
