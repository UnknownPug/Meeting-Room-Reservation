package cvut.fel.ear.room.meeting.service;

import cvut.fel.ear.room.meeting.entity.User;
import cvut.fel.ear.room.meeting.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @Test
    public void testGetUsers() {
        when(userRepository.findAll()).thenReturn(List.of(new User(), new User()));
        assertEquals(2, userService.getUsers().size());
    }

    @Test
    public void testGetUserByIdNotNull() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        assertNotNull(userService.getUserById(1L));
    }

    @Test
    public void testGetUserByUsername() {
        when(userRepository.findByUsername(anyString())).thenReturn(new User());
        assertNotNull(userService.getUserByUsername("username"));
    }

    @Test
    public void testGetUserByEmail() {
        when(userRepository.findByEmail(anyString())).thenReturn(new User());
        assertNotNull(userService.getUserByEmail("email"));
    }

    @Test
    public void testGetUsersAsc() {
        when(userRepository.findAllByIdIsNotNullOrderByUsernameAsc()).thenReturn(List.of(new User(), new User()));
        assertEquals(2, userService.getUsersAsc().size());
    }

    @Test
    public void testGetUsersDesc() {
        when(userRepository.findAllByIdIsNotNullOrderByUsernameDesc()).thenReturn(List.of(new User(), new User()));
        assertEquals(2, userService.getUsersDesc().size());
    }

    @Test
    public void testGetUserById() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");
        user.setEmail("user1@example.com");
        Optional<User> optionalUser = Optional.of(user);
        when(userRepository.findById(1L)).thenReturn(optionalUser);
        Optional<User> result = userService.getUserById(1L);
        assertEquals(optionalUser, result);
    }
}
