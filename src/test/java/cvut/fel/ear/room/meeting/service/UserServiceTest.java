package cvut.fel.ear.room.meeting.service;

import cvut.fel.ear.room.meeting.entity.User;
import cvut.fel.ear.room.meeting.exception.ApplicationException;
import cvut.fel.ear.room.meeting.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUsersReturnsAllValidUsers() {
        List<User> userList = new ArrayList<>();
        userList.add(new User());
        userList.add(new User());
        when(userRepository.findAll()).thenReturn(userList);

        Collection<User> users = userService.getUsers();
        assertEquals(userList, users);
        verify(userRepository).findAll();
    }

    @Test
    void getUserByIdExistingUserIdReturnsValidUser() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.getUserById(userId);

        assertEquals(user, result);
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserByIdNonExistingUserIdThrowApplicationException() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ApplicationException.class, () -> userService.getUserById(userId));
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserByUsernameExistingUsernameReturnsValidUser() {
        String username = "user1";
        User user = new User();
        when(userRepository.findByUsername(username)).thenReturn(user);

        User result = userService.getUserByUsername(username);

        assertEquals(user, result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void getUserByUsernameNonExistingUsernameThrowApplicationException() {
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(null);

        assertThrows(ApplicationException.class, () -> userService.getUserByUsername(username));
        verify(userRepository).findByUsername(username);
    }

    @Test
    void getUserByEmailExistingEmailReturnsValidUser() {
        String email = "user1@example.com";
        User user = new User();
        when(userRepository.findByEmail(email)).thenReturn(user);

        User result = userService.getUserByEmail(email);

        assertEquals(user, result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void getUserByEmailNonExistingEmailThrowApplicationException() {
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(null);

        assertThrows(ApplicationException.class, () -> userService.getUserByEmail(email));
        verify(userRepository).findByEmail(email);
    }

    @Test
    void getUsersAscReturnValidSortedUsersListAsc() {
        List<User> userList = new ArrayList<>();
        userList.add(new User());
        userList.add(new User());
        when(userRepository.findAllByIdIsNotNullOrderByUsernameAsc()).thenReturn(userList);

        List<User> users = userService.getUsersAsc();

        assertEquals(userList, users);
        verify(userRepository).findAllByIdIsNotNullOrderByUsernameAsc();
    }

    @Test
    void getUsersDescReturnValidSortedUsersListDesc() {
        List<User> userList = new ArrayList<>();
        userList.add(new User());
        userList.add(new User());
        when(userRepository.findAllByIdIsNotNullOrderByUsernameDesc()).thenReturn(userList);

        List<User> users = userService.getUsersDesc();

        assertEquals(userList, users);
        verify(userRepository).findAllByIdIsNotNullOrderByUsernameDesc();
    }

    @Test
    void createUserSetCorrectUserDataCreatesUser() {
        String username = "user1";
        String email = "user1@example.com";
        String password = "password1";
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password);

        when(userRepository.findByUsername(username)).thenReturn(null);
        when(userRepository.findByEmail(email)).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        User result = userService.createUser(username, email, password);

        assertEquals(newUser, result);
        verify(userRepository).findByUsername(username);
        verify(userRepository).findByEmail(email);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUserUsernameAlreadyExistsThrowApplicationException() {
        String username = "user1";
        String email = "user1@example.com";
        String password = "password1";
        when(userRepository.findByUsername(username)).thenReturn(new User());

        assertThrows(ApplicationException.class, () -> userService.createUser(username, email, password));
        verify(userRepository).findByUsername(username);
        verify(userRepository, never()).findByEmail(email);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUserEmailAlreadyUsedThrowApplicationException() {
        String username = "user1";
        String email = "user1@example.com";
        String password = "password1";
        when(userRepository.findByUsername(username)).thenReturn(null);
        when(userRepository.findByEmail(email)).thenReturn(new User());

        assertThrows(ApplicationException.class, () -> userService.createUser(username, email, password));
        verify(userRepository).findByUsername(username);
        verify(userRepository).findByEmail(email);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUserMissingInfoThrowApplicationException() {
        String username = "";
        String email = "user1@example.com";
        String password = "password1";

        assertThrows(ApplicationException.class, () -> userService.createUser(username, email, password));
        verify(userRepository, never()).findByUsername(username);
        verify(userRepository, never()).findByEmail(email);
        verify(userRepository, never()).save(any(User.class));
    }
}
