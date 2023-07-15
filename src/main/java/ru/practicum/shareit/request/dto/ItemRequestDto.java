package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.PostRequestValidationGroup;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestDto {
    private Long id;
    @NotBlank(message = "Field description has to be filled.", groups = PostRequestValidationGroup.class)
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}
