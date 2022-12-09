package ru.practicum.shareit.comment.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.comment.dto.RequestCommentDto;
import ru.practicum.shareit.comment.dto.ResponseCommentDto;
import ru.practicum.shareit.comment.model.Comment;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface CommentMapper {
    @Mapping(source = "authorName", target = "author.name")
    Comment toEntity(ResponseCommentDto responseCommentDto);

    @Mapping(source = "author.name", target = "authorName")
    ResponseCommentDto commentToResponseCommentDto(Comment comment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "authorName", target = "author.name")
    Comment partialUpdate(ResponseCommentDto responseCommentDto, @MappingTarget Comment comment);

    Comment requestCommentDtoToComment(RequestCommentDto requestCommentDto);

    RequestCommentDto toDto1(Comment comment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Comment partialUpdate1(RequestCommentDto requestCommentDto, @MappingTarget Comment comment);
}