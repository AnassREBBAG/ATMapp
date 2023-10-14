package com.example.atmapp.services;

import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;


@Service
@AllArgsConstructor
public class AtmService {

    private RestTemplate restTemplate;
    private ReceiptService receiptService;

    public Long getBalance(Long id) throws IOException {

        String checkBalanceAPI = "http://localhost:8080/user/checkbalance?id="+id;

//      return restTemplate.postForEntity(checkBalanceAPI, new HttpEntity<>(id), Long.class).getBody();
        return restTemplate.getForEntity(checkBalanceAPI, Long.class).getBody();
    }

    public ResponseEntity<String> withdraw(Map<String, Object> request) throws IOException {


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String withdrawAPI = "http://localhost:8080/user/withdraw";



        if( !request.get("generateReceipt").toString().equals("0")){
            request.put("amount", Long.parseLong(request.get("amount").toString())  + 1);
        }


        ResponseEntity<String> response = restTemplate.exchange(withdrawAPI, HttpMethod.POST, new HttpEntity<>(request, headers), String.class);

        if( !(request.get("generateReceipt").toString().equals("0") || response.getBody().equals("insufficient balance"))){


            receiptService.generateWithdrawalReceipt(Long.parseLong(request.get("id").toString()), Long.parseLong(request.get("amount").toString()) );
        }


        return response;

        //add the mail sending service

    }
}