package ru.practicum.shareit.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDtoComment {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Set<CommentDto> comments;

    public void addComment(List<CommentDto> comments) {
        if (this.comments == null) {
            this.comments = new HashSet<>();
        }
        this.comments.addAll(comments);
    }
}
