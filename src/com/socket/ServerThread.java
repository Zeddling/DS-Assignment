package com.socket;


import com.dao.ToyInfo;
import com.dao.h2.H2JDBCUtils;
import com.dao.h2.ToyDatabase;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

@Slf4j
public class ServerThread extends Thread {
    private static Socket client;
    private static final Logger log = LoggerFactory.getLogger(ServerThread.class);
    // private ThreadFunctions threadFunctions;
    public ServerThread(Socket socket){
        client = socket;
    }

    @SneakyThrows
    @Override
    public void run(){
        log.info("Socket connection started with client: " + client.getInetAddress().getHostAddress());

        //  Read message
        try {
            List<ToyInfo> toyDetails;
            ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());
            Object object = objectInputStream.readObject();
            log.info(object.toString());
            toyDetails = (List<ToyInfo>) object;
            log.info("Received: " + toyDetails);

            //  Save in database
            ToyDatabase database = new ToyDatabase();
            database.creatTable();
            database.save(toyDetails);
        } catch (IOException | ClassNotFoundException e) {
            log.error(e.getMessage());
        } catch (SQLException e){
            H2JDBCUtils.printSQLException(e);
        }

        //  Send response
        String response = "Object received";
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(client.getOutputStream());
            PrintWriter printWriter = new PrintWriter(outputStreamWriter);
            printWriter.println(response);
            printWriter.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }



}
