package com.zulkan.ewallet.service.implementation;

import com.zulkan.ewallet.Constant;
import com.zulkan.ewallet.config.Properties;
import com.zulkan.ewallet.dto.message.BalanceTopupMessage;
import com.zulkan.ewallet.dto.message.TransferMessage;
import com.zulkan.ewallet.exception.FailedUpdateDataException;
import com.zulkan.ewallet.exception.InsufficientBalanceException;
import com.zulkan.ewallet.exception.InvalidAmountException;
import com.zulkan.ewallet.exception.InvalidDestinationAccountException;
import com.zulkan.ewallet.model.Transaction;
import com.zulkan.ewallet.model.TransactionType;
import com.zulkan.ewallet.model.User;
import com.zulkan.ewallet.repository.TransactionRepository;
import com.zulkan.ewallet.repository.UserRepository;
import com.zulkan.ewallet.service.TransactionInterface;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.zulkan.ewallet.utils.Utils.toJson;


@Service
@Slf4j
public class TransactionImpl implements TransactionInterface {

    private final UserRepository userRepository;

    private final TransactionRepository transactionRepository;

    private Properties properties;

    private RabbitTemplate template;

    @Autowired
    public TransactionImpl(UserRepository userRepository, TransactionRepository transactionRepository,
                            Properties properties, RabbitTemplate template) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.properties = properties;
        this.template = template;
    }

    @Override
    @Transactional
    public void transfer(TransferMessage transferMessage) {
        int updatedRows = userRepository.addBalance(transferMessage.getToUserId(), transferMessage.getAmount());
        if (updatedRows == 0) {
            throw new FailedUpdateDataException("Failed update balance destination");
        }

        updatedRows = userRepository.addBalance(transferMessage.getFromUserId(), transferMessage.getAmount() * -1);
        if (updatedRows == 0) {
            throw new FailedUpdateDataException("Failed update balance");
        }

        Transaction transRecord = new Transaction();
        transRecord.setSourceId(transferMessage.getFromUserId());
        transRecord.setDestinationId(transferMessage.getToUserId());
        transRecord.setTransactionType(TransactionType.TRANSFER);
        transRecord.setAmount(transferMessage.getAmount());
        transRecord.setCreatedBy(transferMessage.getFromUsername());
        transRecord.setUpdatedBy(transferMessage.getFromUsername());

        transactionRepository.save(transRecord);

    }

    @Override
    public void publishTransfer(User fromUser, String toUsername, Integer amount) {
        if (amount < 1 || amount > Constant.MAXIMUM_TRANSACTION_AMOUNT) {
            throw new InvalidAmountException("topup can't be more than " + Constant.MAXIMUM_TRANSACTION_AMOUNT);
        }
        if (amount > fromUser.getBalance()) {
            throw new InsufficientBalanceException();
        }
        User destinationUser = userRepository.getUserByUsername(toUsername);

        if (destinationUser == null) {
            throw new InvalidDestinationAccountException("Invalid Destination Account");
        }
        TransferMessage transferMessage = new TransferMessage(fromUser.getId(), fromUser.getUsername(), destinationUser.getId(), destinationUser.getUsername(), amount);
        template.convertAndSend(properties.getTransferTopic(), toJson(transferMessage));
    }


    @Override
    @Transactional
    public void balanceTopup(Integer userId, Integer amount) {
        int updatedRows = userRepository.addBalance(userId, amount);
        if (updatedRows == 0) {
            throw new FailedUpdateDataException("Failed update balance");
        }

        Transaction transRecord = new Transaction();
        transRecord.setSourceId(userId);
        transRecord.setDestinationId(userId);
        transRecord.setTransactionType(TransactionType.TOPUP);
        transRecord.setAmount(amount);
        transactionRepository.save(transRecord);
//        transactionRepository.save(userId, amount);
    }

    @Override
    public void publishBalanceTopup(Integer userId, Integer amount) {
        if (amount < 1 || amount > Constant.MAXIMUM_TRANSACTION_AMOUNT) {
            throw new InvalidAmountException("Invalid topup amount "+amount);
        }
        BalanceTopupMessage message = new BalanceTopupMessage(userId, amount);
        template.convertAndSend(properties.getBalanceTopupTopic(), toJson(message));
    }
}
