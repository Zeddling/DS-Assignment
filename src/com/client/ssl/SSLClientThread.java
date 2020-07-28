package com.client.ssl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SSLClient extends Thread {

    private static int port = 8010;
    private static String host = "127.0.0.1";
    private KeyStore clientKeyStore;                    //  KeyStore for storing our public/private key pair
    private KeyStore serverKeyStore;                    //  KeyStore for storing the server's public key
    private SSLContext sslContext;                      //  Used to generate a SocketFactory
    static private final String passphrase = "123456";  // Passphrase for accessing keystore
    private static SecureRandom secureRandom;           //  A source of secure random numbers
    private static final Logger log = LoggerFactory.getLogger(SSLClient.class);

    //  Constructor
    public SSLClient ( int port ) {
        this.port = port;
    }

    public static void main(String[] args) {
        log.info( "Creating secure random" );
        secureRandom = new SecureRandom();
        secureRandom.nextInt();
        log.info("Secure random created");
        new Thread( new SSLClient(port) ).start();
    }

    //  Read client's key pair from client.private.
    //  Also read server's public key
    private void setupClientKeyStore() {
        KeyStore clientKeyStore = null;
        try {
            clientKeyStore = KeyStore.getInstance("JKS");
            clientKeyStore.load(new FileInputStream( "/home/zeddling/Documents/Projects/DS Socket Protocol/src/com/client/ssl/client.private" ), passphrase.toCharArray());
            log.info("Client keystore setup complete");
        } catch (KeyStoreException e) {
            log.error(e.getMessage());
        } catch (CertificateException e) {
            log.error(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage());
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void setupServerKeystore() {
        try {
            serverKeyStore = KeyStore.getInstance( "JKS" );
            serverKeyStore.load( new FileInputStream( "/home/zeddling/Documents/Projects/DS Socket Protocol/src/com/client/ssl/server.public" ), "public".toCharArray() );
            log.info("Server keystore complete");
        } catch (KeyStoreException e) {
            log.error(e.getMessage());
        } catch (CertificateException e) {
            log.error(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage());
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void setupSSLContext() {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance( "SunX509" );
            trustManagerFactory.init( serverKeyStore );

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance( "SunX509" );
            keyManagerFactory.init( clientKeyStore, passphrase.toCharArray() );

            sslContext = SSLContext.getInstance( "TLS" );
            sslContext.init( keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), secureRandom );
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    private SSLSocket connect(int port, String host) {
        setupClientKeyStore();
        setupServerKeystore();
        setupSSLContext();
        SSLSocket sslSocket = null;
        try {
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            sslSocket = ( SSLSocket ) sslSocketFactory.createSocket(host, port);
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        return sslSocket;
    }

    public List<String> setToyDetails() {
        List<String> toyDetails = new ArrayList<>();
        toyDetails.add(0, "RS-7938080");
        toyDetails.add(1, "Ball");
        toyDetails.add(2, "Round bouncy object");
        toyDetails.add(3, "700.00");
        toyDetails.add(4, "07-01-2020");
        toyDetails.add(5, "TML-73462787");
        toyDetails.add(6, "Adidas");
        toyDetails.add(7, "Tuskys T-Mall");
        toyDetails.add(8, "0100");
        toyDetails.add(9, "Has a good design");
        return toyDetails;
    }

    @SneakyThrows
    @Override
    public void run () {
        SSLSocket client = connect(port, host);
        List<String> toyDetails = setToyDetails();

        //  Send object
        ObjectOutputStream objectOutput;
        try {
            log.info("Communication through socket: " + client);
            objectOutput = new ObjectOutputStream(client.getOutputStream());
            objectOutput.writeObject(toyDetails);
            objectOutput.flush();
            log.info("Object sent successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //  Receive response
        try {
            InputStream input = client.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            log.info("Server says: " + br.readLine());
            client.close();
            log.info("Socket connection closed");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
