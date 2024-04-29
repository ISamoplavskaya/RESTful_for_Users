package ua.com.restful_for_users.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ua.com.restful_for_users.entity.User;
import ua.com.restful_for_users.util.ObjectUtils;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByBirthDateBetween() {
        User user1 = ObjectUtils.getUser("John");
        user1.setBirthDate(LocalDate.of(1990, 5, 15));
        User user2 = ObjectUtils.getUser("Jane");
        user2.setBirthDate(LocalDate.of(1995, 8, 25));

        userRepository.save(user1);
        userRepository.save(user2);

        LocalDate fromDate = LocalDate.of(1990, 1, 1);
        LocalDate toDate = LocalDate.of(1995, 12, 31);
        List<User> users = userRepository.findByBirthDateBetween(fromDate, toDate);

        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
    }
}
