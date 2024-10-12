package ru.practicum.shareit.server.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
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
