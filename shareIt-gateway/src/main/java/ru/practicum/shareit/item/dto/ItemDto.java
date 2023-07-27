package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.util.PostRequestValidationGroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Builder
public class ItemDto {
    private Long id;
    @NotBlank(message = "Field name has to be filled.", groups = PostRequestValidationGroup.class)
    private String name;
    @NotBlank(message = "Field description has to be filled.", groups = PostRequestValidationGroup.class)
    private String description;
    @NotNull(message = "Field available has to be filled.", groups = PostRequestValidationGroup.class)
    private Boolean available;
    @Positive(message = "Field request id has to be positive")
    private Long requestId;
}
