package sample.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.DatabaseConnection;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class Controller {

    @FXML
    private Button cancelButton;
    @FXML
    private Label loginMessageError;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    public void cancelButtonOnAction(ActionEvent e){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }


    public void  loginButtonOnAction(ActionEvent e){
        loginMessageError.setText("Username");
        if(!usernameField.getText().isBlank() && !passwordField.getText().isBlank()){
            validateLogin();
        }
        else{
            loginMessageError.setText("Login and password can't be null");
        }

    }

    public void validateLogin(){
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();
        String verifyLogin = "SELECT count(1) FROM login WHERE username ='" + usernameField.getText() + "'AND password ='" + passwordField.getText() +"'";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);

            while(queryResult.next()){
                if(queryResult.getInt(1)==1){
                    loginMessageError.setText("Welcome"); //load new scene
                } else{
                    loginMessageError.setText("Invalid login. Try again");
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }

    }

}
