package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;

import com.techelevator.tenmo.model.TransferType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class TransferTypeService {
    private String baseUrl;
    private RestTemplate restTemplate = new RestTemplate();
    public TransferTypeService(String url) { // constructor
        this.baseUrl = url;
    }

    public TransferType getTransferType(AuthenticatedUser currentUser, String desc) {
        TransferType transferType = null;
        try {

            transferType = restTemplate.exchange(baseUrl + "transfertype/filter?description=" +
                    desc,
                    HttpMethod.GET,
                    makeEntity(currentUser),
                    TransferType.class).getBody();
        }  catch(RestClientResponseException | ResourceAccessException e) {
            e.printStackTrace();
        }
        return transferType;
    }


    public TransferType getTransferTypeFromId(AuthenticatedUser currentUser, int transferTypeId) {
        TransferType transferType = null;
        try {
            transferType = restTemplate.exchange(baseUrl + "transfertype/" +
                    transferTypeId,
                    HttpMethod.GET,
                    makeEntity(currentUser),
                    TransferType.class).getBody();
        }  catch(RestClientResponseException | ResourceAccessException e) {
            e.printStackTrace();
        }
        return transferType;
    }

    private HttpEntity makeEntity(AuthenticatedUser currentUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity(headers);
        return entity;
    }
}
