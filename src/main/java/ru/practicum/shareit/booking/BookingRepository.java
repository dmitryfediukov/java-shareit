package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b join fetch b.item join fetch b.booker "
            + "where (:owner = false and b.booker.id = :userId or :owner = true and b.item.owner = :userId) "
            + "and (:state = 'ALL' "
            + "or :state = 'CURRENT' and b.start <= :now and b.end > :now "
            + "or :state = 'PAST' and b.end <= :now "
            + "or :state = 'FUTURE' and b.start > :now "
            + "or :state = 'WAITING' and b.status = ru.practicum.shareit.booking.BookingStatus.WAITING "
            + "or :state = 'REJECTED' and b.status = ru.practicum.shareit.booking.BookingStatus.REJECTED) "
            + "order by b.start desc, b.id desc")
    List<Booking> findForUser(@Param("userId") long userId, @Param("owner") boolean owner,
                              @Param("state") String state, @Param("now") LocalDateTime now);

    @Query("select b from Booking b join fetch b.booker where b.item.id in :ids "
            + "and b.status = ru.practicum.shareit.booking.BookingStatus.APPROVED order by b.start, b.id")
    List<Booking> findApprovedForItems(@Param("ids") Collection<Long> ids);

    boolean existsByItemIdAndBookerIdAndStatusAndEndLessThanEqual(
            Long itemId, Long bookerId, BookingStatus status, LocalDateTime now);
}
