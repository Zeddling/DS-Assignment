package com.client;

import com.client.ssl.SSLClientThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class TestClient {
    private static final Logger log = LoggerFactory.getLogger(TestClient.class);
    private static SecureRandom secureRandom;           //  A source of secure random numbers

    public static void main(String[] args){
        log.info( "Creating secure random" );
        secureRandom = new SecureRandom();
        secureRandom.nextInt();
        log.info("Secure random created");
        List<String> toyDetails = setToyDetails();
        new Thread( new SSLClientThread(8010, "127.0.0.1", toyDetails, secureRandom ) ).start();
    }

    public static List<String> setToyDetails() {
        List<String> toyDetails = new ArrayList<>();
        toyDetails.add(0, "RS-7938095");
        toyDetails.add(1, "Plastic Bottle");
        toyDetails.add(2, "1 Litre translucent plastic bottle");
        toyDetails.add(3, "300.00");
        toyDetails.add(4, "05-02-2020");
        toyDetails.add(5, "TML-73863255");
        toyDetails.add(6, "Dasani");
        toyDetails.add(7, "Tuskys T-Mall");
        toyDetails.add(8, "0100");
        toyDetails.add(9, "Durable");
        return toyDetails;
    }

}
