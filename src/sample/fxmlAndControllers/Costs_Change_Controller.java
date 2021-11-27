package sample.fxmlAndControllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.DatabaseConnection;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class Costs_Change_Controller implements Initializable {
    @FXML
    private TextField scopTextField, valoareTextField;
    @FXML
    private ComboBox<Integer> idCostComboBox;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Text valoareText, invalidIdCost, invalidScop, invalidDate, invalidValoare;
    @FXML

    private Pane succesPane;
    private int idElement;
    private float maxValue;
    private boolean flag;
    private String tipElement;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getIdCost();
        idCostComboBox.getSelectionModel().selectedItemProperty().addListener(((
                observableValue, integer, t1) -> {
            getCostsInformationFromDb();
            if(flag){
                valoareText.setText("Cantitate");
                valoareTextField.setPromptText("Cantitate");
                getMaxValueActive();
            } else{
                valoareText.setText("Valoare");
                valoareTextField.setPromptText("Valoare");
                if(tipElement.equals("Remunerarea muncii")){
                    maxValue = 20000;
                } else{
                    String alteCosturiGetMaxQuery = "SELECT cantitate * pret - (SELECT SUM(valoare) from costul_productiei where" +
                            " tip_cost_id = " + idElement + " and tip = 'Alte costuri') + (select valoare from costul_productiei where " +
                            "_id_cost = "+ idCostComboBox.getValue() +  ") as valmax from facturi where _id_factura = " + idElement;
                    getMaxValueOtherCase(alteCosturiGetMaxQuery);
                }
            }
        }));

    }

    //GET DATA FROM DB
    private void getIdCost(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getIdCostsQuery = "SELECT _id_cost from costul_productiei";
        try{
            ResultSet getIdResultSet = conn.createStatement().executeQuery(getIdCostsQuery);
            while(getIdResultSet.next()){
                idCostComboBox.getItems().add(getIdResultSet.getInt("_id_cost"));
            }
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void getCostsInformationFromDb(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getDataAboutCostQuery = "SELECT tip, scop, data_cost, cantitate, valoare, tip_cost_id from costul_productiei " +
                "where _id_cost = " + idCostComboBox.getValue();
        try{
            ResultSet getDataResultSet = conn.createStatement().executeQuery(getDataAboutCostQuery);
            if (getDataResultSet.next()){
                scopTextField.setText(getDataResultSet.getString("scop"));
                datePicker.getEditor().setText(String.valueOf(getDataResultSet.getDate("data_cost")));
                idElement = getDataResultSet.getInt("tip_cost_id");
                tipElement = getDataResultSet.getString("tip");
                if(tipElement.equals("Activ")){
                    flag = true;
                    valoareTextField.setText(getDataResultSet.getString("cantitate"));
                } else{
                    flag = false;
                    valoareTextField.setText(getDataResultSet.getString("valoare"));
                }
            }
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void getMaxValueActive(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getMaxValueActive = "SELECT cantitate_stock  + (select cantitate from costul_productiei where _id_cost = " +
                idCostComboBox.getValue()+ ") as valmax from assets where _id_asset = " + idElement;
        try{
            ResultSet getMaxValueResultSet = conn.createStatement().executeQuery(getMaxValueActive);
            if(getMaxValueResultSet.next()){
                maxValue = getMaxValueResultSet.getFloat("valmax");
            }
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void getMaxValueOtherCase(String query){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        try{
            ResultSet getMaxValueResultSet = conn.createStatement().executeQuery(query);
            if(getMaxValueResultSet.next()){
                maxValue = getMaxValueResultSet.getFloat("valmax");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

    }

    //VALIDATION
    private boolean generalValidation(){
        if(idCostComboBox.getSelectionModel().isEmpty()){
            invalidIdCost.setText("Alegeti costul!");
            return false;
        } else if(scopTextField.getText().length() < 4){
            invalidScop.setText("Sunt necesare cel putin 4 caractere");
            return false;
        } else if(datePicker.getEditor().getText().isBlank()){
            invalidDate.setText("Introduceti data");
            return false;
        } else{
            try{
                float valoare = Float.parseFloat(valoareTextField.getText());
                if(valoare > maxValue || valoare < 0){
                    invalidValoare.setText("Valoare invalida. Maxim " + maxValue);
                    return false;
                }
            } catch (NumberFormatException e){
                invalidValoare.setText("Se cere un numar real!");
                return false;
            }
        }
        return true;
    }

    //UPDATE DATA IN DB
    private void updateCosts(String query){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();

        try{
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, scopTextField.getText());
            preparedStatement.setDate(2, Date.valueOf(datePicker.getEditor().getText()));
            preparedStatement.setFloat(3, Float.parseFloat(valoareTextField.getText()));
            preparedStatement.execute();
            conn.close();
            succesUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void updateActive(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String updateCostsQuery = "update assets set cantitate_stock = " +
                (maxValue - Float.parseFloat(valoareTextField.getText())) +
                " where _id_asset = " +idElement;
        try {
            conn.createStatement().execute(updateCostsQuery);
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    //BUTTONS
    public void submitButtonOnAction(){
        Stream.of(invalidIdCost, invalidDate, invalidValoare, invalidScop).forEach(text -> text.setText(""));
        if(generalValidation()){
            String query;
            if(!tipElement.equals("Activ")){
                query = "UPDATE costul_productiei SET scop = ?, " +
                        "data_cost = ?, valoare = ? where _id_cost = " + idCostComboBox.getValue();
            } else {
                query ="UPDATE costul_productiei SET scop = ?, " +
                        "data_cost = ?,cantitate = ?, valoare = " + valoareTextField.getText() +
                        " * (select pret from facturi where activ_id = " + idElement + " and (tip_marfa = 'Materiale' OR " +
                        "tip_marfa = 'OMVSD') and tip_intrare_iesire = 'Intrare') where _id_cost = " + idCostComboBox.getValue();
                updateActive();
            }
            updateCosts(query);

        }
    }

    public void cancelButtonOnAction(){
        Stage stage = (Stage) idCostComboBox.getScene().getWindow();
        stage.close();
    }

    private void succesUpdate(){
        succesPane.setVisible(true);
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
        visiblePause.setOnFinished(event -> succesPane.setVisible(false));
        visiblePause.play();
    }
}
