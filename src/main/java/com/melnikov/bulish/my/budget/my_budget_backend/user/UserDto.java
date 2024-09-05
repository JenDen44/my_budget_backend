package com.melnikov.bulish.my.budget.my_budget_backend.user;

import com.melnikov.bulish.my.budget.my_budget_backend.entity.AbstractDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
public class UserDto extends AbstractDto {

    @NotBlank(message = "username should be populated")
    @Size(min = 7, max = 18, message = "username size should be between 7 and 18")
    private String username;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$",
            message = "Need to have one special symbol (i.e., @, #, $, %, etc.)," +
                    "Consists of at least one digit," +
                    "Use at least one lowercase letter," +
                    "Include a capital letter," +
                    "Minimum length of 8 characters and the maximum length of 20 characters")
    private String password;

    public UserDto(Integer id, String username, String password) {
        super(id);
        this.username = username;
        this.password = password;
    }

    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
    }
}
