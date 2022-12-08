package ru.practicum.shareit.comment.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.comment.dto.RequestCommentDto;
import ru.practicum.shareit.comment.dto.ResponseCommentDto;
import ru.practicum.shareit.comment.model.Comment;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface CommentMapper {
    @Mapping(source = "authorId", target = "author.id")
    Comment requestCommentDtoToComment(RequestCommentDto requestCommentDto);

    @Mapping(source = "author.id", target = "authorId")
    RequestCommentDto toDto(Comment comment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "authorId", target = "author.id")
    Comment partialUpdate(RequestCommentDto requestCommentDto, @MappingTarget Comment comment);

    @Mapping(source = "authorName", target = "author.name")
    Comment toEntity1(ResponseCommentDto responseCommentDto);

    @Mapping(source = "author.name", target = "authorName")
    ResponseCommentDto commentToResponseCommentDto(Comment comment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "authorName", target = "author.name")
    Comment partialUpdate1(ResponseCommentDto responseCommentDto, @MappingTarget Comment comment);
}