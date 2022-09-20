package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.util.Create;
import ru.practicum.shareit.util.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Setter
@Getter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NotEmpty(groups = {Create.class})
    @Size(groups = {Create.class}, min = 2, message = "user name should have at least 2 characters")
    private String name;
    @NotEmpty(groups = {Create.class})
    @Email(groups = {Create.class, Update.class}, regexp = "^(.+)@(\\S+)$")
    private String email;
}