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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void testGetUserByIdReturnsValidUser() {
        Long id = 123L;
        User user = new User();

        when(service.getUserById(id)).thenReturn(user);

        ResponseEntity<User> response = controller.getUser(id, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(service, times(1)).getUserById(id);
    }

    @Test
    void testGetUserByUsernameReturnsValidUser() {
        String username = "testuser";
        User user = new User();

        when(service.getUserByUsername(username)).thenReturn(user);

        ResponseEntity<User> response = controller.getUser(null, username, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(service, times(1)).getUserByUsername(username);
    }

    @Test
    void testGetUserByEmailReturnsValidUser() {
        String email = "test@example.com";
        User user = new User();

        when(service.getUserByEmail(email)).thenReturn(user);

        ResponseEntity<User> response = controller.getUser(null, null, email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(service, times(1)).getUserByEmail(email);
    }

    @Test
    void getUserWithInvalidRequestThrowsApplicationException() {
        assertThrows(ApplicationException.class, () ->
                controller.getUser(null, "testuser", "test@example.com"));
    }

    @Test
    public void testGetUsersReturnsSortedUsersListAsc() {
        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setUsername("user1");
        User user2 = new User();
        user2.setUsername("user2");
        users.add(user1);
        users.add(user2);

        when(service.getUsersAsc()).thenReturn(users);

        ResponseEntity<List<User>> response = controller.getSortedUsers("asc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
    }

    @Test
    public void testGetUsersReturnsSortedUsersListDesc() {
        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setUsername("user1");
        User user2 = new User();
        user2.setUsername("user2");
        users.add(user1);
        users.add(user2);

        when(service.getUsersDesc()).thenReturn(users);

        ResponseEntity<List<User>> response = controller.getSortedUsers("desc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(users, response.getBody());
    }

    @Test
    public void getSortedUsersWithInvalidSortTypeThrowsApplicationException() {
        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> controller.getSortedUsers("invalid"));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals("Set the sort type to asc or desc to get the list of sorted" +
                " users in ascending or descending order.", exception.getMessage());
    }
}
