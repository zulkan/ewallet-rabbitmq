package com.zulkan.ewallet.e2e;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zulkan.ewallet.config.Properties;
import com.zulkan.ewallet.dto.request.BalanceTopupRequest;
import com.zulkan.ewallet.dto.request.TransferRequest;
import com.zulkan.ewallet.model.User;
import com.zulkan.ewallet.repository.TransactionRepository;
import com.zulkan.ewallet.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;

import java.util.Collections;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @LocalServerPort
    private int port = 8080;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Properties properties;

    @Autowired
    private TransactionRepository transactionRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    private final User firstUser = new User();

    @Bean
    Queue balanceTopupQueue() {
        return new Queue(properties.getBalanceTopupTopic(), false);
    }

    @Bean
    Queue transferQueue() {
        return new Queue(properties.getTransferTopic(), false);
    }

    @BeforeEach
    void init() {
        transactionRepository.deleteAll();
        userRepository.deleteAll();

        baseUrl = "http://localhost:" + port;

        firstUser.setToken("secretKey");
        firstUser.setUsername("zulkan");
        firstUser.setBalance(100000);

        userRepository.save(firstUser);
        userRepository.flush();
    }

    void setAuth() {
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("Authorization", "Bearer " + firstUser.getToken());
                    return execution.execute(request, body);
                }));
    }

    @Test
    void balanceTopupTest() throws JsonProcessingException, InterruptedException {
        var users = userRepository.findAll();

        Assertions.assertEquals(1, users.size());

        System.out.println(users.get(0).getUsername()+" "+users.get(0).getToken());

        BalanceTopupRequest requestDTO = new BalanceTopupRequest();
        requestDTO.setAmount(300000);

        setAuth();

        var resp = restTemplate.postForEntity(baseUrl + "/transactions/balance_topup", requestDTO, String.class );

        System.out.println(resp.getBody());

        Assertions.assertEquals(200, resp.getStatusCodeValue());
        Assertions.assertTrue(resp.getBody().contains("balance topup request received"));

        int ct = 0;
        while(ct++<30) {
            if(transactionRepository.count() == 1) {
                break;
            }
            Thread.sleep(1000);
        }

        Assertions.assertEquals(1, transactionRepository.count());

        var transactions = transactionRepository.findAll();

        Assertions.assertEquals(requestDTO.getAmount(), transactions.get(0).getAmount());

        var latestUserData = userRepository.getUserByUsername(firstUser.getUsername());

        Assertions.assertEquals(firstUser.getBalance() + requestDTO.getAmount(), latestUserData.getBalance());


    }

    @Test
    void transferTest() throws InterruptedException {

        var destinationUser = new User();
        destinationUser.setId(33);
        destinationUser.setUsername("destinationUser");
        destinationUser.setBalance(0);
        destinationUser.setToken("2ndsecret");

        userRepository.save(destinationUser);

        var users = userRepository.findAll();

        Assertions.assertEquals(2, users.size());

        TransferRequest requestDTO = new TransferRequest();
        requestDTO.setAmount(30000);
        requestDTO.setToUsername(destinationUser.getUsername());

        setAuth();

        var resp = restTemplate.postForEntity(baseUrl + "/transactions/transfer", requestDTO, String.class );

        System.out.println(resp.getBody());

        Assertions.assertEquals(200, resp.getStatusCodeValue());
        Assertions.assertTrue(resp.getBody().contains("transfer request received"));

        int ct = 0;
        while(ct++<30) {
            if(transactionRepository.count() == 1) {
                break;
            }
            Thread.sleep(1000);
        }

        Assertions.assertEquals(1, transactionRepository.count());

        var transactions = transactionRepository.findAll();

        Assertions.assertEquals(requestDTO.getAmount(), transactions.get(0).getAmount());

        var latestUserData = userRepository.getUserByUsername(firstUser.getUsername());

        Assertions.assertEquals(firstUser.getBalance()  - requestDTO.getAmount(), latestUserData.getBalance());


        var latestDestinationUserData = userRepository.getUserByUsername(destinationUser.getUsername());
        Assertions.assertEquals(destinationUser.getBalance()  + requestDTO.getAmount(), latestDestinationUserData.getBalance());


    }
}
