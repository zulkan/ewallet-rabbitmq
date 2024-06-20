package com.zulkan.ewallet.service;

import com.zulkan.ewallet.dto.message.TransferMessage;
import com.zulkan.ewallet.model.User;

public interface TransactionInterface {

    void transfer(TransferMessage transferMessage);
    void publishTransfer(User fromUser, String toUsername, Integer amount);

    void balanceTopup(Integer userId, Integer amount);
    void publishBalanceTopup(Integer userId, Integer amount);

}
