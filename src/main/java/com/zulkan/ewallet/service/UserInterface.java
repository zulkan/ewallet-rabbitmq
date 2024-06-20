package com.zulkan.ewallet.service;

import org.apache.coyote.BadRequestException;

public interface UserInterface {

    String createUser(String username) throws BadRequestException;

}
