package application;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import message.RegMessage;

import java.io.IOException;

public class Controller {

    private Stage regStage;
    private RegController regController;
    private Client client;
    private String result;


    public void tryEnter(ActionEvent actionEvent) throws IOException {
        if (regStage == null) {
            createRegWindow();
        }
    }

    private void createRegWindow() throws IOException {
        FXMLLoader regFxmlLoader = new FXMLLoader(App.class.getResource("/application/login.fxml"));
        Scene regScene = new Scene(regFxmlLoader.load(), 320, 200);
        Stage regStage = new Stage();

        regStage.setTitle("Вход/регистрация");
        regStage.setScene(regScene);
        regStage.initModality(Modality.APPLICATION_MODAL);
        regStage.show();

        regController = regFxmlLoader.getController();
        regController.setController(this);
    }

    public void registration(String login, String password) {
        RegMessage regMessage = new RegMessage();
        regMessage.setLogin(login);
        regMessage.setPassword(password);
        client = new Client();

        client.setRegMessage(regMessage);
        client.start();
        result = client.getResult();
        regController.regResult(result);

    }

    public String getResult() {
        return result;
    }
}
