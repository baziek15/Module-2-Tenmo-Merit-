package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.TransferStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
public class TransferStatusService {
    private  String baseUrl;
    private RestTemplate restTemplate = new RestTemplate();

    public TransferStatusService(String url) { // constructor
        this.baseUrl = url;
    }


    public TransferStatus getTransferStatus(AuthenticatedUser authenticatedUser, String description) {
        TransferStatus transferStatus = null;
        try {
            transferStatus = restTemplate.exchange(baseUrl + "transferstatus/filter?description=" + description,
                    HttpMethod.GET, makeEntity(authenticatedUser),
                    TransferStatus.class).getBody();
        }  catch(RestClientResponseException | ResourceAccessException e) {
            e.printStackTrace();
        }
        return transferStatus;
    }


    public TransferStatus getTransferStatusById(AuthenticatedUser authenticatedUser, int transferStatusId) {
        TransferStatus transferStatus = null;
        try {
            transferStatus = restTemplate.exchange(baseUrl + "transferstatus/" + transferStatusId, HttpMethod.GET,
                    makeEntity(authenticatedUser),
                    TransferStatus.class).getBody();
        }  catch(RestClientResponseException | ResourceAccessException e) {
            e.printStackTrace();
        }
        return transferStatus;
    }

    private HttpEntity makeEntity(AuthenticatedUser authenticatedUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticatedUser.getToken());
        HttpEntity entity = new HttpEntity(headers);
        return entity;
    }
}
