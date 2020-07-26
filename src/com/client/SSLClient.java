package com.client;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;

@Slf4j
public class SSLClient extends Thread {
    private static final Logger log = LoggerFactory.getLogger(SSLClient.class);

    @SneakyThrows
    @Override
    public void run() {
        log.info("Creating secure socket");
    }

    //  Creates a sequence of random numbers for stringer encryption
    private int secureRandom() {
        SecureRandom secureRandom = new SecureRandom();
        return secureRandom.nextInt();
    }

    //  Read client's key pair from client.private.
    //  Also read server's public key
    private void setupClientKeyStore() throws GeneralSecurityException, IOException {
        KeyStore clientKeyStore = KeyStore.getInstance("JKS");
        clientKeyStore.load(new FileInputStream( "client.private" ), "public".toCharArray());
    }




}
