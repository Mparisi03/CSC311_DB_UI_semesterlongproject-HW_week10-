package viewmodel;

import com.azure.storage.blob.BlobClient;
import dao.DbConnectivityClass;
import dao.StorageUploader;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Person;
import service.MyLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DB_GUI_Controller implements Initializable {
    StorageUploader store = new StorageUploader();
    @FXML
    private Button DeleteBtn;
    @FXML
    private Button addBtn;
    @FXML
    private Button editBtn;

    @FXML
    private MenuItem ChangePic;

    @FXML
    private MenuItem ClearItem;

    @FXML
    private MenuItem CopyItem;

    @FXML
    private MenuItem editItem;

    @FXML
    private MenuItem logOut;

    @FXML
    private MenuItem newItem;


    @FXML
    private ComboBox<sele_major> Major;


    @FXML
    TextField first_name, last_name, department, email, imageURL;
    @FXML
    ImageView img_view;
    @FXML
    MenuBar menuBar;
    @FXML
    private TableView<Person> tv;
    @FXML
    private TableColumn<Person, Integer> tv_id;
    @FXML
    private TableColumn<Person, String> tv_fn, tv_ln, tv_department, tv_major, tv_email;
    private final DbConnectivityClass cnUtil = new DbConnectivityClass();
    private final ObservableList<Person> data = cnUtil.getData();

    @FXML
    private ProgressBar progressBar;


    public enum sele_major {
        Business, CSC, CPIS
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Major.setItems(FXCollections.observableArrayList(sele_major.values()));

        editBtn.disableProperty().bind(Bindings.isEmpty(tv.getSelectionModel().getSelectedItems()));
        DeleteBtn.disableProperty().bind(Bindings.isEmpty(tv.getSelectionModel().getSelectedItems()));

        addBtn.disableProperty().bind(Bindings.createBooleanBinding(() ->
                first_name.getText().isEmpty()||
                last_name.getText().isEmpty()||
                department.getText().isEmpty()||
                email.getText().isEmpty()||
                !fn_regex()||
                !ln_regex()||
                !dept_regex()||

                email_regex(),
                first_name.textProperty(),
                last_name.textProperty(),
                department.textProperty(),
                email.textProperty()
                ));

        try {
            tv_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            tv_fn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            tv_ln.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            tv_department.setCellValueFactory(new PropertyValueFactory<>("department"));
            tv_major.setCellValueFactory(new PropertyValueFactory<>("major"));
            tv_email.setCellValueFactory(new PropertyValueFactory<>("email"));
            tv.setItems(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean fn_regex(){
        final String regex = "(\\b[a-zA-Z]{2,26})";
        return checker(first_name.getText(), regex);
    }
    protected boolean ln_regex(){
        final String regex = "(\\b[a-zA-Z]{2,26})";
        return checker(last_name.getText(), regex);
    }
    protected boolean dept_regex(){
        final String regex = "(\\b[a-zA-Z]{2,30})";
        return checker(department.getText(), regex);
    }
    protected boolean email_regex(){
        final String regex = "(\\^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$)";
        return checker(email.getText(), regex);
    }

   protected boolean checker(String string, String regex){
       Pattern pattern = Pattern.compile(regex);
       Matcher matcher = pattern.matcher(string);
       return matcher.matches();

   }

   protected boolean isVaild(){
        return !first_name.getText().isEmpty() &&
                !last_name.getText().isEmpty() &&
                !department.getText().isEmpty() &&
                !email.getText().isEmpty();
   }

   private void configMenu(){
      ChangePic.disableProperty().bind(Bindings.isEmpty(tv.getSelectionModel().getSelectedItems()));
      ClearItem.disableProperty().bind(Bindings.isEmpty(tv.getSelectionModel().getSelectedItems()));
      CopyItem.disableProperty().bind(Bindings.isEmpty(tv.getSelectionModel().getSelectedItems()));
      editItem.disableProperty().bind(Bindings.isEmpty(tv.getSelectionModel().getSelectedItems()));
      logOut.disableProperty().bind(Bindings.isEmpty(tv.getSelectionModel().getSelectedItems()));
      newItem.disableProperty().bind(Bindings.isEmpty(tv.getSelectionModel().getSelectedItems()));
   }

    @FXML
    protected void addNewRecord() {
        if(!isVaild()){
            System.out.println("Plase fill out all flieds propley");
        }else {

            Person p = new Person(first_name.getText(), last_name.getText(), department.getText(),
                    Major.getValue().toString(), email.getText(), imageURL.getText());
            cnUtil.insertUser(p);
            cnUtil.retrieveId(p);
            p.setId(cnUtil.retrieveId(p));
            data.add(p);
            clearForm();
        }

    }

    @FXML
    protected void clearForm() {
        first_name.setText("");
        last_name.setText("");
        department.setText("");
        Major.getSelectionModel().clearSelection();
        email.setText("");
        imageURL.setText("");
    }

    @FXML
    protected void logOut(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").getFile());
            Stage window = (Stage) menuBar.getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void closeApplication() {
        System.exit(0);
    }

    @FXML
    protected void displayAbout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/about.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root, 600, 500);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void editRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        int index = data.indexOf(p);

        String major = Major.getValue().toString();
        Person p2 = new Person(index + 1, first_name.getText(), last_name.getText(), department.getText(),
                major, email.getText(),  imageURL.getText());
        cnUtil.editUser(p.getId(), p2);
        data.remove(p);
        data.add(index, p2);
        tv.getSelectionModel().select(index);
    }

    @FXML
    protected void deleteRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        int index = data.indexOf(p);
        cnUtil.deleteRecord(p);
        data.remove(index);
        tv.getSelectionModel().select(index);
    }

    @FXML
    protected void showImage() {
        File file = (new FileChooser()).showOpenDialog(img_view.getScene().getWindow());
        if (file != null) {
            img_view.setImage(new Image(file.toURI().toString()));
        }
        Task<Void> uploadTask = createUploadTask(file, progressBar);
        progressBar.progressProperty().bind(uploadTask.progressProperty());
        new Thread(uploadTask).start();
    }

    @FXML
    protected void addRecord() {
        showSomeone();
    }

    @FXML
    protected void selectedItemTV(MouseEvent mouseEvent) {
        Person p = tv.getSelectionModel().getSelectedItem();

        first_name.setText(p.getFirstName());
        last_name.setText(p.getLastName());
        department.setText(p.getDepartment());

        // Ensure that p.getMajor() returns a string
        String majorString = p.getMajor();  // Assuming p.getMajor() returns a String

        // Map the string value to the corresponding Major enum
        sele_major majorEnum = mapStringToMajor(majorString);

        // Set the ComboBox value
        Major.setValue(majorEnum);

        email.setText(p.getEmail());
        imageURL.setText(p.getImageURL());
    }

    private sele_major mapStringToMajor(String majorString) {

        switch (majorString) {
            case "CSC":
                return sele_major.CSC;
            case "CPIS":
                return sele_major.CPIS;
            case "Business":
                return sele_major.Business;

            default:
                return null; // Or handle the default case as needed
        }
    }

    public void lightTheme(ActionEvent actionEvent) {
        try {
            Scene scene = menuBar.getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.getScene().getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
            System.out.println("light " + scene.getStylesheets());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void darkTheme(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) menuBar.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/darkTheme.css").toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSomeone() {
        Dialog<Results> dialog = new Dialog<>();
        dialog.setTitle("New User");
        dialog.setHeaderText("Please specify…");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField textField1 = new TextField("Name");
        TextField textField2 = new TextField("Last Name");
        TextField textField3 = new TextField("Email ");
        ObservableList<sele_major> options = FXCollections.observableArrayList(sele_major.values());
        ComboBox<sele_major> comboBox = new ComboBox<>(options);
        comboBox.getSelectionModel().selectFirst();
        dialogPane.setContent(new VBox(8, textField1, textField2,textField3, comboBox));
        Platform.runLater(textField1::requestFocus);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(textField1.getText(),
                        textField2.getText(), comboBox.getValue());
            }
            return null;
        });
        Optional<Results> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((Results results) -> {
            MyLogger.makeLog(
                    results.fname + " " + results.lname + " " + results.major);
        });
    }

    private static enum Major {Business, CSC, CPIS}

    private static class Results {

        String fname;
        String lname;
        sele_major major;

        public Results(String name, String date, sele_major venue) {
            this.fname = name;
            this.lname = date;
            this.major = venue;
        }
    }

    private Task<Void> createUploadTask(File file, ProgressBar progressBar) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                BlobClient blobClient = store.getContainerClient().getBlobClient(file.getName());
                long fileSize = Files.size(file.toPath());
                long uploadedBytes = 0;

                try (FileInputStream fileInputStream = new FileInputStream(file);
                     OutputStream blobOutputStream = blobClient.getBlockBlobClient().getBlobOutputStream()) {

                    byte[] buffer = new byte[1024 * 1024]; // 1 MB buffer size
                    int bytesRead;

                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        blobOutputStream.write(buffer, 0, bytesRead);
                        uploadedBytes += bytesRead;

                        // Calculate and update progress as a percentage
                        int progress = (int) ((double) uploadedBytes / fileSize * 100);
                        updateProgress(progress, 100);
                    }
                }

                return null;
            }
        };
    }

}