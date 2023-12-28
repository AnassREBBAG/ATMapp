package com.example.atmapp.controllers;

import com.example.atmapp.dto.UpdatePinDto;
import com.example.atmapp.services.AtmService;
import com.example.atmapp.services.cardservice.AuthenticationStatus;
import com.example.atmapp.services.cardservice.CardService;
import com.example.atmapp.services.ReceiptService;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.smartcardio.CardException;
import java.io.IOException;
import java.util.Map;


@RestController
@AllArgsConstructor
@RequestMapping("/atm")
public class AtmController {

    private AtmService atmService;
    private ReceiptService receiptService;

    private CardService cardService;

    @GetMapping("/checkbalance")
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


    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationStatus> verifyCardHolder(@RequestParam String inputPin) throws CardException {
        return cardService.verifyCardHolder(inputPin);
    }
    
    @PostMapping("/updatepin")
    public ResponseEntity<Void> updatePin(@RequestBody UpdatePinDto request) throws CardException {
        return cardService.updatePin(request);


    }






}