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

    public AccountService(String url) { // AccountService constructor with endpoint link url as parameter
        this.baseUrl = url;
    }


    public BigDecimal getBalance(AuthenticatedUser currentUser) { // Sending the request to get the balance
        BigDecimal balance = BigDecimal.ZERO; // initialize balance value to 0
        try {
            balance = restTemplate.exchange(
                    baseUrl + "balance",
                    HttpMethod.GET,
                    createAuthEntity(currentUser),
                    BigDecimal.class).getBody();

        } catch (RestClientResponseException errorMessage) {
            System.out.println("Error getting balance from the account : "+currentUser.getUser().getUsername());
            errorMessage.printStackTrace();
        }
        return balance;
    }

    public Account getAccountByUserId(AuthenticatedUser currentUser, int userId) { // Sending the request to get Account by user_id
        Account account = new Account();
        try {
            account = restTemplate.exchange(baseUrl + "account/user/" + userId,
                    HttpMethod.GET, createAuthEntity(currentUser), Account.class).getBody();

        } catch (RestClientResponseException errorMessage) {
            errorMessage.printStackTrace();
        }
        return account;
    }

    public Account getAccountById(AuthenticatedUser currentUser, int accountId) { // Sending the request to get Account by account_id
        Account account = new Account();
        try {
            account = restTemplate.exchange(baseUrl +"account/" + accountId,
                    HttpMethod.GET, createAuthEntity(currentUser), Account.class).getBody();

        } catch (RestClientResponseException errorMessage) {
            errorMessage.printStackTrace();
        }
        return account;
    }

    private HttpEntity createAuthEntity(AuthenticatedUser authenticatedUser) { //creates an HTTP entity, entity object with authorization headers
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authenticatedUser.getToken());
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }
}

