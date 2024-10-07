package ru.practicum.shareit.server.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.server.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestWithItemDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private Set<ItemDto> items;

    public void addItems(List<ItemDto> items) {
        if (this.items == null) {
            this.items = new HashSet<>();
        }
        this.items.addAll(items);
    }
}
