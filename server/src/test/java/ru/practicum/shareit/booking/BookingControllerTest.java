package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.UnavailableItemException;
import ru.practicum.shareit.error.IncorrectStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {
    @MockBean
    private BookingServiceImpl bookingService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private BookingDto bookingDto;
    private BookingResponseDto bookingResponseDto;
    private PageRequest pageRequest;

    @BeforeEach
    void beforeEach() {
        User user = new User(1L, "name1", "email@mail.ru");
        Item item = new Item(1L, "item1", "description1", true, user, null);
        bookingDto = new BookingDto(1L, 1L,
                LocalDateTime.of(2023, 5, 23, 12, 0), LocalDateTime.MAX);
        bookingResponseDto = new BookingResponseDto(
                1L,
                BookingStatus.APPROVED,
                LocalDateTime.of(2023, 5, 23, 12, 0),
                LocalDateTime.MAX,
                new BookingResponseDto.Item(item.getId(), item.getName()),
                new BookingResponseDto.User(user.getId()));
        pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "start"));
    }

    @Test
    void createNewBookingTest() throws Exception {
        when(bookingService.create(bookingDto, 1L)).thenReturn(bookingResponseDto);
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.item.id").value(1L));
        verify(bookingService, times(1)).create(bookingDto, 1L);
    }

    @Test
    void createNewBookingWithItemNotAvailableTest() throws Exception {
        when(bookingService.create(bookingDto, 1L)).thenThrow(new UnavailableItemException(""));
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approveBookingTest() throws Exception {
        when(bookingService.approveBooking(1L, 1L, true)).thenReturn(bookingResponseDto);
        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.item.name").value("item1"));
        verify(bookingService, times(1)).approveBooking(1L, 1L, true);
    }

    @Test
    void approveBookingWithIncorrectStateTest() throws Exception {
        when(bookingService.approveBooking(1L, 1L, true)).thenThrow(new IncorrectStateException(""));
        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingByIdTest() throws Exception {
        when(bookingService.find(1L, 1L)).thenReturn(bookingResponseDto);
        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.item.name").value("item1"));
        verify(bookingService, times(1)).find(1L, 1L);
    }

    @Test
    void getBookingByIncorrectIdTest() throws Exception {
        when(bookingService.find(1L, 10L)).thenThrow(new NotFoundException(""));
        mockMvc.perform(get("/bookings/10")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllBookingsByUserIdTest() throws Exception {
        when(bookingService.getAllBookingsByUserId(1L, BookingState.ALL, pageRequest))
                .thenReturn(Collections.singletonList(bookingResponseDto));
        mockMvc.perform(get("/bookings?state=ALL&from=0&size=2")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].item.name").value("item1"));
        verify(bookingService, times(1))
                .getAllBookingsByUserId(1L, BookingState.ALL,
                        pageRequest);
    }

    @Test
    void getAllBookingsOfCurrentUserItemsTest() throws Exception {
        when(bookingService.getAllBookingsOfCurrentUserItems(1L, BookingState.ALL, pageRequest))
                .thenReturn(Collections.singletonList(bookingResponseDto));
        mockMvc.perform(get("/bookings/owner?state=ALL&from=0&size=2")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].item.name").value("item1"));
        verify(bookingService, times(1))
                .getAllBookingsOfCurrentUserItems(1L, BookingState.ALL,
                        pageRequest);
    }

//    @Test
//    void checkDatesTest() throws Exception {
//        BookingDto bookingDto1 = new BookingDto(
//                1L,
//                1L,
//                LocalDateTime.of(2023, 5, 5, 5, 5),
//                LocalDateTime.of(2022, 12, 5, 5, 5));
//        when(bookingService.create(bookingDto1, 1L)).thenThrow(IncorrectDateException.class);
//        mockMvc.perform(post("/bookings")
//                        .header("X-Sharer-User-Id", 1L)
//                        .content(objectMapper.writeValueAsString(bookingDto1))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
//        verify(bookingService, times(0)).create(bookingDto1, 1L);
//    }
}