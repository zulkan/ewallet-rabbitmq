package com.zulkan.ewallet.dto.response;

public class GetBalanceResponse {

    private final Integer balance;

    public GetBalanceResponse(Integer balance) {
        this.balance = balance;
    }

    public Integer getBalance() {
        return balance;
    }
}
