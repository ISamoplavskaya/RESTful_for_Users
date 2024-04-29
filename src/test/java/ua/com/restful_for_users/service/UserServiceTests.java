package ua.com.restful_for_users.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import ua.com.restful_for_users.entity.User;
import ua.com.restful_for_users.repository.UserRepository;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;
    @Value("${user.age.min}")
    private int minUserAge;

    private int userAge;

    @BeforeEach
    void setUp() {
        userAge = minUserAge;
    }
    @Test
    void getAllUsers_ReturnsListOfUsers() {
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(new User());
        expectedUsers.add(new User());

        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<User> foundUsers = userService.getAllUsers();

        assertEquals(expectedUsers.size(), foundUsers.size());
        assertEquals(expectedUsers, foundUsers);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void createUser_ValidUser_UserCreated() {
        User user = new User();
        user.setBirthDate(LocalDate.now().minusYears(userAge));
        when(userRepository.save(user)).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertEquals(user.getBirthDate(), createdUser.getBirthDate());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void createUser_InvalidUser_ThrowsException() {
        User user = new User();
        user.setBirthDate(LocalDate.now().minusYears(userAge-1));

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
        verify(userRepository, never()).save(user);
    }

    @Test
    void updateUser_ValidUpdates_UserUpdated() {
        long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setBirthDate(LocalDate.now().minusYears(userAge));

        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", "John");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        User updatedUser = userService.updateUser(userId, updates);

        assertNotNull(updatedUser);
        assertEquals("John", updatedUser.getFirstName());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void updateUser_InvalidUserId_ThrowsException() {
        long userId = 1L;
        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", "John");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userId, updates));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateAllUserFields_UserExists_UserUpdated() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        User userToUpdate = new User();
        userToUpdate.setEmail("updated@example.com");
        userToUpdate.setFirstName("Updated");
        userToUpdate.setLastName("User");
        userToUpdate.setBirthDate(LocalDate.of(2000, 1, 1));
        userToUpdate.setAddress("Updated Address");
        userToUpdate.setPhoneNumber("123456789");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        User updatedUser = userService.updateAllUserFields(userId, userToUpdate);

        assertEquals(userToUpdate.getEmail(), updatedUser.getEmail());
        assertEquals(userToUpdate.getFirstName(), updatedUser.getFirstName());
        assertEquals(userToUpdate.getLastName(), updatedUser.getLastName());
        assertEquals(userToUpdate.getBirthDate(), updatedUser.getBirthDate());
        assertEquals(userToUpdate.getAddress(), updatedUser.getAddress());
        assertEquals(userToUpdate.getPhoneNumber(), updatedUser.getPhoneNumber());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void updateUser_UserNotFound_ThrowsException() {
        Long userId = 1L;
        User userToUpdate = new User();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.updateAllUserFields(userId, userToUpdate));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_UserExists_UserDeleted() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);

        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUser_UserNotFound_ThrowsException() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).deleteById(userId);
    }

    @Test
    void searchUsersByBirthDateRange_ValidDateRange_ReturnsListOfUsers() {
        LocalDate fromDate = LocalDate.of(1990, 1, 1);
        LocalDate toDate = LocalDate.of(2000, 1, 1);
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(new User());
        expectedUsers.add(new User());

        when(userRepository.findByBirthDateBetween(fromDate, toDate)).thenReturn(expectedUsers);

        List<User> foundUsers = userService.searchUsersByBirthDateRange(fromDate, toDate);

        assertEquals(expectedUsers.size(), foundUsers.size());
        assertEquals(expectedUsers, foundUsers);
        verify(userRepository, times(1)).findByBirthDateBetween(fromDate, toDate);
    }

    @Test
    void searchUsersByBirthDateRange_InvalidDateRange_ThrowsException() {
        LocalDate fromDate = LocalDate.of(2000, 1, 1);
        LocalDate toDate = LocalDate.of(1990, 1, 1);

        assertThrows(IllegalArgumentException.class, () -> userService.searchUsersByBirthDateRange(fromDate, toDate));
        verify(userRepository, never()).findByBirthDateBetween(any(), any());
    }
}
