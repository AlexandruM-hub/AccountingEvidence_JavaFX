package sample.fxmlAndControllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
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

public class Change_Debts_Claims_Controller implements Initializable {
    @FXML
    private ComboBox<Integer> idDatorieCreantaComboBox;
    @FXML
    private TextField valueTextField;
    @FXML
    private DatePicker termenLimitaDatePicker, dataAchitatDatePicker;
    @FXML
    private Text invalidValueText, invalidIdDatorieCreanta;
    @FXML
    private Pane succesPane;
    private boolean facturaCheck = true;
    private float maxValue;
    private int facturaId;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getIdDatorieCreanta();
        idDatorieCreantaComboBox.getSelectionModel().selectedItemProperty().addListener(((observableValue, integer, t1) -> {
            getDataAboutDatorieCreanta();
            if(facturaCheck){
                getMaxValueFromDb();
            }
        }));
    }
    //GET DATA FROM DB
    private void getDataAboutDatorieCreanta(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getDataAboutClaimDebtQuery = "SELECT termen_limita, valoare, data_achitat, factura_id FROM datorii_creante WHERE _id_datorie_creanta ="
                +idDatorieCreantaComboBox.getValue();
        try{
            ResultSet getDataResultSet = conn.createStatement().executeQuery(getDataAboutClaimDebtQuery);
            if(getDataResultSet.next()){
                facturaCheck = getDataResultSet.getString("factura_id") != null;
                if(facturaCheck){
                    facturaId = getDataResultSet.getInt("factura_id");
                }
                valueTextField.setText(getDataResultSet.getString("valoare"));
                termenLimitaDatePicker.getEditor().setText(getDataResultSet.getString("termen_limita"));
                dataAchitatDatePicker.getEditor().setText(getDataResultSet.getString("data_achitat"));
            }
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void getIdDatorieCreanta(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getIdDatorieCreantaQuery = "SELECT _id_datorie_creanta from datorii_creante";
        try{
            ResultSet getIdDatorieCreanteResultSet = conn.createStatement().executeQuery(getIdDatorieCreantaQuery);
            while(getIdDatorieCreanteResultSet.next()){
                idDatorieCreantaComboBox.getItems().add(getIdDatorieCreanteResultSet.getInt("_id_datorie_creanta"));
            }
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    //VALIDATION
    private void getMaxValueFromDb(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getSumFromDb = "SELECT coalesce(SUM(valoare), 0) as maxsum FROM datorii_creante WHERE _id_datorie_creanta <> "
                + idDatorieCreantaComboBox.getValue() + " AND factura_id = "
                + facturaId;
        String getValueFromFactura = "SELECT cantitate * pret AS valoare FROM facturi where _id_factura = " + facturaId;
        try{
            ResultSet getValueFromFacturiResultSet = conn.createStatement().executeQuery(getValueFromFactura);
            if(getValueFromFacturiResultSet.next()){
                maxValue = Float.parseFloat(getValueFromFacturiResultSet.getString("valoare"));

                ResultSet getSumResultSet = conn.createStatement().executeQuery(getSumFromDb);
                if(getSumResultSet.next()){
                    maxValue -= Float.parseFloat(getSumResultSet.getString("maxsum"));
                }
            }
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private boolean validation(){
        if(idDatorieCreantaComboBox.getSelectionModel().isEmpty()){
            invalidIdDatorieCreanta.setText("Alegeti Datoria / Creanta!");
            return false;
        } else if(valueTextField.getText().isBlank()){
            invalidValueText.setText("Campul nu poate fi gol");
            return false;
        } else{
            try{
                float checkValue = Float.parseFloat(valueTextField.getText());
                if(checkValue < 0 || checkValue > maxValue){
                    invalidValueText.setText("Valoare invalida");
                    return false;
                }
            } catch (NumberFormatException e){
                invalidValueText.setText("Se cere un numar real");
            }
        }
        return true;
    }

    //UPDATE VALUES IN DB
    private void updateDataInDb(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();

        String updateQuery = "UPDATE datorii_creante SET termen_limita = ?, valoare = ?, data_achitat = ? WHERE _id_datorie_creanta = "
                + idDatorieCreantaComboBox.getValue();
        try{
            PreparedStatement preparedStatement = conn.prepareStatement(updateQuery);
            try{
                preparedStatement.setDate(1,Date.valueOf(termenLimitaDatePicker.getValue()));
            } catch (NullPointerException e){
                preparedStatement.setDate(1,null);
            }
            preparedStatement.setFloat(2, Float.parseFloat(valueTextField.getText()));
            try{
                preparedStatement.setDate(3, Date.valueOf(dataAchitatDatePicker.getValue()));
            } catch (NullPointerException e){
                preparedStatement.setDate(3, null);
            }
            preparedStatement.execute();
            conn.close();
            succesUpdated();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void succesUpdated(){
        succesPane.setVisible(true);
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
        visiblePause.setOnFinished(event -> succesPane.setVisible(false));
        visiblePause.play();
    }

    //BUTTONS
    public void saveButtonOnAction(){
        invalidIdDatorieCreanta.setText("");
        invalidValueText.setText("");
        if(validation()){
            updateDataInDb();
        }
    }

    public void cancelButtonOnAction(){
        Stage stage = (Stage) valueTextField.getScene().getWindow();
        stage.close();
    }

}
