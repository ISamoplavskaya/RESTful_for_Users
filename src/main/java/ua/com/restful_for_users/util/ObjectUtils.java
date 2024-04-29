package ua.com.restful_for_users.util;
import lombok.experimental.UtilityClass;
import ua.com.restful_for_users.entity.User;

import java.time.LocalDate;
import java.util.ArrayList;

@UtilityClass
public class ObjectUtils {
    public static User getUser(String name) {
        return User.builder()
                .email(name+"@gmail.com")
                .firstName(name)
                .lastName("Lastname")
                .build();
    }

}
