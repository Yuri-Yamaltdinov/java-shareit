package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private Long id;
    @NotBlank(message = "Field name has to be filled.")
    private String name;
    @NotBlank(message = "Filed email has to be filled.")
    @Email(message = "Incorrect email format.")
    private String email;
}
