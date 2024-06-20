package com.zulkan.ewallet.service.implementation;

import com.zulkan.ewallet.model.User;
import com.zulkan.ewallet.repository.UserRepository;
import com.zulkan.ewallet.service.UserInterface;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Service
public class UserImpl implements UserInterface {

    private final UserRepository userRepository;

    public UserImpl(@Autowired UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static String getRandomString(int length) {
        try {
            String allowedChars = "0123456789abcdefghijklmnopqrstuvwxyz-_ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            SecureRandom secureRandom = SecureRandom.getInstance("NativePRNG");

            secureRandom.nextInt(allowedChars.length());
            return secureRandom.ints(length, 0, allowedChars.length()).mapToObj(allowedChars::charAt)
                    .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }


    //201 token
    //400 bad request
    //409 username already exist
    public String createUser(String username) throws BadRequestException {
        username = username.trim();
        if (username.length() == 0) {
            throw new BadRequestException("invalid username");
        }
        boolean isExist = userRepository.getUserByUsername(username) != null;
        if (isExist) {
            throw new BadRequestException("username already exist");
        }

        User user = new User();
        user.setUsername(username);
        user.setBalance(0);
        user.setToken(getRandomString(120));
        user = userRepository.save(user);

        return user.getToken();
    }

}
