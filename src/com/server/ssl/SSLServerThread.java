package com.server.ssl;

import com.aes.EncryptorAESGCM;
import com.dao.h2.ToyDatabase;
import com.rmi.ServerRMI;
import com.rmi.ServerRMIHandler;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;

public class SSLServerThread extends Thread {

    private static int port = 8010;               //  Listening port
    private KeyStore clientKeyStore;                    //  KeyStore for storing the client's public key
    private KeyStore serverKeyStore;                    //  KeyStore for storing our public/private key pair
    private SSLContext sslContext;                      //  Used to generate a SocketFactory
    static private final String passphrase = "123456";  // Passphrase for accessing keystore
    private static SecureRandom secureRandom;           //  A source of secure random numbers
    private static final Logger log = LoggerFactory.getLogger(SSLServerThread.class);
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    //  Constructor
    public SSLServerThread(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }


    private void setupClientKeyStore() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        clientKeyStore = KeyStore.getInstance("JKS");
        clientKeyStore.load( new FileInputStream("/path-to-file/client.public"), "public".toCharArray() );
        log.info("Client key store setup complete.");
    }

    private void setupServerKeyStore() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        serverKeyStore = KeyStore.getInstance("JKS");
        serverKeyStore.load( new FileInputStream("/path-to-file/server.private" ), passphrase.toCharArray() );
        log.info("Server key store setup complete.");
    }

    private void setupSSLContext() throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, KeyManagementException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance( "SunX509" );
        trustManagerFactory.init( clientKeyStore );

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance( "SunX509" );
        keyManagerFactory.init( serverKeyStore, passphrase.toCharArray() );

        sslContext = SSLContext.getInstance( "TLS" );
        sslContext.init( keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), secureRandom );

        log.info("SSL context setup complete.");
    }

    private SSLSocket connect(int port) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException, KeyManagementException {
        setupClientKeyStore();
        setupServerKeyStore();
        setupSSLContext();
        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
        SSLServerSocket sslServerSocket = ( SSLServerSocket ) sslServerSocketFactory.createServerSocket(port);

        //  Force client authentication
        //  sslServerSocket.setNeedClientAuth(true);

        SSLSocket server = (SSLSocket) sslServerSocket.accept();
        log.info("Connection complete: " + server);
        return server;
    }

    private void fetchAllRecords() throws RemoteException, AlreadyBoundException {
        ServerRMIHandler serverRMIHandler = new ServerRMIHandler();
        //  Export remote object to the stub
        ServerRMI stub = (ServerRMI) UnicastRemoteObject.exportObject(serverRMIHandler, 8010);
        //  Bind stub in the registry
        Registry registry = LocateRegistry.getRegistry();

        //  Fetch database records
        ToyDatabase database = new ToyDatabase();
        List<String> list = database.showAll();
        registry.bind(String.valueOf(list), stub);
        log.info("Fetch all RMI ready");
    }

    @SneakyThrows
    @Override
    public void run(){
        SSLSocket server;
        try {
            server = connect(port);

            //  Read object input stream
            List<String> toyDetails;
            ObjectInputStream objectInputStream = new ObjectInputStream(server.getInputStream());
            Object object = objectInputStream.readObject();
            byte[] cipherText = (byte[]) object;
            toyDetails = decryptStringToList(cipherText);
            if (!toyDetails.isEmpty()) {
                log.info("Server received: " + toyDetails);
                //  run database.createTable() if running H2 for the first time
                ToyDatabase database = new ToyDatabase();
                database.save(toyDetails);

                //  Send response
                int response = 200;
                log.info("Sending response code");
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(server.getOutputStream());
                PrintWriter printWriter = new PrintWriter(outputStreamWriter);
                printWriter.println(response);
                printWriter.flush();
                log.info("Response code sent");
                server.close();
            }
        } catch (IOException | CertificateException | NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException | KeyManagementException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage());
        } finally {
            try {
                server = connect(port);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(server.getOutputStream());
                PrintWriter printWriter = new PrintWriter(outputStreamWriter);
                printWriter.println(500);   //  Internal server error
                printWriter.flush();
                server.close();
            } catch (IOException | CertificateException | NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException | KeyManagementException e) {
                log.error(e.getMessage());
            }
        }
    }

    private static List<String> decryptStringToList(byte[] cipherText) {
        String decryptedString = null;
        try {
            decryptedString = EncryptorAESGCM.decrypt(cipherText, passphrase);
        } catch (NoSuchPaddingException | InvalidKeySpecException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            log.error(e.getMessage());
        }
        List<String> list = Arrays.asList(decryptedString.split(","));
        return list;
    }

}




