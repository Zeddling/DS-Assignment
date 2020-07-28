package com.server.ssl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;

public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);

    public static void main (String[] args) {
        SecureRandom secureRandom = new SecureRandom();
        log.info( "Creating secure random" );
        secureRandom.nextInt();
        log.info("Secure random created");
        new Thread( new SSLServerThread( secureRandom ) ).start();
    }

}
