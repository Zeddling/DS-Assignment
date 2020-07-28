package com.ui;

import com.client.ssl.SSLClientThread;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

//
public class ToyInfoController {
    private static final Logger log = LoggerFactory.getLogger(ToyInfoController.class);
    private int statusCode;

    @FXML
    private TextField toyCode;

    @FXML
    private TextField toyName;

    @FXML
    private TextField description;

    @FXML
    private TextField price;

    @FXML
    private TextField manufactureDate;

    @FXML
    private TextField batchNo;

    @FXML
    private TextField companyName;

    @FXML
    private TextField streetAddress;

    @FXML
    private TextField zipCode;

    @FXML
    private TextField comment;

    @FXML
    protected Button submitToyInfo;

    @FXML
    protected List<String> handleToyInformation() {
        Window owner = submitToyInfo.getScene().getWindow();
        List<String> list = new ArrayList<>();
        //  Form validation
        checkIfFieldsEmpty(owner);
        list.add(toyCode.getText());
        list.add(toyName.getText());
        list.add(description.getText());
        list.add(price.getText());
        list.add(manufactureDate.getText());
        list.add(batchNo.getText());
        list.add(companyName.getText());
        list.add(streetAddress.getText());
        list.add(zipCode.getText());
        list.add(comment.getText());

        //  Send list to server
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextInt();
        log.info("Secure random created");
        SSLClientThread sslClientThread = new SSLClientThread(8010, "127.0.0.1", list, secureRandom);
        new Thread( sslClientThread ).start();
        statusCode = sslClientThread.getStatusCode();
        if ( statusCode == 200 ) {
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, owner, "Request successful",
                    "Record has been successfully saved");
            clearTextFields();
        }

        if ( statusCode == 500 )
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Request not successful",
                    "Kindly check your internet connection");
        return list;
    }

    private void checkIfFieldsEmpty(Window owner) {
        if (toyCode.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!",
                    "Please fill in all fields!");
        } else if (toyName.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Erro|",
                    "Please fill in all fields!");
        } else if (description.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Erro|",
                    "Please fill in all fields!");
        } else if (price.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Erro|",
                    "Please fill in all fields!");
        } else if (manufactureDate.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Erro|",
                    "Please fill in all fields!");
        } else if (batchNo.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Erro|",
                    "Please fill in all fields!");
        } else if (companyName.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Erro|",
                    "Please fill in all fields!");
        } else if (streetAddress.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Erro|",
                    "Please fill in all fields!");
        } else if (zipCode.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Erro|",
                    "Please fill in all fields!");
        } else if (comment.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Erro|",
                    "Please fill in all fields!");
        }
    }

    public void clearTextFields() {
        toyCode.clear();
        toyName.clear();
        description.clear();
        price.clear();
        manufactureDate.clear();
        batchNo.clear();
        companyName.clear();
        streetAddress.clear();
        zipCode.clear();
        comment.clear();
    }

    public void setStatusCode(int statusCode) {
        this.statusCode =  statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

}








