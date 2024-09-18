package com.melnikov.bulish.my.budget.my_budget_backend.user;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.AbstractEntity;
import com.melnikov.bulish.my.budget.my_budget_backend.purchase.Purchase;
import com.melnikov.bulish.my.budget.my_budget_backend.token.Token;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

    private String username;
    private String password;

    @ToString.Exclude
    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    @ToString.Exclude
    @OneToMany(mappedBy = "user")
    private List<Purchase> purchases;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public User(UserDto userDto) {
        this.id = userDto.getId();
        this.username = userDto.getUsername();
        this.password = userDto.getPassword();
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
