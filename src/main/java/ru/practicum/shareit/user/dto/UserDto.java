package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private Long id;
    @NotBlank(message = "Поле name должно быть заполнено.")
    private String name;
    @NotBlank(message = "Поле email должно быть заполнено.")
    @Email(message = "Введен неправильный формат поля email.")
    private String email;
}
