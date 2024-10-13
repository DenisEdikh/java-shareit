package ru.practicum.shareit.server.user;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.server.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDtoTest {
    private final JacksonTester<UserDto> tester;

    @Test
    @SneakyThrows
    void testSerialize() {
        UserDto userDto = new UserDto(1L, "name", "email@email.com");

        JsonContent<UserDto> result = tester.write(userDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.email");
        assertThat(result).extractingJsonPathNumberValue("@.id").isEqualTo(userDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("@.name").isEqualTo(userDto.getName());
        assertThat(result).extractingJsonPathStringValue("@.email").isEqualTo(userDto.getEmail());
    }
}