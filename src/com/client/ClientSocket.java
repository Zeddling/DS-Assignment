package com.client;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ClientSocket {
    private static final Logger log = LoggerFactory.getLogger(ClientSocket.class);

    public static void main(String[] args){
        try {
            Socket socket = new Socket("127.0.0.1", 8006);
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

            ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
            objectOutput.writeObject(toyDetails);
            objectOutput.flush();
            log.info("Object sent to server");

            //	Get response
            InputStream input = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            log.info("Server says: " + br.readLine());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
