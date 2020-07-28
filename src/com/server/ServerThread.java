package com.server;


import com.dao.h2.ToyDatabase;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

@Slf4j
public class ServerThread extends Thread {
    private static SSLSocket sslSocket;
    private static final Logger log = LoggerFactory.getLogger(ServerThread.class);
    // private ThreadFunctions threadFunctions;
    public ServerThread(SSLSocket sslServerSocket){
        sslSocket = sslServerSocket;
    }

    @SneakyThrows
    @Override
    public void run(){
        log.info("Socket connection started with client: " + sslSocket.getInetAddress().getHostAddress());

        //  Read message
        try {
            List<String> toyDetails;
            ObjectInputStream objectInputStream = new ObjectInputStream(sslSocket.getInputStream());
            Object object = objectInputStream.readObject();
            log.info(object.toString());
            toyDetails = (List<String>) object;
            log.info("Received: " + toyDetails);

            //  Save in database
            ToyDatabase database = new ToyDatabase();
            database.createTable();
            database.save(toyDetails);
        } catch (IOException | ClassNotFoundException e) {
            log.error(e.getMessage());
        }

        //  Send response
        String response = "Object received";
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(sslSocket.getOutputStream());
            PrintWriter printWriter = new PrintWriter(outputStreamWriter);
            printWriter.println(response);
            printWriter.flush();

            //  Close connection
            sslSocket.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }



}
