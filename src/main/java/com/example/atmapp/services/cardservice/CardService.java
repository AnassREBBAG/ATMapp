package com.example.atmapp.services.cardservice;


import com.example.atmapp.dto.UpdatePinDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.smartcardio.CardException;

import static com.example.atmapp.services.cardservice.CardUtility.*;

@Service
public class CardService {


    public ResponseEntity<Void> updatePin(UpdatePinDto request) throws CardException {

        if( ! request.getInputPin().equals(request.getConfirmationPin()) )
            return new ResponseEntity<>(HttpStatus.CONFLICT);

        CardUtility.updatePin(request.getInputPin(), request.getConfirmationPin());
        return new ResponseEntity<>(HttpStatus.OK);

    }



    public ResponseEntity<AuthenticationStatus> verifyCardHolder(String inputPin) throws CardException {

        setup();
        submitICcode();
        readFile(DATA_FILE_ID,MAX_RECORD_LENGTH_DATA,DATA_FILE_LENGTH);

        if(inputPin.equals(getAdminPin())){

            return new ResponseEntity<>( AuthenticationStatus.ADMIN , HttpStatus.OK) ;
        }

        if( hasExpired() ){
            return new ResponseEntity<>( AuthenticationStatus.EXPIRED , HttpStatus.FORBIDDEN) ;
        }

        if(areTrialsExceeded()){
            return new ResponseEntity<>( AuthenticationStatus.BLOCKED , HttpStatus.FORBIDDEN) ;
        }

        if( !isPinValid(inputPin) ){
            incrementTrials();
            return new ResponseEntity<>( AuthenticationStatus.INVALID_PIN , HttpStatus.FORBIDDEN) ;

        }

        resetTrials();
        return  new ResponseEntity<>( AuthenticationStatus.VALID , HttpStatus.OK) ;

    }



}
