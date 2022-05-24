package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import message.AuthMessage;
import message.RegMessage;

import java.io.IOException;

public class RegController  {

    private Controller controller;
//    private Stage regStage;
    private RegMessage regMessage;
    private AuthMessage authMessage;

    @FXML
    private Button closeButton;
    @FXML
    private TextField loginField;
    @FXML
    private TextField passwordField;
    @FXML
    private Label textEnter;
    @FXML
    private Label textOK;




    @FXML
    public void tryToReg(ActionEvent actionEvent) {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();
        registration(login, password);

    }
    @FXML
    public void tryEnter(ActionEvent actionEvent) {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();
        authorization(login, password);

    }
    @FXML
    public void closeRegWindow(ActionEvent actionEvent) {
        Node source = (Node)actionEvent.getSource();
        Stage regStage = (Stage) source.getScene().getWindow();
        regStage.close();
    }

    public void regResult (String result) {
        System.out.println("result: "+result);
        if (result.equals("regOK")) {
            textOK.setText("Регистрация прошла успешно!");
        } else {
            textOK.setText("Регистрация не удалась!");
        }
    }


    public void registration(String login, String password) {
        regMessage = new RegMessage();
        regMessage.setLogin(login);
        regMessage.setPassword(password);


    }

    public void authorization(String login, String password) {
        authMessage = new AuthMessage();
        authMessage.setLogin(login);
        authMessage.setPassword(password);


    }

//
//    public void setController(Controller controller) {
//        this.controller = controller;
//    }

    public RegMessage getRegMessage () {
        return regMessage;
    }

    public AuthMessage getAuthMessage () {
        return authMessage;
    }


}
