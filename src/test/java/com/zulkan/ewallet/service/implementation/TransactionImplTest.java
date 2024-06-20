package com.zulkan.ewallet.service.implementation;

import com.zulkan.ewallet.config.Properties;
import com.zulkan.ewallet.dto.message.BalanceTopupMessage;
import com.zulkan.ewallet.dto.message.TransferMessage;
import com.zulkan.ewallet.exception.InvalidDestinationAccountException;
import com.zulkan.ewallet.model.TransactionType;
import com.zulkan.ewallet.model.User;
import com.zulkan.ewallet.repository.TransactionRepository;
import com.zulkan.ewallet.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static com.zulkan.ewallet.utils.Utils.toJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private Properties properties;

    @Mock
    private RabbitTemplate template;

    @InjectMocks
    private TransactionImpl transactionService;

    @Test
    void transferTest_success() {
        // Set up any necessary data or mocks
        User sourceUser = new User();
        sourceUser.setId(1);
        sourceUser.setBalance(100);
        String toUsername = "destinationUser";
        Integer amount = 50;

        User destinationUser = new User();
        destinationUser.setId(2);
        destinationUser.setUsername(toUsername);
        destinationUser.setBalance(100);

        when(userRepository.addBalance(sourceUser.getId(), amount * -1)).thenReturn(1);
        when(userRepository.addBalance(destinationUser.getId(), amount)).thenReturn(1);

        TransferMessage message = new TransferMessage(sourceUser.getId(), sourceUser.getUsername(), destinationUser.getId(), destinationUser.getUsername(), amount);

        // Call the method
        transactionService.transfer(message);

        // Assert the expected behavior or outcome
        Mockito.verify(transactionRepository).save(argThat(transaction -> {
            return transaction.getSourceId().equals(sourceUser.getId()) &&
                    transaction.getDestinationId().equals(destinationUser.getId()) &&
                    transaction.getTransactionType().equals(TransactionType.TRANSFER) &&
                    transaction.getAmount().equals(amount);
        }));

    }

    @Test
    void publishTransferTest_destinationNotFound() {

        // Set up any necessary data or mocks
        User sourceUser = new User();
        sourceUser.setBalance(100);
        String toUsername = "nonExistingUser";
        Integer amount = 50;

        // Define the behavior of mocked dependencies when the destination user is not found
        when(userRepository.getUserByUsername(toUsername)).thenReturn(null);

        // Call the method
        InvalidDestinationAccountException exception = assertThrows(InvalidDestinationAccountException.class, () -> {
            transactionService.publishTransfer(sourceUser, toUsername, amount);
        });

        // Assert the expected behavior or outcome
        assertEquals("Invalid Destination Account", exception.getMessage());

        // Verify that the transactionRepository is not called when the destination user is not found
        Mockito.verify(transactionRepository, never()).save(Mockito.any());

    }


    @Test
    void balanceTopup() {
        // Set up any necessary data or mocks
        User user = new User();
        user.setId(1);
        user.setBalance(100);
        Integer amount = 50;

        // Define the behavior of mocked dependencies if needed
        when(userRepository.addBalance(user.getId(), amount)).thenReturn(1);

        // Call the method
        transactionService.balanceTopup(user.getId(), amount);

        // Assert the expected behavior or outcome
        Mockito.verify(transactionRepository).save(argThat(transaction -> {
            return transaction.getSourceId().equals(user.getId()) &&
                    transaction.getDestinationId().equals(user.getId()) &&
                    transaction.getTransactionType().equals(TransactionType.TOPUP) &&
                    transaction.getAmount().equals(amount);
        }));
    }

    @Test
    void publishTransferTest_success() {
        User fromUser = new User();
        fromUser.setBalance(100);

        User destinationUser = new User();
        destinationUser.setUsername("destinationUser");

        Integer amount = 50;

        Mockito.when(userRepository.getUserByUsername(destinationUser.getUsername())).thenReturn(destinationUser);

        Mockito.when(properties.getTransferTopic()).thenReturn("transfer");

        transactionService.publishTransfer(fromUser, destinationUser.getUsername(), amount);

        TransferMessage transferMessage = new TransferMessage(fromUser.getId(), fromUser.getUsername(), destinationUser.getId(), destinationUser.getUsername(), amount);

        Mockito.verify(template).convertAndSend("transfer", toJson(transferMessage));
    }

    @Test
    void publishBalanceTopup_success() {
        User fromUser = new User();
        fromUser.setId(3);
        fromUser.setBalance(100);

        Integer amount = 30000;
        Mockito.when(properties.getBalanceTopupTopic()).thenReturn("topup");

        transactionService.publishBalanceTopup(fromUser.getId(), amount);

        BalanceTopupMessage message = new BalanceTopupMessage(fromUser.getId(), amount);

        Mockito.verify(template).convertAndSend("topup", toJson(message));


    }
}