package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemDto {
    private Long id;
    @NotBlank(message = "Field name has to be filled.")
    private String name;
    @NotBlank(message = "Field description has to be filled.")
    private String description;
    @NotNull(message = "Field available has to be filled.")
    private Boolean available;
    private Long requestId;
}
