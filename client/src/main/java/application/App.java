package application;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class App extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }



    @Override
    public void start(Stage stage) throws Exception {
//запуск основного окна
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/application/1.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 870, 600);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

// запуск окна регистрации сразу при включении приложения поверх основного окна
        FXMLLoader regFxmlLoader = new FXMLLoader(App.class.getResource("/application/login.fxml"));
        Scene regScene = new Scene(regFxmlLoader.load(), 320, 200);
        Stage regStage = new Stage();
        regStage.setTitle("Вход/регистрация");
        regStage.setScene(regScene);
        regStage.initModality(Modality.APPLICATION_MODAL);
        regStage.show();



    }
}
