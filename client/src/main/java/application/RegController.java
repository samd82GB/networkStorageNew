package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class RegController {
    private Controller controller;
    private Client client;


    @FXML
    public TextField loginField;
    @FXML
    public TextField passwordField;
    @FXML
    public Label textEnter;
    @FXML
    public Label textOK;




    @FXML
    public void tryToReg(ActionEvent actionEvent) {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        controller.registration(login, password);

    }
    @FXML
    public void tryEnter(ActionEvent actionEvent) {

    }
    @FXML
    public void closeRegWindow(ActionEvent actionEvent) {

    }

    public void regResult (String result) {
        System.out.println(result);
        if (result.equals("regOK")) {
            textOK.setVisible(true);
        } else {
            textOK.setVisible(false);
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }



}
