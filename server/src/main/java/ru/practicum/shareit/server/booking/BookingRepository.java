package ru.practicum.shareit.server.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {
    List<Booking> findBookingByItemId(Long itemId);

    List<Booking> findBookingByBookerId(Long bookerId, Sort sort);

    List<Booking> findBookingByBookerIdAndStatus(Long bookerId, Status status, Sort sort);

    List<Booking> findBookingByBookerIdAndStartBeforeAndEndAfter(Long bookerId,
                                                                 LocalDateTime ldt1,
                                                                 LocalDateTime ldt2,
                                                                 Sort sort);

    List<Booking> findBookingByBookerIdAndEndBefore(Long bookerId, LocalDateTime ldt, Sort sort);

    List<Booking> findBookingByBookerIdAndStartAfter(Long bookerId, LocalDateTime ldt, Sort sort);

    List<Booking> findBookingByItemOwnerId(Long ownerId, Sort sort);

    List<Booking> findBookingByItemOwnerIdAndStatus(Long ownerId, Status status, Sort sort);

    List<Booking> findBookingByItemOwnerIdAndStartBeforeAndEndAfter(Long ownerId,
                                                                    LocalDateTime ldt1,
                                                                    LocalDateTime ldt2,
                                                                    Sort sort);

    List<Booking> findBookingByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime ldt, Sort sort);

    List<Booking> findBookingByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime ldt, Sort sort);

    List<Booking> findBookingByBookerIdAndItemIdAndStatus(Long ownerId, Long itemId, Status status);

    Optional<Booking> findFirstByItemIdAndItemOwnerIdAndEndIsBefore(Long itemId,
                                                                    Long userId,
                                                                    LocalDateTime ldt,
                                                                    Sort sort);

    Optional<Booking> findFirstByItemIdAndItemOwnerIdAndStartIsAfter(Long itemId,
                                                                     Long userId,
                                                                     LocalDateTime ldt,
                                                                     Sort sort);

    @Query("""
            SELECT b from Booking as b
            left join fetch b.item as i
            left join fetch i.owner as o
            where i.id in :itemIds and o.id = :userId and b.end = (
            select max(b2.end)
            from Booking as b2
            where b2.end < :ldt)
            """)
    Optional<Booking> findLastBookingWith(@Param("itemIds") Set<Long> itemIds,
                                          @Param("userId") Long userId,
                                          @Param("ldt") LocalDateTime ldt);

    @Query("""
            select b from Booking as b
            left join fetch b.item as i
            left join fetch i.owner as o
            where i.id in :itemIds and o.id = :userId and b.start = (
            select min(b2.start)
            from Booking as b2
            where b2.start > :ldt
            )
            """)
    Optional<Booking> findNextBookingWith(@Param("itemIds") Set<Long> itemIds,
                                          @Param("userId") Long userId,
                                          @Param("ldt") LocalDateTime ldt);
}
