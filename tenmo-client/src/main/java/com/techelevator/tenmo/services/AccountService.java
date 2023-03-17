package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountService{

    private String baseUrl;
    private RestTemplate restTemplate = new RestTemplate();
    public AccountService(String url) {
        this.baseUrl = url;
    }


    public BigDecimal getBalance(AuthenticatedUser currentUser) {
        BigDecimal balance = BigDecimal.ZERO; // initialize balance value to 0
        try {
            balance = restTemplate.exchange(
                    baseUrl + "balance",
                    HttpMethod.GET,
                    createAuthEntity(currentUser),
                    BigDecimal.class).getBody();

        } catch (RestClientException m) {
            System.out.println("Error getting balance from the account : "+currentUser.getUser().getUsername());
        }
        return balance;
    }

    public Account getAccountByUserId(AuthenticatedUser currentUser, int userId) {
        Account myAccount = new Account();
        try {
            myAccount = restTemplate.exchange(baseUrl + "account/user/" + userId, HttpMethod.GET, createAuthEntity(currentUser), Account.class).getBody();
        } catch (RestClientResponseException e) {
            e.printStackTrace();
        }
        return myAccount;
    }

    public Account getAccountById(AuthenticatedUser currentUser, int accountId) {
        Account myAccount = new Account();
        try {
            myAccount = restTemplate.exchange(baseUrl +"account/" + accountId, HttpMethod.GET, createAuthEntity(currentUser), Account.class).getBody();
        } catch (RestClientResponseException m) {
            m.printStackTrace();
        }
        return myAccount;
    }


    private HttpEntity createAuthEntity(AuthenticatedUser authenticatedUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authenticatedUser.getToken());
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }
}

