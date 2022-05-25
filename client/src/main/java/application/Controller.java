package application;

import io.netty.channel.Channel;
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



    private Client client;
    private Channel channel;

    private RegMessage regMessage;
    private AuthMessage authMessage;

    private FXMLLoader fxmlLoader = new FXMLLoader();
    private Parent fxmlReg;
    private Stage stage;

    @FXML
        private void initialize() throws IOException {
            fxmlLoader.setLocation(getClass().getResource("/application/login.fxml"));
            fxmlReg = fxmlLoader.load();
            regController = fxmlLoader.getController();
            startClient();

            Platform.runLater(() -> {
                    stage = (Stage) regButton.getScene().getWindow();

                //обработка нажатия крестика на окне
                stage.setOnCloseRequest(event -> { //нажали крестик
                    System.out.println("Goodbye!"); //попрощались
                    if (channel != null) { //Если канал открыт, то закрываем его
                        try {
                            channel.close();
                            client.getGroup().shutdownGracefully();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            });


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
//        client = new Client(regController, this);
        client = new Client();
        client.start();
        channel = client.getChannel();
        client.setRegController(regController);
        client.setController(this);
        regController.setChannel(channel);
}
    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
        }

    public Client getClient() {
        return client;
    }
//
//    public void setClient(Client client) {
//        this.client = client;
//    }
}


