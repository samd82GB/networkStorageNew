package application;

import io.netty.channel.Channel;
import javafx.application.Platform;
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
//  private Stage regStage;
    private RegMessage regMessage;
    private AuthMessage authMessage;

    private Client client;
    private Channel channel;

    @FXML
    private Button regButton;
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
        regButton.setDisable(false);
        loginField.clear();
        passwordField.clear();
        Node source = (Node)actionEvent.getSource();
        Stage regStage = (Stage) source.getScene().getWindow();
        regStage.close();
    }

    public void regResult (String result) {

        System.out.println("result: "+result);
        Platform.runLater(() -> {
            if (result.equals("regOK")) {
                textOK.setText("Регистрация прошла успешно!");
                regButton.setDisable(true);
            } else if (result.equals("regError")) {
                textOK.setText("Регистрация не удалась!");
            } else if (result.equals("authError")) {
                textOK.setText("Ошибка входа!");
            } else if (result.startsWith("auth")) {
                String[] token = result.split(" ", 3);
                textOK.setText("Успешный вход пользователя: "+token[1]);
                Stage stage = (Stage) regButton.getScene().getWindow();
                stage.close();
                Stage mainStage = controller.getMainStage();
                mainStage.setTitle("Хранилище пользователя № "+token[1]);
                controller.clientDirView();
                controller.getRegButton().setDisable(true);
            }



        });
    }



    public void registration(String login, String password) {
        regMessage = new RegMessage();
        regMessage.setLogin(login);
        regMessage.setPassword(password);
        if (regMessage.getLogin()!=null&&regMessage.getPassword()!=null) {
            channel.writeAndFlush(regMessage);
        }

    }

    public void authorization(String login, String password) {
        authMessage = new AuthMessage();
        authMessage.setLogin(login);
        authMessage.setPassword(password);
        if (authMessage.getLogin()!=null&&authMessage.getPassword()!=null) {
            channel.writeAndFlush(authMessage);
        }

    }


    public void setController(Controller controller) {
        this.controller = controller;
    }

//    public void setClient(Client client) {
//        this.client = client;
//    }
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

}
