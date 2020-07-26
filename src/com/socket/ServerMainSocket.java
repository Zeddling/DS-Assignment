package com.socket;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;

import java.net.Socket;

@Slf4j
public class ServerMainSocket {

    public static void main(String[] args){
        Logger log = LoggerFactory.getLogger(ServerMainSocket.class);
        try{
            ServerSocket server = new ServerSocket(8006);
            while (true){
                Socket client = server.accept();
                log.info("Server socket open at: " + client.toString());
                new Thread(new ServerThread(client)).start();
            }
        }catch (IOException e){
            log.error(e.getMessage());
        }
    }

}
