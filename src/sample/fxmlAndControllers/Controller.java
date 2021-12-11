package sample.fxmlAndControllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.DatabaseConnection;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Controller {

    @FXML
    private Button cancelButton;
    @FXML
    private Label loginMessageError;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    public void cancelButtonOnAction(){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void  loginButtonOnAction() throws NoSuchAlgorithmException {
        if(!usernameField.getText().isBlank() && !passwordField.getText().isBlank()){
            validateLogin();
        }
        else{
            loginMessageError.setText("Completati campurile");
        }
    }

    public void validateLogin() throws NoSuchAlgorithmException {
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.update(passwordField.getText().getBytes());
        byte[] bytes = m.digest();
        StringBuilder s = new StringBuilder();
        for (byte aByte : bytes) {
            s.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }
        String encryptedString = s.toString();
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection conn = connectNow.getConnection();
        String getPass = "select password from login where username = '" +usernameField.getText()+"'";
        String verifyUser = "select count(1) from login where username = '" + usernameField.getText() +"'";
        try {
            ResultSet verifyUsername = conn.createStatement().executeQuery(verifyUser);
            if(verifyUsername.next()){
                if(verifyUsername.getInt(1)==1){
                    ResultSet verifyPasswordResultSet = conn.createStatement().executeQuery(getPass);
                    if(verifyPasswordResultSet.next()){
                        if(verifyPasswordResultSet.getString("password").equals(encryptedString)){
                            cancelButtonOnAction();
                            loadAppScene();
                        }else{
                            loginMessageError.setText("Acces respins");
                        }
                    }
                } else{
                    loginMessageError.setText("Acces Respins");
                }
            }
        }
        catch(SQLException | IOException e){
            e.printStackTrace();
        }

    }

    private void loadAppScene() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("app.fxml"));
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene(root));
        stage.setTitle("GT Mazureac Alexandru");
        Image iconIMG = new Image("file:D:\\Lucrari de laborator\\Semestrul 3\\Teza de an\\TezaProiect\\src\\css\\tractor.png");
        stage.getIcons().add(iconIMG);
        stage.show();
    }
}
