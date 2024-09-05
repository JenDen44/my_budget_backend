package com.melnikov.bulish.my.budget.my_budget_backend.user;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.AbstractDto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
public class UserDto extends AbstractDto {

    private String username;
    private String password;

    public UserDto(Integer id, String username, String password) {
        super(id);
        this.username = username;
        this.password = password;
    }

    public UserDto(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
    }
}
