package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;

public class NewPath {
    @FXML
    public TextArea textArea;
    private Controller controller;

    private String newDir;

    public String getNewDir() {
        return newDir;
    }
    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void enterNewPathDir(ActionEvent actionEvent) throws IOException {
        newDir = textArea.getText();
        controller.makeNewDir(controller.getCurDir());
        closeStage(actionEvent);
    }

    public void closeStage(ActionEvent actionEvent) {
        textArea.clear();
        Node source = (Node)actionEvent.getSource();
        Stage pathStage = (Stage) source.getScene().getWindow();
        pathStage.close();

    }
}
