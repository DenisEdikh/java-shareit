package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {
    List<Booking> findBookingByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findBookingByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status);

    List<Booking> findBookingByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long bookerId,
            LocalDateTime ldt1,
            LocalDateTime ldt2);

    List<Booking> findBookingByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime ldt);

    List<Booking> findBookingByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime ldt);

    List<Booking> findBookingByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findBookingByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, Status status);

    List<Booking> findBookingByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long ownerId,
            LocalDateTime ldt1,
            LocalDateTime ldt2);

    List<Booking> findBookingByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime ldt);

    List<Booking> findBookingByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime ldt);

    Optional<Booking> findBookingByBookerIdAndItemIdAndStatus(Long ownerId, Long itemId, Status status);

    Optional<Booking> findFirstByItemIdAndEndIsBeforeAndStartIsAfterOrderByEndDesc(Long itemId, LocalDateTime ldt1, LocalDateTime ldt2);

    Optional<Booking> findFirstByItemIdAndStartIsAfterOrderByStartAsc(Long itemId, LocalDateTime ldt);
}
