package com.example.atmapp.services;

import lombok.AllArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;


@Service
@AllArgsConstructor
public class ReceiptService {

    private RestTemplate restTemplate;

    public void generateBalanceReceipt(Long id) throws IOException {

        Map<String, String> customerInfoMap = getCustomerInfo(id);

        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A6);
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));



        printText(contentStream,10, 400,8 , "...........................................................");
        printText(contentStream,95, 375,12 , "Liberty Bank");
        printText(contentStream,10, 350,8 , "...........................................................");

        printText(contentStream,90, 325,12 , "ACCOUNT BALANCE");


        printText(contentStream, 20,225, 10 , "DATE & TIME    :       " + time);
        printText(contentStream, 20,200, 10 , "COSTUMER NAME  :       " + customerInfoMap.get("fullName"));
        printText(contentStream, 20,175, 10 , "ACCOUNT NUMBER :       " + "XXXXXXXX " + customerInfoMap.get("rib").substring(14, 18) );
        printText(contentStream, 20,150, 10 , "BALANCE        :       "  + customerInfoMap.get("accountBalance") + " MAD");


        //footer
        printText(contentStream,10, 60,8 , "...........................................................");
        printText(contentStream, 75,30, 9 , "THANK YOU FOR BANKING WITH US" );

        contentStream.close();


        document.save("C:\\Users\\anass\\OneDrive\\Desktop\\receipt.pdf");

        document.close();

    }




    public void generateWithdrawalReceipt(Long id, Long amount) throws IOException {

        Map<String, String> customerInfoMap = getCustomerInfo(id);

        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A6);
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));



        printText(contentStream,10, 400,8 , "...........................................................");
        printText(contentStream,95, 375,12 , "Liberty Bank");
        printText(contentStream,10, 350,8 , "...........................................................");

        printText(contentStream,90, 325,12 , "WITHDRAWAL RECEIPT");


        printText(contentStream, 20,225, 10 , "DATE & TIME    :       " + time);
        printText(contentStream, 20,200, 10 , "COSTUMER NAME  :       " + customerInfoMap.get("fullName"));
        printText(contentStream, 20,175, 10 , "ACCOUNT NUMBER :       " + "XXXXXXXX " + customerInfoMap.get("rib").substring(14, 18) );
        printText(contentStream, 20,150, 10 , "AMOUNT         :       " + (amount) + " MAD");
        printText(contentStream, 20,125, 10 , "BALANCE        :       "  + (Long.parseLong(customerInfoMap.get("accountBalance"))  ) +   " MAD");


        //footer
        printText(contentStream,10, 60,8 , "...........................................................");
        printText(contentStream, 75,30, 9 , "THANK YOU FOR BANKING WITH US" );

        contentStream.close();


        document.save("C:\\Users\\anass\\OneDrive\\Desktop\\withdrawalReceipt.pdf");

        document.close();




    }





    private void printText(PDPageContentStream contentStream, int tx, int ty, int size, String text){

        try{
            contentStream.beginText();
            contentStream.setFont(PDType1Font.COURIER, size);
            contentStream.newLineAtOffset(tx, ty);
            contentStream.showText(text);
            contentStream.endText();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> getCustomerInfo(Long id){
        String customerInfoAPI = "http://localhost:8080/user/getCustomerInfo?id="+id;

        Map<String, String> response = restTemplate.getForObject(customerInfoAPI, Map.class);

        return response;
    }
}