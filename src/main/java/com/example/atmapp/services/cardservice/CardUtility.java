package com.example.atmapp.services.cardservice;

import javax.smartcardio.*;
import java.time.LocalDate;
import java.util.Arrays;

public class CardUtility {

    private static TerminalFactory tf ;
    private static CardTerminals lecteurs;
    private static CardTerminal lecteur ;
    private static Card card ;
    private static CardChannel ch;
    private static ResponseAPDU ra;


    private static String[] fileData ;

    static final byte[] DATA_FILE_ID = new byte[]{(byte) 0xAA, 0x10};

    static final byte MAX_TRIALS = 0x03;
    static final byte DATA_FILE_LENGTH = 0x07;
    static final byte MAX_RECORD_LENGTH_DATA = 0X14;


    public static void setup() throws CardException {

        tf = TerminalFactory.getDefault();
        lecteurs = tf.terminals();
        lecteur =    tf.terminals().list().get(0);  // lecteur id
        card = null;

        System.out.println("Attente de la carte ...");

        while(true){

            if(lecteur.isCardPresent()){
                card = lecteur.connect("*");

//

                System.out.println("Terminal connected successfully !!");

                if(card != null ){
                    byte[] bytes_ATR = card.getATR().getBytes();
                    System.out.println("ATR de la carte : " + byteArrayToHexString(bytes_ATR));
                    //card.disconnect(true);
                    ch = card.getBasicChannel();
                }
                break;
            }
        }
    }

    public static void submitICcode() throws CardException {
        byte[] APDU_Submit_IC = {(byte) 0x80, (byte) 0x20, 0x07, 0x00, 0x08, 0x41, 0x43, 0x4F, 0x53, 0x54, 0x45,
                0x53, 0x54};

        CommandAPDU apdu = new CommandAPDU(APDU_Submit_IC);
        ResponseAPDU ra = ch.transmit(apdu);

        if(ra.getSW() == 0x9000){
            System.out.println("Ok submit IC code !!");
        }
        else System.out.println("IC code error!! " + Integer.toHexString(ra.getSW()));
    }

    public static String byteArrayToHexString(byte[] b){
        String result = "";
        for(int i = 0; i < b.length ; i++){
            result += Integer.toString((b[i] & 0xFF) + 0x100, 16).substring(1);

        }
        return  result;
    }

    public static void selectFile(byte[] fileId) throws CardException {

        byte[] APDU_select_file = {(byte) 0x80, (byte) 0xa4, 0x00, 0x00, 0x02, fileId[0], fileId[1]};

        ra = ch.transmit(new CommandAPDU(APDU_select_file));

        if(ra.getSW() == 0x9000){
            System.out.println("OK select file" + Integer.toHexString(fileId[0])  + Integer.toHexString(fileId[1]) );
            System.out.println(byteArrayToHexString(ra.getData()));

        }
        else if(ra.getSW() == 0x9100){
            System.out.println("OK file selected");

        }
        else {
            System.out.println("erreur " + Integer.toHexString(ra.getSW()));
        }

    }




    public static void readFile(byte[] fileId , byte maxRecordLength, byte fileLength) throws CardException {

        selectFile(fileId);

        fileData = new String[fileLength];


        for (int i = 0; i < fileLength; i++) {
            byte[] readRecord = { (byte) 0x80, (byte) 0xb2, (byte) i, 0x00, maxRecordLength};
            ra = ch.transmit(new CommandAPDU(readRecord));
            if(ra.getSW() == 0x9000){
                fileData[i] = (new String(ra.getData())).replaceAll("\u0000+$", "");  ;
                System.out.println("record " + i + " : " + new String(ra.getData()));
            }
            else System.out.println( "Error reading : " + Integer.toHexString(ra.getSW()));
        }

        Arrays.stream(fileData).forEach(System.out::println);
    }


    public static void writeFile(byte[] fileId, String[] records, byte maxRecordLength ) throws CardException {

        byte[] writeRecord;

        selectFile(fileId);

        for(int i = 0; i < records.length; i++){

            byte[] recordBytes = records[i].getBytes();

            writeRecord = new byte[5 + maxRecordLength];
            writeRecord[0] = (byte)  0x80;
            writeRecord[1] = (byte)  0xd2;
            writeRecord[2] = (byte)  i;
            writeRecord[3] = (byte)  0x00;
            writeRecord[4] = maxRecordLength;

            for (int j = 0; j < recordBytes.length; j++ ){
                writeRecord[5 + j] = recordBytes[j];
            }

            ra = ch.transmit(new CommandAPDU(writeRecord));
            if(ra.getSW() == 0x9000){
                System.out.println("Ok MAJ file " + Integer.toHexString(fileId[0])  + Integer.toHexString(fileId[1]));
            }
            else {
                System.out.println("Error writeFile " + Integer.toHexString(ra.getSW()));
            }
        }
    }


    public static void incrementTrials() throws CardException {
        fileData[3] = Integer.toString (Integer.parseInt(fileData[3]) + 1);
        writeFile(DATA_FILE_ID, fileData,MAX_RECORD_LENGTH_DATA);
    }

    public static void resetTrials() throws CardException {
        fileData[3] = Integer.toString (0);
        writeFile(DATA_FILE_ID, fileData,MAX_RECORD_LENGTH_DATA);
    }


    public static boolean areTrialsExceeded(){
        return getTrials() == MAX_TRIALS ;
    }

    public static boolean isPinValid(String inputPin){
        return inputPin.equals(getPin());
    }


    public static void updatePin(String inputPin, String confirmationPin) throws CardException {


        fileData[2] = inputPin;
        fileData[3] = "0";

        writeFile(DATA_FILE_ID, fileData,MAX_RECORD_LENGTH_DATA );


    }

    public static boolean hasExpired(){
        return getExpDate().isBefore(LocalDate.now());
    }
    public static String getFullName(){
        return fileData[0];
    }
    public static Long getId(){
        return Long.parseLong(fileData[1]) ;
    }
    private static String getPin(){
        return fileData[2];
    }
    private static byte getTrials(){
        return Byte.parseByte(fileData[3]);
    }

    static String getAdminPin(){
        return fileData[5];
    }
    private static LocalDate getExpDate(){
        return LocalDate.parse(fileData[6]);
    }
}
