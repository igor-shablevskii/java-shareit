package ru.practicum.shareit.request.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class ItemRequest {

    private Long id;
    private String description;
    private Long requester;
    private LocalDateTime created;
}
