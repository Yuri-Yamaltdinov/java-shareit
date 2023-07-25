package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.util.PostRequestValidationGroup;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(groups = PostRequestValidationGroup.class, message = "Field name has to be filled.")
    private String name;
    @NotBlank(groups = PostRequestValidationGroup.class, message = "Field email has to be filled.")
    @Email(message = "Incorrect email format.")
    private String email;
}
