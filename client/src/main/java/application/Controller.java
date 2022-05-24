package application;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import message.AuthMessage;
import message.RegMessage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller {
    @FXML
    public Button regButton;

    private Stage regStage;
    private Stage mainStage;
    private RegController regController;
//    private Client client;

    private RegMessage regMessage;
    private AuthMessage authMessage;

    private FXMLLoader fxmlLoader = new FXMLLoader();
    private Parent fxmlReg;

        @FXML
        private void initialize() throws IOException {
            fxmlLoader.setLocation(getClass().getResource("/application/login.fxml"));
            fxmlReg = fxmlLoader.load();
            regController = fxmlLoader.getController();
//            startClient(); //если стартовать клиента тут, то блокируется поток FX и окна будут работать только после отключения приложения

}


    public void tryEnter(ActionEvent actionEvent) throws IOException {
        showRegWindow();
    }

    private void showRegWindow() {
         if (regStage == null) {
             regStage = new Stage();

             regStage.setTitle("Вход/регистрация");
             regStage.setScene(new Scene(fxmlReg));
             regStage.initModality(Modality.WINDOW_MODAL);
             regStage.initOwner(mainStage);
         }

            regStage.show();
                    startClient(); //если стартовать клиента тут, то окно регистрации зависает
       }


//
//    public void registration(String login, String password) {
//        regMessage = new RegMessage();
//        regMessage.setLogin(login);
//        regMessage.setPassword(password);
//        startClient();
//
//    }
//
//    public void authorization(String login, String password) {
//        authMessage = new AuthMessage();
//        authMessage.setLogin(login);
//        authMessage.setPassword(password);
//        startClient();
//
//    }

    public void startClient () {
        Client client = new Client(regController, this);
        client.start();
}
    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
        }

}


