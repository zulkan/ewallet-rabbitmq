package com.zulkan.ewallet.service.implementation;

import com.zulkan.ewallet.model.User;
import com.zulkan.ewallet.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class UserImplTest {


    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserImpl userService;


    @Test
    void getRandomStringTest_lengthMatch() {
        String result = userService.getRandomString(10);
        assertEquals(10, result.length());
    }

    @Test
    void createUserTest_success() throws BadRequestException {
        String username = "testUser";

        Mockito.when(userRepository.save(Mockito.any())).then(invocation -> {
            User savedUser = invocation.getArgument(0, User.class);
            savedUser.setId(23);
            return savedUser;
        });

        String token = userService.createUser(username);
        assertNotNull(token);

        Mockito.verify(userRepository).getUserByUsername(username);
        Mockito.verify(userRepository).save(Mockito.any());
    }
}