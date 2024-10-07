package ru.practicum.shareit.server.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> {
    List<Item> findAllByOwnerId(Long userId);

    List<Item> findAllByItemRequestIdIn(Set<Long> requestId);

    List<Item> findAllByItemRequestId(Long requestId);
}
