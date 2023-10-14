package com.example.atmapp.controllers;

import com.example.atmapp.services.AtmService;
import com.example.atmapp.services.ReceiptService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;


@RestController
@AllArgsConstructor
@RequestMapping("/atm")
public class AtmController {

    private AtmService atmService;
    private ReceiptService receiptService;

    @PostMapping("/checkbalance")
    public ResponseEntity<Long> getBalance(@RequestParam Long id) throws IOException {

        return ResponseEntity.ok().body(atmService.getBalance(id));
    }

    @PostMapping("/balancereceipt")
    public void generateBalanceReceipt(@RequestParam Long id) throws IOException {

        receiptService.generateBalanceReceipt(id);
    }



    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestBody Map<String, Object> request) throws IOException {
        //map {
        //      id(Long) ,
        //      amount(Long) ,
        //      generateReceipt(boolean)
        //     }

        return atmService.withdraw(request);
    }




}