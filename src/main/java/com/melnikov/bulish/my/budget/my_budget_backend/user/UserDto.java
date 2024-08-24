package com.melnikov.bulish.my.budget.my_budget_backend.user;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.AbstractDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserDto extends AbstractDto {

    private String username;
    private String password;

    public UserDto(Integer id, String username, String password) {
        super(id);
        this.username = username;
        this.password = password;
    }
}
