package com.zulkan.ewallet.dto.message;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class TransferMessage {

    private Integer fromUserId;
    private String fromUsername;
    private Integer toUserId;
    private String toUsername;
    private Integer amount;

    public TransferMessage(Integer fromUserId, String fromUsername, Integer toUserId, String toUsername, Integer amount) {
        this.fromUserId = fromUserId;
        this.fromUsername = fromUsername;
        this.toUserId = toUserId;
        this.toUsername = toUsername;
        this.amount = amount;
    }
}
