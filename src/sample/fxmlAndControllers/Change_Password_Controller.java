package sample.fxmlAndControllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class Change_Password_Controller implements Initializable {
    @FXML
    private ComboBox<Integer> idPersonalComboBox;
    @FXML
    private TextField usernameTextField;
    @FXML
    private Text invalidIdPersoana, invalidUsername, invalidParola, invalidParolaAdmin, invalidParolaAdminRestrict;
    @FXML
    private PasswordField userPasswordField, adminPasswordField, deleteAdminPasswordTextField;
    @FXML
    private Pane succesPane;
    @FXML
    private Tab changePasswordTab, restrictAccesTab;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getIdPersonal();

    }

    private void getIdPersonal(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getIdPersonal = "SELECT persoana_id from login order by persoana_id";
        try{
            ResultSet getIdResultSet = conn.createStatement().executeQuery(getIdPersonal);
            while(getIdResultSet.next()){
                idPersonalComboBox.getItems().add(getIdResultSet.getInt("persoana_id"));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private boolean validation() throws NoSuchAlgorithmException {
        if(idPersonalComboBox.getSelectionModel().isEmpty()){
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
                if(!checkAdmnPassword(adminPasswordField.getText())){
                    invalidParolaAdmin.setText("Parola gresita");
                    return false;
                }
            }
        }
        return true;
    }

    private boolean restrictAccesValidation() throws NoSuchAlgorithmException {
        if(idPersonalComboBox.getSelectionModel().isEmpty()){
            invalidIdPersoana.setText("Alegeti persoana");
            return false;
        }else if(deleteAdminPasswordTextField.getText().length() < 7 || deleteAdminPasswordTextField.getText().length() > 20 ){
            invalidParolaAdminRestrict.setText("Parola gresita");
            return false;
        }
        else {
            if(!checkAdmnPassword(deleteAdminPasswordTextField.getText())){
                invalidParolaAdminRestrict.setText("Parola gresita");
                return false;
            }
        }
        return true;
    }

    private boolean checkUsername(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String checkUsernameQuery = "SELECT username from login where persoana_id = " + idPersonalComboBox.getValue();
        try{
            ResultSet resultSet = conn.createStatement().executeQuery(checkUsernameQuery);
            if(resultSet.next()){
                if(!usernameTextField.getText().equals(resultSet.getString("username"))){
                    invalidUsername.setText("Usernameul nu coincide cu id");
                    return false;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return true;
    }

    private boolean checkAdmnPassword(String password) throws NoSuchAlgorithmException {
        String encryptedString = encryptPassword(password);
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getPasswordQuery = "SELECT password FROM login where persoana_id = 1";
        try{
            ResultSet resultSet = conn.createStatement().executeQuery(getPasswordQuery);
            if(resultSet.next()){
                if(!encryptedString.equals(resultSet.getString("password"))){
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

    public void saveButtonOnAction() throws NoSuchAlgorithmException {
        Stream.of(invalidParolaAdminRestrict, invalidParola, invalidIdPersoana, invalidUsername,
                invalidParolaAdmin).forEach(text -> text.setText(""));
        if(changePasswordTab.isSelected()){
            if(validation()){
                updatePasswordInDb();
            }
        }else if(restrictAccesTab.isSelected()){
            if(restrictAccesValidation()){
                restrictAcces();
            }
        }
    }

    private void updatePasswordInDb() throws NoSuchAlgorithmException {
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String encryptedPassword = encryptPassword(userPasswordField.getText());
        String updateQuery = "UPDATE login set password = '" + encryptedPassword +
                "' where persoana_id = " + idPersonalComboBox.getValue();
        try{
            conn.createStatement().execute(updateQuery);
            successInsertion();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void restrictAcces() {
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String restrictQuery = "delete from login where persoana_id = " + idPersonalComboBox.getValue();
        try{
            conn.createStatement().execute(restrictQuery);
            successInsertion();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void successInsertion(){
        succesPane.setVisible(true);
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
        visiblePause.setOnFinished(event -> succesPane.setVisible(false));
        visiblePause.play();
    }

    public void cancelButtonOnAction(){
        Stage stage = (Stage) idPersonalComboBox.getScene().getWindow();
        stage.close();
    }
}
