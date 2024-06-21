package com.zulkan.ewallet.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zulkan.ewallet.dto.message.BalanceTopupMessage;
import com.zulkan.ewallet.dto.message.TransferMessage;
import com.zulkan.ewallet.service.TransactionInterface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionConsumerTest {


    @Mock
    private TransactionInterface transactionService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private TransactionConsumer transactionConsumer;


    @Test
    void topupConsumer() throws JsonProcessingException {
        BalanceTopupMessage message = new BalanceTopupMessage(1, 1000);
        var messageString = objectMapper.writeValueAsString(message);

        transactionConsumer.topupConsumer(messageString);

        Mockito.verify(transactionService).balanceTopup(1, 1000);
    }

    @Test
    void transferConsumer() throws JsonProcessingException {

        TransferMessage message = new TransferMessage(1, "user1", 2, "user2", 1000);
        var messageString = objectMapper.writeValueAsString(message);

        transactionConsumer.transferConsumer(messageString);

        Mockito.verify(transactionService).transfer(message);
    }
}