package ru.practicum.shareit.comment.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.comment.dto.ResponseCommentDto;
import ru.practicum.shareit.comment.model.Comment;

import java.util.ArrayList;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    ArrayList<ResponseCommentDto> findAllByItem_IdOrderById(long itemId);
}