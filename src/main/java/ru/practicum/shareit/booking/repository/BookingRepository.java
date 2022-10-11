package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
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

    List<Booking> findAllByBooker_Id(Long userId, Sort sort);

    List<Booking> findAllByItem_Owner_Id(Long userId, Sort sort);

    List<Booking> findByBooker_IdAndEndBefore(Long userId, LocalDateTime now, Sort sort);

    List<Booking> findByBooker_IdAndStartAfter(Long userId, LocalDateTime now, Sort sort);

    List<Booking> findByBooker_IdAndStatus(Long userId, BookingStatus waiting, Sort sort);

    List<Booking> findByBooker_IdAndStartBeforeAndEndAfter(Long userId, LocalDateTime now, LocalDateTime now1,
                                                           Sort sort);

    List<Booking> findByItem_Owner_IdAndStartBeforeAndEndAfter(Long userId, LocalDateTime now, LocalDateTime now1,
                                                               Sort sort);

    List<Booking> findByItem_Owner_IdAndEndBefore(Long userId, LocalDateTime now, Sort sort);

    List<Booking> findByItem_Owner_IdAndStartAfter(Long userId, LocalDateTime now, Sort sort);

    List<Booking> findByItem_Owner_IdAndStatus(Long userId, BookingStatus waiting, Sort sort);
}