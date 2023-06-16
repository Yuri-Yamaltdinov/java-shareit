package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {
    private Long id;
    @NotBlank(message = "Поле name должно быть заполнено.")
    private String name;
    @NotBlank(message = "Поле description должно быть заполнено.")
    private String description;
    @NotNull(message = "Поле available должно быть заполнено.")
    private Boolean available;
    private Long requestId;
}
