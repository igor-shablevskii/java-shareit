package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.util.Create;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class ItemDto {
    private Long id;
    @NotEmpty(groups = {Create.class})
    @Size(groups = {Create.class}, min = 2, message = "item name should have at least 2 characters")
    private String name;
    @NotEmpty(groups = {Create.class})
    private String description;
    @NotNull(groups = {Create.class})
    @AssertTrue(groups = {Create.class})
    private Boolean available;
    private Long requestId;
}