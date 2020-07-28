package com.server;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;

@Slf4j
public class ServerMainSocket {
    private final static String pathToStores = "/home/zeddling/Documents/Projects/DS Socket Protocol/ssl/assignment.keystore";
    private final static String keyToStoreFile = "assignmentPublic.keystore";
    private final static String passphrase = "123456";


    public static void main(String[] args){
        Logger log = LoggerFactory.getLogger(ServerMainSocket.class);
        try{
            while (true){
                String trustFileName = pathToStores + "/" + keyToStoreFile;
                System.setProperty("javax.net.ssl.keyStore", trustFileName);
                System.setProperty("javax.net.ssl.keyStorePassword", passphrase);

                SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
                SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(8006);
                SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
                log.info("Server socket open at: " + sslSocket.toString());
                new Thread(new ServerThread(sslSocket)).start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
