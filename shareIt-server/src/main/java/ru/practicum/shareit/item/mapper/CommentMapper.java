package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment commentFromDto(CommentDto commentDto);

    @Mapping(target = "authorName", source = "comment.author.name")
    CommentDto commentToDto(Comment comment);
}
