package com.zulkan.ewallet.controller;

import com.zulkan.ewallet.dto.request.CreateUserRequest;
import com.zulkan.ewallet.dto.response.GetBalanceResponse;
import com.zulkan.ewallet.model.User;
import com.zulkan.ewallet.service.UserInterface;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("users")
public class UserController {

    private final UserInterface userService;
    @Autowired
    public UserController(UserInterface userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "balance_read", method = RequestMethod.GET)
    public ResponseEntity<GetBalanceResponse> getBalance() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(new GetBalanceResponse(user.getBalance()));
    }

    //    Register a new user by username. Username is to be unique for every user.
    @RequestMapping(value = "create_user", method = RequestMethod.POST)
    public ResponseEntity<Object> createUser(@RequestBody CreateUserRequest request) throws BadRequestException {
        String token = userService.createUser(request.getUsername());
        return ResponseEntity.ok().body(Map.of("token", token));
    }

}