package com.zulkan.ewallet.controller;

import com.zulkan.ewallet.dto.request.BalanceTopupRequest;
import com.zulkan.ewallet.dto.request.TransferRequest;
import com.zulkan.ewallet.model.User;
import com.zulkan.ewallet.service.TransactionInterface;
import com.zulkan.ewallet.service.UserInterface;
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
@RequestMapping("transactions")
public class TransactionController {

    private final TransactionInterface transactionService;

    @Autowired
    public TransactionController(TransactionInterface transactionService) {
        this.transactionService = transactionService;
    }


    @RequestMapping(value = "transfer", method = RequestMethod.POST)
    public ResponseEntity<Object> transfer(@RequestBody TransferRequest transferRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        transactionService.publishTransfer(user, transferRequest.getToUsername(), transferRequest.getAmount());
        return ResponseEntity.ok().body(Map.of("message", "transfer request received"));
    }

    @RequestMapping(value = "balance_topup", method = RequestMethod.POST)
    public ResponseEntity<Object> balanceTopup(@RequestBody BalanceTopupRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        transactionService.publishBalanceTopup(user.getId(), request.getAmount());
        return ResponseEntity.ok().body(Map.of("message", "balance topup request received"));
    }

}
