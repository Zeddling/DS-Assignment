package com.ui;

import com.client.ssl.SSLClientThread;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.List;

public class Main extends Application {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static SecureRandom secureRandom;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("insert.fxml"));
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.setTitle("Toy Information");
        primaryStage.show();
    }


    public static void main(String[] args) {
        log.info("Application setup beginning");
        launch(args);
        log.info("Application started");
    }
}
