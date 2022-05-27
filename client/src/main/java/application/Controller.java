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
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import message.AuthMessage;
import message.RegMessage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller {
    @FXML
    public ListView clientListView;
    @FXML
    public ListView serverListView;
    @FXML
    public TextField clientTextField;
    @FXML
    public TextField serverTextField;
    @FXML
    public Button clRenewButton;
    @FXML
    public Button clSendButton;
    @FXML
    public Button clCreateDirButton;
    @FXML
    public Button srvRenewButton;
    @FXML
    public Button srvRcvButton;
    @FXML
    public Button srvCreateDirButton;
    @FXML
    public Button srvDelButton;
    @FXML
    private Button regButton;


    private Stage regStage;
    private Stage mainStage;
    private RegController regController;
    private Client client;
    private Channel channel;
    private FXMLLoader fxmlLoader = new FXMLLoader();
    private Parent fxmlReg;
    private Stage stage;

    @FXML
        private void initialize() throws IOException {
            fxmlLoader.setLocation(getClass().getResource("/application/login.fxml"));
            fxmlReg = fxmlLoader.load();
            regController = fxmlLoader.getController();
            startClient();
            //завершаем работу приложения нажатием на крестик главного окна и закрываем канал и группу клиента
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
            regController.setController(this);
       }


    public void startClient () {

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

    public Stage getMainStage() {
        return mainStage;
    }



    //действие кнопки Обновить на клиенте
    public void clRenew(ActionEvent actionEvent) {
    }
    //действие кнопки Отправить на клиенте
    public void clSend(ActionEvent actionEvent) {
    }
    //действие кнопки Создать папку на клиенте
    public void clCreateDir(ActionEvent actionEvent) {
    }
    //действие кнопки Обновить на сервере
    public void srvRenew(ActionEvent actionEvent) {
    }
    //действие кнопки Получить на сервере
    public void srvRcv(ActionEvent actionEvent) {
    }
    //действие кнопки Создать папку на сервере
    public void srvCreateDir(ActionEvent actionEvent) {
    }
    //действие кнопки Удалить на сервере
    public void srvDelete(ActionEvent actionEvent) {
    }
}


