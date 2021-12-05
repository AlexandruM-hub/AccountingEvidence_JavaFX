package sample.fxmlAndControllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.DatabaseConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class Allow_Access_Controller implements Initializable {
    @FXML
    private ComboBox<Integer> idPersoanaComboBox;
    @FXML
    private TextField usernameTextField;
    @FXML
    private Text invalidIdPersoana, invalidUsername, invalidParola, invalidParolaAdmin;
    @FXML
    private PasswordField userPasswordField, adminPasswordField;
    @FXML
    private Pane succesPane;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getIdPersoane();
    }

    private void getIdPersoane(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getIdQuery = "SELECT _id_personal from personal where _id_personal not in " +
                "(select persoana_id from login)";
        try{
            ResultSet idResultSet = conn.createStatement().executeQuery(getIdQuery);
            while(idResultSet.next()){
                idPersoanaComboBox.getItems().add(idResultSet.getInt("_id_personal"));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private boolean validation() throws NoSuchAlgorithmException {
        if(idPersoanaComboBox.getSelectionModel().isEmpty()){
            invalidIdPersoana.setText("Alegeti id-ul persoanei");
            return false;
        } else if(!usernameTextField.getText().matches("[a-zA-Z.]{7,20}")){
            invalidUsername.setText("Numar de caractere invalid");
            return false;
        } else if(!userPasswordField.getText().matches("[a-zA-Z0-9.]{7,20}")){
            invalidParola.setText("Parola invalida");
            return false;
        } else{
            if(!checkUsername()){
                return false;
            } else {
                if(!checkAdmnPassword()){
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkAdmnPassword() throws NoSuchAlgorithmException {
        String encryptedString = encryptPassword(adminPasswordField.getText());
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getPasswordQuery = "SELECT password FROM login where persoana_id = 1";
        try{
            ResultSet resultSet = conn.createStatement().executeQuery(getPasswordQuery);
            if(resultSet.next()){
                if(!encryptedString.equals(resultSet.getString("password"))){
                    invalidParolaAdmin.setText("Parola gresita");
                    return false;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return true;
    }

    private String encryptPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.update(password.getBytes());
        byte[] bytes = m.digest();
        StringBuilder s = new StringBuilder();
        for (byte aByte : bytes) {
            s.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }
        return s.toString();
    }

    private boolean checkUsername(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String checkUsernameQuery = "SELECT username from login";
        try{
            ResultSet checkUsernameResultSet = conn.createStatement().executeQuery(checkUsernameQuery);
            while(checkUsernameResultSet.next()){
                if(userPasswordField.getText().equals(checkUsernameResultSet.getString("username"))){
                    invalidUsername.setText("Usernameul deja exista!");
                    return false;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return true;
    }

    private void insertDataInDb(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String insertData = "INSERT INTO login(username, password, persoana_id) values(?,?,?)";
        try{
            PreparedStatement preparedStatement = conn.prepareStatement(insertData);
            preparedStatement.setString(1,usernameTextField.getText());
            preparedStatement.setString(2,encryptPassword(userPasswordField.getText()));
            preparedStatement.setInt(3, idPersoanaComboBox.getValue());
            preparedStatement.execute();
            successInsertion();
        } catch (SQLException | NoSuchAlgorithmException e){
            e.printStackTrace();
        }
    }

    public void saveButtonOnAction() throws NoSuchAlgorithmException {
        Stream.of(invalidIdPersoana, invalidParola, invalidParolaAdmin,
                invalidUsername).forEach(text -> text.setText(""));
        if(validation()){
            insertDataInDb();
        }
    }

    public void cancelButtonOnAction(){
        Stage stage = (Stage) usernameTextField.getScene().getWindow();
        stage.close();
    }

    private void successInsertion(){
        succesPane.setVisible(true);
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
        visiblePause.setOnFinished(event -> succesPane.setVisible(false));
        visiblePause.play();
    }
}
