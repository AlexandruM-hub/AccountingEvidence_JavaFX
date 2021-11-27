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

public class Costs_Delete_Controller implements Initializable {
    @FXML
    private ComboBox<Integer> idCostsComboBox;
    @FXML
    private Pane confirmationPane, succesPane;
    @FXML
    private Text invalidIdCost;

    private float cantitate;
    private int idElement;
    private String tip ="";
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getIdComboBox();
        idCostsComboBox.getSelectionModel().selectedItemProperty().addListener(observable -> getCantitateFromAssets());
    }

    private void getIdComboBox(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getIdQuery = "SELECT _id_cost from costul_productiei";
        try{
            ResultSet idCostResultSet = conn.createStatement().executeQuery(getIdQuery);
            while(idCostResultSet.next()){
                idCostsComboBox.getItems().add(idCostResultSet.getInt("_id_cost"));
            }
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void getCantitateFromAssets(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String query = "SELECT tip, cantitate, tip_cost_id from costul_productiei where _id_cost = " +
                idCostsComboBox.getValue();
        try{
            ResultSet resultSet = conn.createStatement().executeQuery(query);
            if(resultSet.next()){
               tip = resultSet.getString("tip");
               if(tip.equals("Activ")){
                   cantitate = resultSet.getFloat("cantitate");
                   idElement = resultSet.getInt("tip_cost_id");
               }
            }
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private boolean idValidation(){
        if(idCostsComboBox.getSelectionModel().isEmpty()){
            invalidIdCost.setText("Alegeti Costul");
            return false;
        }
        return true;
    }

    private void stergereaCostului(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String delitionQuery = "DELETE FROM costul_productiei WHERE _id_cost = '" + idCostsComboBox.getValue() +"'";
        try{
            conn.createStatement().execute(delitionQuery);
            if(tip.equals("Activ")){
                String updateAssetsQuery = "UPDATE assets SET cantitate_stock += " + cantitate +
                        " where _id_asset = " + idElement;
                conn.createStatement().execute(updateAssetsQuery);
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cancelButtonOnAction(){
        Stage stage = (Stage) idCostsComboBox.getScene().getWindow();
        stage.close();
    }

    public void nuButtonOnAction(){
        confirmationPane.setVisible(false);
    }

    public void submitButtonOnAction(){
        invalidIdCost.setText("");
        if(idValidation()){
            confirmationPane.setVisible(true);
        }
    }

    public void daButtonOnAction(){
        stergereaCostului();
        confirmationPane.setVisible(false);
        succesPane.setVisible(true);
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
        visiblePause.setOnFinished(event -> succesPane.setVisible(false));
        visiblePause.play();
    }
}
