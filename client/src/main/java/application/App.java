package application;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
//запуск основного окна
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/application/1.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        Controller controller = fxmlLoader.getController();
        controller.setMainStage(stage);

        stage.setTitle("Сетевое хранилище");
        stage.setScene(scene);
        stage.show();



    }

    public static void main(String[] args) {
        launch(args);
    }
}
