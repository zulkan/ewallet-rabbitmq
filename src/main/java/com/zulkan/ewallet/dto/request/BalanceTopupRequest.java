package com.zulkan.ewallet.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BalanceTopupRequest {

    private Integer amount;

    public BalanceTopupRequest(Integer amount) {
        this.amount = amount;
    }
}
