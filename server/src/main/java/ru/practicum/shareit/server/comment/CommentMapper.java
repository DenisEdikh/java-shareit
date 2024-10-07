package ru.practicum.shareit.server.comment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.comment.dto.CommentDto;
import ru.practicum.shareit.server.comment.dto.NewCommentDto;
import ru.practicum.shareit.server.user.User;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setCreated(comment.getCreated());
        commentDto.setAuthorName(comment.getUser().getName());
        return commentDto;
    }

    public static List<CommentDto> toCommentDto(List<Comment> comments) {
        if (comments == null) {
            return List.of();
        }
        return comments.stream().map(CommentMapper::toCommentDto).toList();
    }

    public static Comment toComment(Item item, User user, NewCommentDto newCommentDto) {
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setItem(item);
        comment.setText(newCommentDto.getText());
        return comment;
    }


}
