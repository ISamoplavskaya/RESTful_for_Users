package ua.com.restful_for_users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.com.restful_for_users.entity.User;
import ua.com.restful_for_users.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    @Value("${user.age.min}")
    private int minUserAge;

    public List<User> getAllUsers() {
        log.info("Getting all users");
        return userRepository.findAll();
    }

    public User createUser(User user) {
        log.info("Creating user: {}", user);
        LocalDate minBirthDate = LocalDate.now().minusYears(minUserAge);
        if (user.getBirthDate().isAfter(minBirthDate)) {
            throw new IllegalArgumentException("User must be at least " + minUserAge + " years old.");
        }
        return userRepository.save(user);
    }

    public User updateUser(Long userId, Map<String, Object> updates) {
        log.info("Updating user with ID {}: {}", userId, updates);
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        updates.forEach((key, value) -> {
            switch (key) {
                case "email":
                    existingUser.setEmail((String) value);
                    break;
                case "firstName":
                    existingUser.setFirstName((String) value);
                    break;
                case "lastName":
                    existingUser.setLastName((String) value);
                    break;
                case "birthDate":
                    existingUser.setBirthDate((LocalDate) value);
                    break;
                case "address":
                    existingUser.setAddress((String) value);
                    break;
                case "phoneNumber":
                    existingUser.setPhoneNumber((String) value);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid field: " + key);
            }
        });

        // Save updated user
        return userRepository.save(existingUser);
    }

    public User updateAllUserFields(Long userId, User user) {
        log.info("Updating all fields for user with ID {}: {}", userId, user);
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        existingUser.setEmail(user.getEmail());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setBirthDate(user.getBirthDate());
        existingUser.setAddress(user.getAddress());
        existingUser.setPhoneNumber(user.getPhoneNumber());

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long userId) {
        log.info("Deleting user with ID {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }

    public List<User> searchUsersByBirthDateRange(LocalDate from, LocalDate to) {
        log.info("Searching for users between {} and {}", from, to);
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("From date must be before To date.");
        }
        return userRepository.findByBirthDateBetween(from, to);
    }

}
