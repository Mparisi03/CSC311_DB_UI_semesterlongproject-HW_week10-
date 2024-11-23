package viewmodel;
import dao.DbConnectivityClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Person;
import service.UserSession;

public class SignUpController {

    @FXML
    private TextField confirm_password;

    @FXML
    private Button goBackBtn;

    @FXML
    private Button newAccountBtn;

    @FXML
    private TextField password;

    @FXML
    private TextField username;

    private DbConnectivityClass dbConnectivityClass = new DbConnectivityClass();

    public void createNewAccount(ActionEvent actionEvent) {
        String un = this.username.getText();
        String confirmPass = this.confirm_password.getText();
        String pass = this.password.getText();

         //checks if empty
        if (un.isEmpty() || confirmPass.isEmpty() || pass.isEmpty()) {
            showAlert("Empty", "Username/password can't be empty");
            //check if password and confirmPass are the same
        } else if (!pass.equals(confirmPass)) {
            showAlert("Mismatch", "Passwords do not match");
        } else if (userExists(un)) {
            showAlert("Exists", "Username already exists");
        } else {
            // Create a Person object for the new user, if all pass
            Person newUser = new Person(un, "", "", "", un, "");  // Assuming only username and email are needed at signup
            UserSession session = UserSession.getInstace(un, pass, "N/A");
            session.setCurrentUser(newUser);  // Store the user in the session

            // Call method to save user data to the database
            saveUserToDatabase(un, pass);

            // Show success message
            showAlert("Success", "New account created");
        }
        try {
            // Navigate back to login screen
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/view/login.fxml")), 900, 600);
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void goBack(ActionEvent actionEvent) {
        try {
            // Navigate back to login screen
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/view/login.fxml")), 900, 600);
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean userExists(String username) {
        if (UserSession.checkUserExistsInSession(username)) {
            return true; // User exists in session
        }
        if (dbConnectivityClass.checkUserExists(username)) {
            return true; // User exists in the database
        }
        return false; //return false if not
    }

    private void saveUserToDatabase(String username, String password) {
        // Create a new Person object with the user data
        Person newUser = new Person(0, username, password, "default_department", "default_major", username + "@example.com", "");
        // Save to the database
        dbConnectivityClass.insertUser(newUser);
    }
}