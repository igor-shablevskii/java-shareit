package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking b where b.booker.id = ?1 and b.item.id = ?2 and b.end < ?3")
    Booking findBooking(Long id, Long itemId, LocalDateTime now);

    List<Booking> findAllByBookerId(Long userId, Pageable pageable);

    List<Booking> findAllByItemOwnerId(Long userId, Pageable pageable);

    List<Booking> findByBookerIdAndEndBefore(Long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findByBookerIdAndStartAfter(Long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findByBookerIdAndStatus(Long userId, BookingStatus waiting, Pageable pageable);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfter(Long userId, LocalDateTime now, LocalDateTime now1,
                                                          Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfter(Long userId, LocalDateTime now, LocalDateTime now1,
                                                             Pageable pageable);

    List<Booking> findByItemOwnerIdAndEndBefore(Long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartAfter(Long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStatus(Long userId, BookingStatus waiting, Pageable pageable);
}