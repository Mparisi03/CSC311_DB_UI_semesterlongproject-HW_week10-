package viewmodel;

import dao.DbConnectivityClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Person;
import service.UserSession;

import java.sql.*;

import static java.nio.file.Files.exists;


public class SignUpController {

    @FXML
    private TextField comfirm_password;

    @FXML
    private Button goBackBtn;

    @FXML
    private Button newAccountBtn;

    @FXML
    private TextField password;

    private DbConnectivityClass dbConnectivityClass = new DbConnectivityClass();

    @FXML
    private TextField username;
    public void createNewAccount(ActionEvent actionEvent) {
        String un = this.username.getText();
        String comfirmpass = this.comfirm_password.getText();
        String pass = this.password.getText();

        if(un.isEmpty() || comfirmpass.isEmpty() || pass.isEmpty()) {
            showAlert("Empty"," username/password can't be empty ");
        } else if (!pass.equals(comfirmpass)) {
            showAlert("not equal"," passwords does not match ");

        } else {
            if(userExists(un,pass)){
                showAlert("Username already exists"," username already exists ");

            }else {
                UserSession session = UserSession.getInstace(un, pass, "user");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("New account created");
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Info for the user. Message goes here");
        alert.showAndWait();
    }

    public void goBack(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();


    }
    private static boolean userExists(String username, String password) {
        Boolean exists = false;

        UserSession session = UserSession.getInstace(username, password);

        // Check if the session exists (if session is created)
        if (session != null && session.getUserName().equals(username) && session.getPassword().equals(password)) {
            exists = true;  // If the session exists with the given credentials
        }

        return exists;
    }
}
