package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class BookingRepositoryIntegrationTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void beforeEach() {
        bookingRepository.deleteAll();
    }

    @Test
    void testFindByBookerId() {
        User booker = saveRandomUser();
        Item item = saveRandomItem(saveRandomUser());
        PageRequest pageRequest = PageRequest.of(1, 1);
        Booking booking1 = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .status(BookingState.WAITING)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(1))
                .build());
        Booking booking2 = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .status(BookingState.WAITING)
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().plusHours(1))
                .build());

        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(booker.getId(), pageRequest);
        Page<Booking> bookingsPage = new PageImpl<>(bookings, pageRequest, bookings.size());

        assertThat(bookingsPage.getTotalPages(), equalTo(2));
        assertThat(bookingsPage.getTotalElements(), equalTo(2L));
        assertEquals(booking2, bookingsPage.getContent().get(0));
    }

    @Test
    void testFindByBookerIdAndEndIsAfterAndStartIsBefore() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        Long bookerId = saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner()
                .get("BookerId");
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.now();

        List<Booking> bookings = bookingRepository
                .findByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(bookerId, start, end, pageRequest);
        Page<Booking> bookingsPage = new PageImpl<>(bookings, pageRequest, bookings.size());

        Booking current = bookingsPage.getContent().get(0);
        assertThat(bookingsPage.getTotalPages(), equalTo(1));
        assertThat(bookingsPage.getTotalElements(), equalTo(1L));
        assertThat(current.getBooker().getId(), equalTo(bookerId));
        assertTrue(current.getStart().isBefore(LocalDateTime.now()));
        assertTrue(current.getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    void testFindByBookerIdAndEndIsBefore() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        Long bookerId = saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner()
                .get("BookerId");
        LocalDateTime end = LocalDateTime.now();

        List<Booking> bookings = bookingRepository
                .findAllByBookerIdAndEndBeforeOrderByStartDesc(bookerId, end, pageRequest);
        Page<Booking> bookingsPage = new PageImpl<>(bookings, pageRequest, bookings.size());

        Booking past = bookingsPage.getContent().get(0);
        assertThat(bookingsPage.getTotalPages(), equalTo(1));
        assertThat(bookingsPage.getTotalElements(), equalTo(1L));
        assertThat(past.getBooker().getId(), equalTo(bookerId));
        assertTrue(past.getEnd().isBefore(LocalDateTime.now()));
    }

    @Test
    void testFindByStateAndBookerIdAndItemIdAndEndIsBefore() {
        User booker = saveRandomUser();
        Item item = saveRandomItem(saveRandomUser());
        bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item)
                .status(BookingState.APPROVED)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().minusHours(2))
                .build());
        LocalDateTime date = LocalDateTime.now();

        Optional<Booking> booking = bookingRepository
                .findFirstByItemIdAndBookerIdAndStatusAndEndBefore(item.getId(), booker.getId(), BookingState.APPROVED, date);

        Booking foundBooking = booking.get();
        assertThat(foundBooking.getStatus(), equalTo(BookingState.APPROVED));
        assertThat(foundBooking.getBooker(), equalTo(booker));
        assertThat(foundBooking.getItem(), equalTo(item));
        assertTrue(foundBooking.getEnd().isBefore(date));
    }

    @Test
    void testFindByBookerIdAndStartIsAfter() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        Long bookerId = saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner()
                .get("BookerId");
        LocalDateTime start = LocalDateTime.now();

        List<Booking> bookings = bookingRepository
                .findAllByBookerIdAndStartAfterOrderByStartDesc(bookerId, start, pageRequest);

        Booking future = bookings.get(0);
        assertThat(future.getBooker().getId(), equalTo(bookerId));
        assertTrue(future.getStart().isAfter(LocalDateTime.now()));
    }

    @Test
    void testFindByBookerIdAndState() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        Long bookerId = saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner()
                .get("BookerId");

        List<Booking> bookings = bookingRepository
                .findAllByBookerIdAndStatusOrderByStartDesc(bookerId, BookingState.WAITING, pageRequest);
        Page<Booking> bookingsPage = new PageImpl<>(bookings, pageRequest, bookings.size());

        Booking booking = bookingsPage.getContent().get(0);
        assertThat(bookingsPage.getTotalPages(), equalTo(1));
        assertThat(bookingsPage.getTotalElements(), equalTo(1L));
        assertThat(booking.getBooker().getId(), equalTo(bookerId));
        assertThat(booking.getStatus(), equalTo(BookingState.WAITING));
    }

    @Test
    void testFindByItemOwnerId() {
        User itemOwner = saveRandomUser();
        Item item = saveRandomItem(itemOwner);
        PageRequest pageRequest = PageRequest.of(1, 1);
        Booking booking1 = bookingRepository.save(Booking.builder()
                .booker(saveRandomUser())
                .item(item)
                .status(BookingState.WAITING)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(1))
                .build());
        Booking booking2 = bookingRepository.save(Booking.builder()
                .booker(saveRandomUser())
                .item(item)
                .status(BookingState.WAITING)
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().plusHours(1))
                .build());

        List<Booking> bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(itemOwner.getId(), pageRequest);
        Page<Booking> bookingsPage = new PageImpl<>(bookings, pageRequest, bookings.size());

        assertThat(bookingsPage.getTotalPages(), equalTo(2));
        assertThat(bookingsPage.getTotalElements(), equalTo(2L));
        assertEquals(booking2, bookingsPage.getContent().get(0));
    }

    @Test
    void testFindByItemOwnerIdAndEndIsAfterAndStartIsBefore() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        Long ownerId = saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner()
                .get("ItemOwnerId");
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.now();

        List<Booking> bookings = bookingRepository
                .findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, start, end, pageRequest);
        Page<Booking> bookingsPage = new PageImpl<>(bookings, pageRequest, bookings.size());

        Booking current = bookingsPage.getContent().get(0);
        assertThat(bookingsPage.getTotalPages(), equalTo(1));
        assertThat(bookingsPage.getTotalElements(), equalTo(1L));
        assertThat(current.getItem().getOwner().getId(), equalTo(ownerId));
        assertTrue(current.getStart().isBefore(LocalDateTime.now()));
        assertTrue(current.getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    void testFindByItemOwnerIdAndEndIsBefore() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        Long ownerId = saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner()
                .get("ItemOwnerId");
        LocalDateTime end = LocalDateTime.now();

        List<Booking> bookings = bookingRepository
                .findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, end, pageRequest);
        Page<Booking> bookingsPage = new PageImpl<>(bookings, pageRequest, bookings.size());

        Booking past = bookingsPage.getContent().get(0);
        assertThat(bookingsPage.getTotalPages(), equalTo(1));
        assertThat(bookingsPage.getTotalElements(), equalTo(1L));
        assertThat(past.getItem().getOwner().getId(), equalTo(ownerId));
        assertTrue(past.getEnd().isBefore(LocalDateTime.now()));
    }

    @Test
    void testFindByItemOwnerIdAndStartIsAfter() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        Long ownerId = saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner()
                .get("ItemOwnerId");
        LocalDateTime start = LocalDateTime.now();

        List<Booking> bookings = bookingRepository
                .findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, start, pageRequest);
        Page<Booking> bookingsPage = new PageImpl<>(bookings, pageRequest, bookings.size());

        Booking future = bookingsPage.getContent().get(0);
        assertThat(future.getItem().getOwner().getId(), equalTo(ownerId));
        assertTrue(future.getStart().isAfter(LocalDateTime.now()));
    }

    @Test
    void testFindByItemOwnerIdAndState() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        Long ownerId = saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner()
                .get("ItemOwnerId");

        List<Booking> bookings = bookingRepository
                .findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingState.WAITING, pageRequest);
        Page<Booking> bookingsPage = new PageImpl<>(bookings, pageRequest, bookings.size());

        Booking booking = bookingsPage.getContent().get(0);
        assertThat(bookingsPage.getTotalPages(), equalTo(1));
        assertThat(bookingsPage.getTotalElements(), equalTo(1L));
        assertThat(booking.getItem().getOwner().getId(), equalTo(ownerId));
        assertThat(booking.getStatus(), equalTo(BookingState.WAITING));
    }

    @Test
    void testFindFirstByStateAndItemIdAndStartAfter() {
        Item item = saveRandomItem(saveRandomUser());
        Booking notNextBooking = bookingRepository.save(Booking.builder()
                .item(item)
                .status(BookingState.APPROVED)
                .booker(saveRandomUser())
                .start(LocalDateTime.now().plusHours(2))
                .end(LocalDateTime.now().plusHours(3))
                .build());
        Booking nextBooking = bookingRepository.save(Booking.builder()
                .item(item)
                .status(BookingState.APPROVED)
                .booker(saveRandomUser())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build());

        Optional<Booking> next = bookingRepository
                .findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(item.getId(),
                        BookingState.APPROVED,
                        LocalDateTime.now());

        assertTrue(next.isPresent());
        assertThat(next.get(), equalTo(nextBooking));
    }

    @Test
    void testFindFirstByStateAndItemIdAndStartIsBefore() {
        Item item = saveRandomItem(saveRandomUser());
        Booking notLastBooking = bookingRepository.save(Booking.builder()
                .item(item)
                .status(BookingState.APPROVED)
                .booker(saveRandomUser())
                .start(LocalDateTime.now().minusHours(3))
                .end(LocalDateTime.now().minusHours(2))
                .build());
        Booking lastBooking = bookingRepository.save(Booking.builder()
                .item(item)
                .status(BookingState.APPROVED)
                .booker(saveRandomUser())
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().minusHours(1))
                .build());

        Optional<Booking> last = bookingRepository
                .findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(item.getId(),
                        BookingState.APPROVED,
                        LocalDateTime.now());

        assertTrue(last.isPresent());
        assertThat(last.get(), equalTo(lastBooking));
    }

    private User saveRandomUser() {
        return userRepository.save(User.builder()
                .name("name")
                .email(String.format("%s%s@email.ru", "email", new Random(9999L)))
                .build());
    }

    private Item saveRandomItem(User owner) {
        return itemRepository.save(Item.builder()
                .name("name")
                .description("desc")
                .available(true)
                .owner(owner)
                .build());
    }

    private Map<String, Long> saveOneBookingForEachBookingStateSearchDtoWithSameBookerAndItemOwner() {
        User itemOwner = saveRandomUser();
        User booker = saveRandomUser();

        bookingRepository.save(Booking.builder()
                .item(saveRandomItem(itemOwner))
                .booker(booker)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(1))
                .status(BookingState.APPROVED)
                .build());

        bookingRepository.save(Booking.builder()
                .item(saveRandomItem(itemOwner))
                .booker(booker)
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().minusHours(1))
                .status(BookingState.APPROVED)
                .build());

        bookingRepository.save(Booking.builder()
                .item(saveRandomItem(itemOwner))
                .booker(booker)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .status(BookingState.APPROVED)
                .build());

        bookingRepository.save(Booking.builder()
                .item(saveRandomItem(itemOwner))
                .booker(booker)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .status(BookingState.WAITING)
                .build());

        bookingRepository.save(Booking.builder()
                .item(saveRandomItem(itemOwner))
                .booker(booker)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .status(BookingState.REJECTED)
                .build());

        return Map.of("ItemOwnerId", itemOwner.getId(),
                "BookerId", booker.getId());
    }


}
