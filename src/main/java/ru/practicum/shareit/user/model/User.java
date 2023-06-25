package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class User {
    private Long id;
    @NotBlank(message = "Field name has is empty.")
    private String name;
    @NotBlank(message = "Field email has is empty.")
    @Email(message = "Incorrect email format.")
    private String email;
}
