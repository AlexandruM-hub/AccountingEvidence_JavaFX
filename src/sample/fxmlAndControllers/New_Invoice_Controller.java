package sample.fxmlAndControllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
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
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class New_Invoice_Controller implements Initializable {
    @FXML
    private TextField nrFacturiiTextField, contractantTextField, continutTextField, cantitateTextField, pretTextField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Text invalidNrFacturii, invalidContractant, invalidDate, invalidDenumireContinut, invalidCantitate, invalidPret;
    @FXML
    private Text invalidIdProdus, idProdusText;
    @FXML
    private ComboBox<Integer> idProdusComboBox;
    @FXML
    private CheckBox checkBox;
    @FXML
    private Pane succesPane;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        checkBox.selectedProperty().addListener(((observableValue, aBoolean, t1) -> {
            if(checkBox.isSelected()){
                idProdusComboBox.setVisible(true);
                idProdusText.setVisible(true);

            } else{
                idProdusComboBox.setVisible(false);
                idProdusText.setVisible(false);
            }
        }));
        getIdProdus();
    }

    private void getIdProdus(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        try{
            ResultSet resultSet = conn.createStatement().executeQuery("SELECT _id_produs from produse");
            while(resultSet.next()){
                idProdusComboBox.getItems().add(resultSet.getInt("_id_produs"));
            }
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    private boolean validation(){
        if(nrFacturiiTextField.getText().length() < 5){
            invalidNrFacturii.setText("Sunt necesare minim 5 caractere");
            return false;
        } else if (contractantTextField.getText().length() < 4){
            invalidContractant.setText("Sunt necesare minim 4 caractere");
            return false;
        } else if(continutTextField.getText().length() < 4){
            invalidDenumireContinut.setText("Sunt necesare minim 4 caractere");
            return false;
        } else if(datePicker.getEditor().getText().isBlank()) {
            invalidDate.setText("Campul nu poate fi gol");
            return false;
        } else{
            try{
                datePicker.getConverter().fromString(datePicker.getEditor().getText());
            } catch (DateTimeParseException e){
                invalidDate.setText("Formatul datii este incorect");
                return false;
            }
            try{
                Float.parseFloat(cantitateTextField.getText());
                if(Float.parseFloat(cantitateTextField.getText()) < 0){
                    invalidCantitate.setText("Numar invalid");
                    return false;
                }
            } catch (NumberFormatException e){
                invalidCantitate.setText("Introduceti un numar real");
                return false;
            }
            try{
                if(Float.parseFloat(pretTextField.getText()) < 0){
                    invalidPret.setText("Numar invalid");
                    return false;
                }
            } catch (NumberFormatException e){
                invalidPret.setText("Introduceti un numar real");
                return false;
            }
        }
        return true;
    }

    //DATA INSERTION
    private void insertDataInDb(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String query = "INSERT INTO facturi(nr_factura, tip_intrare_iesire, contractant, " +
                "data, tip_marfa, denumire_marfa, cantitate, pret) Values(?,?,?,?,?,?,?,?)";
        try{
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, nrFacturiiTextField.getText());
            preparedStatement.setString(2, "Intrare");
            preparedStatement.setString(3,contractantTextField.getText());
            preparedStatement.setDate(4,Date.valueOf(datePicker.getValue()));
            preparedStatement.setString(5,continutTextField.getText());
            preparedStatement.setString(6,continutTextField.getText());
            preparedStatement.setFloat(7,Float.parseFloat(cantitateTextField.getText()));
            preparedStatement.setFloat(8,Float.parseFloat(pretTextField.getText()));
            preparedStatement.execute();
            conn.close();
            successInsertion();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void insertDataInCosts(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String query = "INSERT INTO costul_productiei(tip, scop, data_cost," +
                " valoare, tip_cost_id, produs_id) values(?,?,?,?,(SELECT _id_factura from facturi ORDER by _id_factura DESC LIMIT 1),?)";
        try{
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1,"Alte costuri");
            preparedStatement.setString(2, continutTextField.getText());
            preparedStatement.setDate(3,Date.valueOf(datePicker.getValue()));
            preparedStatement.setFloat(4,Float.parseFloat(cantitateTextField.getText())*Float.parseFloat(pretTextField.getText()));
            preparedStatement.setInt(5,idProdusComboBox.getValue());
            preparedStatement.execute();
            conn.close();
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
    //BUTTONS
    public void saveButtonOnAction(){
        Stream.of(invalidContractant, invalidCantitate, invalidPret, invalidDate, invalidDenumireContinut,
                invalidNrFacturii, invalidIdProdus).forEach(text -> text.setText(""));
        if(validation()){
            if(checkBox.isSelected()){
                if(!idProdusComboBox.getSelectionModel().isEmpty()){
                    insertDataInDb();
                    insertDataInCosts();
                } else{
                    invalidIdProdus.setText("Introduceti produsul la care se refera costul");
                }
            }else{
                insertDataInDb();

            }
        }
    }

    public void cancelButtonOnAction(){
        Stage stage = (Stage) nrFacturiiTextField.getScene().getWindow();
        stage.close();
    }

}
