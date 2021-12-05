package sample.fxmlAndControllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
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

public class Delete_Employee_Controller implements Initializable {
    @FXML
    private ComboBox<Integer> idPersoanaComboBox;
    @FXML
    private Text invalidIdPersoana, invalidPassword;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Pane confirmationPane, succesPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getIdPersoane();
    }

    private void getIdPersoane(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getIdPersoane = "SELECT _id_personal from personal where _id_personal <> 1 order by _id_personal";
        try{
            ResultSet getIdResultSet = conn.createStatement().executeQuery(getIdPersoane);
            while (getIdResultSet.next()){
                idPersoanaComboBox.getItems().add(getIdResultSet.getInt("_id_personal"));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private boolean validation() throws NoSuchAlgorithmException {
        if(idPersoanaComboBox.getSelectionModel().isEmpty()){
            invalidIdPersoana.setText("Alegeti Persoana");
            return false;
        } else{
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(passwordField.getText().getBytes());
            byte[] bytes = m.digest();
            StringBuilder s = new StringBuilder();
            for (byte aByte : bytes) {
                s.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            String encryptedString = s.toString();
            DatabaseConnection db = new DatabaseConnection();
            Connection conn = db.getConnection();
            try{
                ResultSet resultSet = conn.createStatement().executeQuery("select password from login where persoana_id = 1");
                if(resultSet.next()){
                    if(!encryptedString.equals(resultSet.getString("password"))){
                        invalidPassword.setText("Parola incorecta");
                        return false;
                    }
                }
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
        return true;
    }

    public void submitButtonOnAction() throws NoSuchAlgorithmException {
        invalidIdPersoana.setText("");
        invalidPassword.setText("");
        if(validation()){
            confirmationPane.setVisible(true);
        }
    }

    public void cancelButtonOnAction(){
        Stage stage = (Stage) invalidPassword.getScene().getWindow();
        stage.close();
    }

    public void nuButtonOnAction(){
        confirmationPane.setVisible(false);
    }

    public void daButtonOnAction(){
        deleteEmployee();
        confirmationPane.setVisible(false);
        succesPane.setVisible(true);
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
        visiblePause.setOnFinished(event -> succesPane.setVisible(false));
        visiblePause.play();
    }

    private void deleteEmployee(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String deleteQuery = "delete from personal where _id_personal = " + idPersoanaComboBox.getValue();
        try{
            conn.createStatement().execute(deleteQuery);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
