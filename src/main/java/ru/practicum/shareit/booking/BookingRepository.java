package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {
    List<Booking> findBookingByBookerId(Long bookerId, Sort sort);

    List<Booking> findBookingByBookerIdAndStatus(Long bookerId, Status status, Sort sort);

    List<Booking> findBookingByBookerIdAndStartBeforeAndEndAfter(
            Long bookerId,
            LocalDateTime ldt1,
            LocalDateTime ldt2,
            Sort sort);

    List<Booking> findBookingByBookerIdAndEndBefore(Long bookerId, LocalDateTime ldt, Sort sort);

    List<Booking> findBookingByBookerIdAndStartAfter(Long bookerId, LocalDateTime ldt, Sort sort);

    List<Booking> findBookingByItemOwnerId(Long ownerId, Sort sort);

    List<Booking> findBookingByItemOwnerIdAndStatus(Long ownerId, Status status, Sort sort);

    List<Booking> findBookingByItemOwnerIdAndStartBeforeAndEndAfter(
            Long ownerId,
            LocalDateTime ldt1,
            LocalDateTime ldt2,
            Sort sort);

    List<Booking> findBookingByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime ldt, Sort sort);

    List<Booking> findBookingByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime ldt, Sort sort);

    Optional<Booking> findBookingByBookerIdAndItemIdAndStatus(Long ownerId, Long itemId, Status status);

    Optional<Booking> findFirstByItemIdAndEndIsBeforeAndStartIsAfter(Long itemId,
                                                                     LocalDateTime ldt1,
                                                                     LocalDateTime ldt2,
                                                                     Sort sort);

    Optional<Booking> findFirstByItemIdAndStartIsAfter(Long itemId, LocalDateTime ldt, Sort sort);
}
