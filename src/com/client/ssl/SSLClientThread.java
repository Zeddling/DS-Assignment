package com.client.ssl;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.net.ssl.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.aes.CryptoUtils;
import com.aes.EncryptorAESGCM;
import com.rmi.ServerRMI;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class SSLClientThread extends Thread {

    private static int port;
    private static String host;
    private KeyStore clientKeyStore;                    //  KeyStore for storing our public/private key pair
    private KeyStore serverKeyStore;                    //  KeyStore for storing the server's public key
    private SSLContext sslContext;                      //  Used to generate a SocketFactory
    private static final String passphrase = "123456";  // Passphrase for accessing keystore
    private static final Logger log = LoggerFactory.getLogger(SSLClientThread.class);
    private static SecureRandom secureRandom;           //  A source of secure random numbers
    private List<String> toyDetails;
    private int statusCode;
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    //  Constructor
    public SSLClientThread(int port, String host, List<String> toyDetails, SecureRandom secureRandom ) {
        this.port = port;
        this.host = host;
        this.toyDetails = toyDetails;
        this.secureRandom = secureRandom;
    }

    //  Read client's key pair from client.private.
    //  Also read server's public key
    private void setupClientKeyStore() {
        try {
            clientKeyStore = KeyStore.getInstance("JKS");
            clientKeyStore.load(new FileInputStream( "/path-to-file/client.private" ), passphrase.toCharArray());
            log.info("Client keystore setup complete");
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            log.error(e.getMessage());
        }
    }

    private void setupServerKeystore() {
        try {
            serverKeyStore = KeyStore.getInstance( "JKS" );
            serverKeyStore.load( new FileInputStream( "/path-to-file/server.public" ), "public".toCharArray() );
            log.info("Server keystore complete");
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
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
        } catch (KeyStoreException | UnrecoverableKeyException | KeyManagementException e) {
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

    @SneakyThrows
    @Override
    public void run () {
        SSLSocket client = connect(port, host);

        //  Send object
        ObjectOutputStream objectOutput;
        try {
            log.info("Communication through socket: " + client);
            objectOutput = new ObjectOutputStream(client.getOutputStream());
            log.info("Encrypting list");
            objectOutput.writeObject(encryptList(ListToString(toyDetails)));
            objectOutput.flush();
            log.info("Object sent successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //  Receive response
        try {
            InputStream input = client.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            statusCode = Integer.parseInt(br.readLine());
            log.info("Server says: " + statusCode);
            client.close();
            log.info("Socket connection closed");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private static byte[] encryptList( String listString ) {
        byte[] encryptedText = new byte[0];
        try {
            encryptedText = EncryptorAESGCM.encrypt( listString.getBytes(UTF_8), passphrase );
            log.info("List encryption successful: " + Base64.getEncoder().encodeToString(encryptedText));
        } catch ( NoSuchAlgorithmException e ) {
            log.error( e.getMessage() );
            log.debug( "Check encryption algorithm." );
        } catch ( InvalidKeySpecException e ) {
            log.error( e.getMessage() );
            log.debug( "KeySpec creation might be flawed. Check code logic." );
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            log.error(e.getMessage());
        }
        return encryptedText;
    }

    //  To be implemented
    public static List<String> getAllRecords() {
        List<String> list = new ArrayList<>();
        try {
            Registry registry = LocateRegistry.getRegistry("127.0.0.1");
            ServerRMI stub = (ServerRMI) registry.lookup("ServerRMI");
            list = stub.showAllRecords();
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getStatusCode() {
        return statusCode;
    }

    private String ListToString(List<String> list) {
        StringBuilder stringBuilder = new StringBuilder();
        String stringSeparator = ",";

        for ( String item : list ) {
            stringBuilder.append(item);
            stringBuilder.append(stringSeparator);
        }
        return stringBuilder.toString();
    }

}
