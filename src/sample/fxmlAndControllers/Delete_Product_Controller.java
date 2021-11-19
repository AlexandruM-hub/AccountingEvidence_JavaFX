package sample.fxmlAndControllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.DatabaseConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Delete_Product_Controller implements Initializable {
    @FXML
    private ComboBox<Integer> idProdus;
    @FXML
    private Text invalidIdProdus;
    @FXML
    private Pane confirmationPane, succesPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getIdProdusFromDB();
    }

    //BD MANIPULATION
    private void getIdProdusFromDB(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getIdQuery = "SELECT _id_produs FROM produse ORDER BY _id_produs";
        try{
            ResultSet idProdusResultSet = conn.createStatement().executeQuery(getIdQuery);
            while(idProdusResultSet.next()){
                idProdus.getItems().add(idProdusResultSet.getInt("_id_produs"));
            }
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void stergereaProdusului() {
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String deleteProductQuery = "DELETE FROM produse WHERE _id_produs = '"
                + idProdus.getValue() + "'";
        try{
            conn.createStatement().execute(deleteProductQuery);
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    //ID VALIDATION
    private boolean idValidation(){
        if(idProdus.getSelectionModel().isEmpty()){
            invalidIdProdus.setText("Alegeti produsul");
            return false;
        }
        return true;
    }

    //BUTTONS
    public void submitButtonOnAction(){
        invalidIdProdus.setText("");
        if(idValidation()){
            confirmationPane.setVisible(true);
        }
    }

    public void cancelButtonOnAction(){
        Stage stage = (Stage) invalidIdProdus.getScene().getWindow();
        stage.close();
    }

    public void nuButtonOnAction(){
        confirmationPane.setVisible(false);
    }

    public void daButtonOnAction(){
        stergereaProdusului();
        confirmationPane.setVisible(false);
        succesPane.setVisible(true);
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
        visiblePause.setOnFinished(event -> succesPane.setVisible(false));
        visiblePause.play();
    }


}
