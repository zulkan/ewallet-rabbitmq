package com.zulkan.ewallet.dto.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class BalanceTopupMessage {

    private Integer userId;
    private Integer amount;

    public BalanceTopupMessage(Integer userId, Integer amount) {
        this.userId = userId;
        this.amount = amount;
    }
}
