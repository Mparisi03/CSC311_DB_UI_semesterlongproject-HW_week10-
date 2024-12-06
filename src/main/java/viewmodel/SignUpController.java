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
    private TextField Last_name;

    @FXML
    private TextField first_name;

    @FXML
    private TextField Email;

    private DbConnectivityClass dbConnectivityClass = new DbConnectivityClass();

    public void createNewAccount(ActionEvent actionEvent) {
        String em = this.Email.getText();
        String fn = this.first_name.getText();
        String ln = this.Last_name.getText();
        String confirmPass = this.confirm_password.getText();
        String pass = this.password.getText();

         //checks if empty
        if (em.isEmpty()|| fn.isEmpty()||ln.isEmpty() || confirmPass.isEmpty() || pass.isEmpty()) {
            showAlert("Empty", "Username/password can't be empty");
            //check if password and confirmPass are the same
        } else if (!pass.equals(confirmPass)) {
            showAlert("Mismatch", "Passwords do not match");
        } if (userExists(em)) {
            showAlert("Exists", "Username already exists");
            return; // Return early if user already exists
        } else {



            // Create a Person object for the new user, if all pass
            Person newUser = new Person(fn, ln, "", "", em, "");  // Assuming only username and email are needed at signup
            UserSession session = UserSession.getInstace(fn, ln, "N/A");
            session.setCurrentUser(newUser);  // Store the user in the session

            // Call method to save user data to the database
            saveUserToDatabase(em,pass,fn,ln);

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
            Throwable cause = e.getCause();
            cause.printStackTrace();
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

    private void saveUserToDatabase(String username, String password,String first_name,String last_name) {
        // Create a new Person object with the user data
        Person newUser = new Person(0, first_name, last_name, "default_department", "default_major", username, "");
        // Save to the database
        dbConnectivityClass.insertUser(newUser);
    }
}