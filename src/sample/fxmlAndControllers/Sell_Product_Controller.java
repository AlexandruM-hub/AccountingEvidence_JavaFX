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

public class Sell_Product_Controller implements Initializable {
    @FXML
    private TextField nrFacturiiTextField, contractantTextField, denumireTextField, cantitateTextField, pretTextField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<Integer> idProdusComboBox;
    @FXML
    private Text invalidNrFacturii, invalidContractant, invalidData, invalidDenumire, invalidCantitate, invalidPret, invalidIdProdus;
    @FXML
    private Pane succesPane;

    private float cantitateStockMaxima;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getIdProdus();
        idProdusComboBox.getSelectionModel().selectedItemProperty().addListener((ObsValue, s,t1) -> getDenumireCantitateProdus());
    }

    //GET DATA FROM DB
    private void getIdProdus(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getIdQuery = "SELECT _id_produs FROM produse WHERE cantitate_ramasa > 0";
        try{
            ResultSet getIdProduseResultSet = conn.createStatement().executeQuery(getIdQuery);
            while(getIdProduseResultSet.next()){
                idProdusComboBox.getItems().add(getIdProduseResultSet.getInt("_id_produs"));
            }
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void getDenumireCantitateProdus(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getDenumireCantitateQuery = "SELECT denumire_produs, cantitate_ramasa FROM produse WHERE _id_produs = '"
                + idProdusComboBox.getValue() +"'";
        try{
            ResultSet getDenumireCantitateResultSet = conn.createStatement().executeQuery(getDenumireCantitateQuery);
            if(getDenumireCantitateResultSet.next()){
                denumireTextField.setText(getDenumireCantitateResultSet.getString("denumire_produs"));
                cantitateTextField.setText(getDenumireCantitateResultSet.getString("cantitate_ramasa"));
                cantitateStockMaxima = Float.parseFloat(getDenumireCantitateResultSet.getString("cantitate_ramasa"));
            }
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    //INSERT DATA IN DB
    private void insertDataInDb(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String produseChangeQuery = "UPDATE produse SET cantitate_ramasa = '" + (cantitateStockMaxima-Float.parseFloat(cantitateTextField.getText()))
                +"' WHERE _id_produs = '" +idProdusComboBox.getValue() +"'";
        try{
            PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO facturi(nr_factura, tip_intrare_iesire,"
            +" contractant, data, tip_marfa, denumire_marfa, cantitate, "
            +"pret, produse_id) VALUES(?,?,?,?,?,?,?,?,?)");
            preparedStatement.setString(1,nrFacturiiTextField.getText());
            preparedStatement.setString(2,"Iesire");
            preparedStatement.setString(3,contractantTextField.getText());
            preparedStatement.setDate(4, Date.valueOf(datePicker.getValue()));
            preparedStatement.setString(5, "Produse");
            preparedStatement.setString(6,denumireTextField.getText());
            preparedStatement.setFloat(7,Float.parseFloat(cantitateTextField.getText()));
            preparedStatement.setFloat(8,Float.parseFloat(pretTextField.getText()));
            preparedStatement.setInt(9, idProdusComboBox.getValue());
            preparedStatement.execute();
            conn.createStatement().execute(produseChangeQuery);
            succesValidation();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    //VALIDARE
    private boolean fieldValidation(){
        if(nrFacturiiTextField.getText().length() < 5 ){
            invalidNrFacturii.setText("Sunt necesare minim 5 caractere");
            return false;
        } else if (contractantTextField.getText().length() < 3) {
            invalidContractant.setText("Sunt necesare cel putin 4 caractere");
            return false;
        } else if (datePicker.getEditor().getText().isBlank()) {
            invalidData.setText("Data este obligatorie");
            return false;
        } else if (denumireTextField.getText().length() < 3) {
            invalidDenumire.setText("Sunt necesare cel putin 4 caractere");
            return false;
        } else if (cantitateTextField.getText().isBlank()) {
            invalidCantitate.setText("Campul nu poate fi gol");
            return false;
        }  else if (pretTextField.getText().isBlank()) {
            invalidPret.setText("Campul nu poate fi gol");
            return false;
        } else{
            try{
                Float.parseFloat(cantitateTextField.getText());
                if(Float.parseFloat(cantitateTextField.getText()) < 0 ||
                        Float.parseFloat(cantitateTextField.getText()) > cantitateStockMaxima) {
                    invalidCantitate.setText("Valoare invalida");
                    return false;
                }
            }catch (NumberFormatException e){
                invalidCantitate.setText("Se cere un numar rational");
                return false;
            }
            try{
                Float.parseFloat(pretTextField.getText());
                if(Float.parseFloat(pretTextField.getText()) < 0){
                    invalidPret.setText("Valoare invalida");
                    return false;
                }
            }catch (NumberFormatException e){
                invalidPret.setText("Se cere un numar rational");
                return false;
            }
        }
        return true;
    }

    private void succesValidation(){
        succesPane.setVisible(true);
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
        visiblePause.setOnFinished(event -> succesPane.setVisible(false));
        visiblePause.play();
    }

    //BUTTONS
    public void saveButtonOnAction(){
        Stream.of(invalidContractant, invalidCantitate, invalidData, invalidDenumire, invalidIdProdus,
                invalidPret, invalidNrFacturii).forEach(text -> text.setText(""));
        if(fieldValidation()){
            insertDataInDb();
        }
    }

    public void cancelButtonOnAction(){
        Stage stage = (Stage) nrFacturiiTextField.getScene().getWindow();
        stage.close();
    }
}
