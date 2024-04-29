package ua.com.restful_for_users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ua.com.restful_for_users.entity.User;
import ua.com.restful_for_users.service.UserService;
import ua.com.restful_for_users.util.ObjectUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Value("${user.age.min}")
    private Long minUserAge;

    private LocalDate validBirthDate;

    @BeforeEach
    void setUp() {
        validBirthDate = LocalDate.now().minusYears(minUserAge);
    }

    @Test
    void getUsers_ReturnsListOfUsers() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(ObjectUtils.getUser("user1"));
        users.add(ObjectUtils.getUser("user2"));

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(users.size()));
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void createUser_ValidUser_ReturnsCreated() throws Exception {
        User user = ObjectUtils.getUser("user1");
        user.setBirthDate(validBirthDate);

        when(userService.createUser(any())).thenReturn(user);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                .andExpect(jsonPath("$.birthDate").value(user.getBirthDate().toString()));

        verify(userService, times(1)).createUser(any());
    }

    @Test
    void updateUser_ValidUpdates_ReturnsUpdatedUser() throws Exception {
        Long userId = 1L;
        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", "UpdatedFirstName");

        User updatedUser = ObjectUtils.getUser("UpdatedFirstName");
        updatedUser.setId(userId);
        when(userService.updateUser(userId, updates)).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("UpdatedFirstName"));

        verify(userService, times(1)).updateUser(userId, updates);
    }

    @Test
    void updateAllUserFields_ValidUser_ReturnsUpdatedUser() throws Exception {
        Long userId = 1L;
        User updatedUser =ObjectUtils.getUser("updatedName");
        updatedUser.setId(userId);

        when(userService.updateAllUserFields(eq(userId), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/updateAll/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.firstName").value("updatedName"));
    }

    @Test
    void deleteUser_ValidUserId_ReturnsNoContent() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/api/users/{userId}", userId))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    void searchUsersByBirthDateRange_ValidDateRange_ReturnsListOfUsers() throws Exception {
        String from = "2000-01-01";
        String to = "2022-12-31";

        List<User> users = new ArrayList<>();
        User user1 = ObjectUtils.getUser("name1");
        user1.setBirthDate(LocalDate.now().minusYears(5));
        User user2 = ObjectUtils.getUser("name2");
        user2.setBirthDate(LocalDate.now().minusYears(4));

        users.add(user1);
        users.add(user2);

        when(userService.searchUsersByBirthDateRange(LocalDate.parse(from), LocalDate.parse(to))).thenReturn(users);

        mockMvc.perform(get("/api/users/search")
                        .param("from", from)
                        .param("to", to))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("name1"))
                .andExpect(jsonPath("$[1].firstName").value("name2"));

        verify(userService, times(1)).searchUsersByBirthDateRange(LocalDate.parse(from), LocalDate.parse(to));
    }
}
