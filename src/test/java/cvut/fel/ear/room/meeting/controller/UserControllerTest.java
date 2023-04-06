package cvut.fel.ear.room.meeting.controller;

import cvut.fel.ear.room.meeting.entity.User;
import cvut.fel.ear.room.meeting.exception.ApplicationException;
import cvut.fel.ear.room.meeting.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private UserController controller;

    @Mock
    private UserService service;

    @BeforeEach
    void setUp() {
        controller = new UserController(service);
    }

    @Test
    void testGetUserById() {
        Long userId = 1L;
        Optional<User> user = Optional.of(new User());
        when(service.getUserById(userId)).thenReturn(user);
        ResponseEntity<Optional<User>> response = controller.getUserById(userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testGetUserByIdWithNullId() {
        assertThrows(ApplicationException.class, () -> controller.getUserById(null));
    }

    @Test
    void getUserByUsername_shouldReturnUser() {
        // given
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        when(service.getUserByUsername(username)).thenReturn(user);
        ResponseEntity<User> responseEntity = controller.getUserByUsername(username);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(user, responseEntity.getBody());
    }

    @Test
    void getUserByUsername_shouldThrowApplicationException_whenUsernameIsNull() {
        assertThrows(ApplicationException.class, () -> controller.getUserByUsername(null));
    }

    @Test
    public void testGetUsersFilterAsc() {
        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setUsername("user1");
        User user2 = new User();
        user2.setUsername("user2");
        users.add(user1);
        users.add(user2);

        when(service.getUsersAsc()).thenReturn(users);

        ResponseEntity<List<User>> response = controller.getUsersFilter("asc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
    }

    @Test
    public void testGetUsersFilterDesc() {
        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setUsername("user1");
        User user2 = new User();
        user2.setUsername("user2");
        users.add(user1);
        users.add(user2);

        when(service.getUsersDesc()).thenReturn(users);

        ResponseEntity<List<User>> response = controller.getUsersFilter("desc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
    }

    @Test
    public void testGetUsersFilterInvalidFilterType() {
        ApplicationException exception = assertThrows(ApplicationException.class, () -> controller.getUsersFilter("invalid"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Filter type must be specified.", exception.getMessage());
    }
}
