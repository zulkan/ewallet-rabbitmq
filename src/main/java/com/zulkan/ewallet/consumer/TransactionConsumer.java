package com.zulkan.ewallet.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zulkan.ewallet.config.Properties;
import com.zulkan.ewallet.dto.message.BalanceTopupMessage;
import com.zulkan.ewallet.dto.message.TransferMessage;
import com.zulkan.ewallet.service.TransactionInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TransactionConsumer {

    private final TransactionInterface transactionService;

    private final ObjectMapper objectMapper;

    private final Properties properties;


    public TransactionConsumer(TransactionInterface transactionService, ObjectMapper objectMapper, Properties properties) {
        this.transactionService = transactionService;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }


    @RabbitListener(queues = "#{properties.getBalanceTopupTopic()}")
    public void topupConsumer(String message) throws JsonProcessingException {
        log.info("Processing Topup message: " + message);
        var topupMessage = objectMapper.readValue(message, BalanceTopupMessage.class);
        transactionService.balanceTopup(topupMessage.getUserId(), topupMessage.getAmount());
        log.info("Finished Processing Topup message: " + message);
    }

    @RabbitListener(queues = "#{properties.getTransferTopic()}")
    public void transferConsumer(String message) throws JsonProcessingException {
        log.info("Processing Transfer message: " + message);
        var transferMessage = objectMapper.readValue(message, TransferMessage.class);
        transactionService.transfer(transferMessage);
        log.info("Finished Processing Transfer message: " + message);

    }
}
