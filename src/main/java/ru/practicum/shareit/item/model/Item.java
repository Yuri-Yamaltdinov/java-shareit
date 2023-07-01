package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class Item {
    private Long id;
    @NotBlank
    @Size(max = 50)
    private String name;
    @Size(max = 200)
    private String description;
    @NotNull
    private Boolean available;
    @NotNull
    private Long ownerId;
    private ItemRequest request;

}
