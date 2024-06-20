package com.zulkan.ewallet.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import com.zulkan.ewallet.config.SecurityConfig;
import com.zulkan.ewallet.dto.request.BalanceTopupRequest;
import com.zulkan.ewallet.dto.request.TransferRequest;
import com.zulkan.ewallet.filter.AuthFilter;
import com.zulkan.ewallet.model.User;
import com.zulkan.ewallet.repository.UserRepository;
import com.zulkan.ewallet.service.TransactionInterface;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@Import({SecurityConfig.class, AuthFilter.class})
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TransactionInterface transactionService;

    private User currentUser = new User();

    private final ObjectMapper objectMapper = new ObjectMapper();


    @BeforeEach
    void init() {
        currentUser.setId(2);
        currentUser.setUsername("zulkan");
        currentUser.setToken("secret");

        Mockito.when(userRepository.getUserByToken(currentUser.getToken())).thenReturn(currentUser);
    }

    @Test
    void transfer() throws Exception {
        TransferRequest requestDTO = new TransferRequest("destinationUser", 10000);

        String json = objectMapper.writeValueAsString(requestDTO);

        MvcResult result = mockMvc.perform(post("/transactions/transfer").content(json).contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + currentUser.getToken()))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();

        String contentResponse = result.getResponse().getContentAsString();

        Map<String, String> response = objectMapper.readValue(contentResponse, new TypeReference<>() {
        });

        Assertions.assertEquals(true, response.containsKey("message"));
        Assertions.assertEquals("transfer request received", response.get("message"));

        Mockito.verify(transactionService).publishTransfer(currentUser, "destinationUser", 10000);
    }

    @Test
    void balanceTopup() throws Exception {
        BalanceTopupRequest requestDTO = new BalanceTopupRequest();
        requestDTO.setAmount(300000);

        String json = objectMapper.writeValueAsString(requestDTO);

        MvcResult result = mockMvc.perform(post("/transactions/balance_topup").content(json).contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + currentUser.getToken()))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();

        String contentResponse = result.getResponse().getContentAsString();

        Map<String, String> response = objectMapper.readValue(contentResponse, new TypeReference<>() {
        });

        Assertions.assertEquals(true, response.containsKey("message"));
        Assertions.assertEquals("balance topup request received", response.get("message"));
        Mockito.verify(transactionService).publishBalanceTopup(currentUser.getId(), requestDTO.getAmount());

    }
}