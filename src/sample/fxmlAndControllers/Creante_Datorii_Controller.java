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

public class Creante_Datorii_Controller  implements Initializable {
    @FXML
    private ComboBox<String> tipComboBox, aferentaComboBox;
    @FXML
    private ComboBox<Integer> elementIdComboBox;
    @FXML
    private ComboBox<Integer> produsIdComboBox;
    @FXML
    private DatePicker termenLimitaDatePicke, dataAchitatDatePiker;
    @FXML
    private TextField valoareTextField, scopRemunerareTextField;
    @FXML
    private Text invalidTipText, invalidAferentaText, invalidTermenLimitaText, invalidElementId, invalidValoare;
    @FXML
    private Text invalidScopText, invalidIdProdus, scopRemunerareText, produseIdText;
    @FXML
    private Pane succesPane;

    private float valoareMaxFactura;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tipComboBox.getItems().addAll("Datorie", "Creanta");
        aferentaComboBox.getItems().addAll("Factura", "Personal");

        aferentaComboBox.getSelectionModel().selectedItemProperty().addListener(((observableValue, s, t1) -> {
            if(!tipComboBox.getSelectionModel().isEmpty()){
                invalidAferentaText.setText("");
                invalidTipText.setText("");
                getElementIdFromDb();
            }else{
                invalidTipText.setText("Trebuie sa alegeti si tipul!");
            }
            if(aferentaComboBox.getValue().equals("Personal")){
                getIdProduse();
                scopRemunerareTextField.setVisible(true);
                produsIdComboBox.setVisible(true);
                scopRemunerareText.setVisible(true);
                produseIdText.setVisible(true);
            } else {
                scopRemunerareTextField.setVisible(false);
                produsIdComboBox.setVisible(false);
                scopRemunerareText.setVisible(false);
                produseIdText.setVisible(false);
            }
        }));

        tipComboBox.getSelectionModel().selectedItemProperty().addListener(((observableValue, s, t1) -> {
            if(!aferentaComboBox.getSelectionModel().isEmpty()){
                invalidAferentaText.setText("");
                invalidTipText.setText("");
                getElementIdFromDb();
            }else{
                invalidAferentaText.setText("Trebuie sa alegeti aferentul!");
            }
        }));

        elementIdComboBox.getSelectionModel().selectedItemProperty().addListener(((observableValue, integer, t1) -> {
            if(aferentaComboBox.getValue().equals("Factura")){
                getValoareMaxFacturi();
            }else{
                valoareTextField.clear();
            }
        }));
    }

    //GET DATA FROM DB
    private void getElementIdFromDb(){
        elementIdComboBox.getItems().clear();
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getIdQuery;
        String columnName;
        if(aferentaComboBox.getValue().equals("Factura")){
            columnName = "_id_factura";
            if(tipComboBox.getValue().equals("Datorie")){
                getIdQuery = "SELECT _id_factura FROM facturi where tip_intrare_iesire = 'Intrare'";
            }else{
                getIdQuery = "SELECT _id_factura FROM facturi where tip_intrare_iesire = 'Iesire'";
            }
        }else{
            columnName = "_id_personal";
            getIdQuery = "SELECT _id_personal FROM personal";
        }
        try {
            ResultSet getIDElementResultSet = conn.createStatement().executeQuery(getIdQuery);
            while(getIDElementResultSet.next()){
                elementIdComboBox.getItems().add(getIDElementResultSet.getInt(columnName));
            }
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void getValoareMaxFacturi(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getValoareMaxQuery = "SELECT cantitate * pret AS valoare FROM facturi where _id_factura = '" + elementIdComboBox.getValue()+"'";
        String getTotalSumFromDatoriiCreanteQuery = "SELECT SUM(valoare) AS suma FROM datorii_creante WHERE "
                +"factura_id = '" + elementIdComboBox.getValue() +"'";
        try{
            ResultSet getValoareMaxResultSet = conn.createStatement().executeQuery(getValoareMaxQuery);
            if(getValoareMaxResultSet.next()){
                valoareMaxFactura = Float.parseFloat(getValoareMaxResultSet.getString("valoare"));
                ResultSet getTotalSumOfResultSet = conn.createStatement().executeQuery(getTotalSumFromDatoriiCreanteQuery);
                if(getTotalSumOfResultSet.next()){
                    try{
                        valoareMaxFactura -= Float.parseFloat(getTotalSumOfResultSet.getString("suma"));
                    } catch (NullPointerException e){

                    }
                }
                valoareTextField.setText(String.valueOf(valoareMaxFactura));
            }
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void getIdProduse(){
        produsIdComboBox.getItems().clear();
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getIdProduseQuery = "SELECT _id_produs FROM produse order by _id_produs";
        try {
            ResultSet getIdProduseResultSet = conn.createStatement().executeQuery(getIdProduseQuery);
            while(getIdProduseResultSet.next()){
                produsIdComboBox.getItems().add(getIdProduseResultSet.getInt("_id_produs"));
            }
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    //INSERT INTO DB
    private void insertFacturiData(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String insertionQuery;
        if(aferentaComboBox.getValue().equals("Factura")){
            insertionQuery = "INSERT INTO datorii_creante(tip, "
                    +"termen_limita, valoare, data_achitat, factura_id) VALUES"
                    +"(?,?,?,?,?)";
        }else {
            insertionQuery = "INSERT INTO datorii_creante(tip, "
                    +"termen_limita, valoare, data_achitat, persoana_id) VALUES"
                    +"(?,?,?,?,?)";
        }

        try{
            PreparedStatement preparedStatement = conn.prepareStatement(insertionQuery);
            preparedStatement.setString(1, tipComboBox.getValue());
            try{
                preparedStatement.setDate(2, Date.valueOf(termenLimitaDatePicke.getValue()));
            } catch (NullPointerException e){
                preparedStatement.setDate(2, null);
            }
            preparedStatement.setFloat(3, Float.parseFloat(valoareTextField.getText()));
            try{
                preparedStatement.setDate(4, Date.valueOf(dataAchitatDatePiker.getValue()));
            } catch (NullPointerException e){
                preparedStatement.setDate(4, null);
            }
            preparedStatement.setInt(5, elementIdComboBox.getValue());
            preparedStatement.execute();

            if(tipComboBox.getValue().equals("Datorie") && aferentaComboBox.getValue().equals("Personal")){
                String addCostQuery = "INSERT INTO costul_productiei(tip, scop, valoare, persoana_id, produs_id) " //?????
                        + "VALUES (?,?,?,?,?)";
                PreparedStatement costuriPreparedStatemen = conn.prepareStatement(addCostQuery);
                costuriPreparedStatemen.setString(1,"Remunerarea muncii");
                costuriPreparedStatemen.setString(2,scopRemunerareTextField.getText());
                costuriPreparedStatemen.setFloat(3,Float.parseFloat(valoareTextField.getText()));
                costuriPreparedStatemen.setInt(4,elementIdComboBox.getValue());
                costuriPreparedStatemen.setInt(5,produsIdComboBox.getValue());
                costuriPreparedStatemen.execute();
            }
            conn.close();
            succesInsertion();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    //VALIDATION
    private boolean facturiCaseValidation(){
        if(tipComboBox.getSelectionModel().isEmpty()){
            invalidTipText.setText("Campul este obligatoriu");
            return false;
        }else if(aferentaComboBox.getSelectionModel().isEmpty()){
            invalidAferentaText.setText("Campul este obligatoriu");
            return false;
        } else if(elementIdComboBox.getSelectionModel().isEmpty()){
            invalidElementId.setText("Campul este obligatoriu");
            return false;
        } else if (valoareTextField.getText().isBlank()){
            invalidValoare.setText("Campul este obligatoriu");
            return false;
        } else{
            try{
                float checkValoare = Float.parseFloat(valoareTextField.getText());
                if(checkValoare < 0){
                    invalidValoare.setText("Valoare invalida");
                    return false;
                }
                if(!aferentaComboBox.getValue().equals("Personal")){
                    if(checkValoare > valoareMaxFactura){
                        invalidValoare.setText("Valoare incorecta");
                        return false;
                    }
                }
            } catch (NumberFormatException e){
                invalidValoare.setText("Se cere un numar real");
                return false;
            }
        }
        return true;
    }

    private boolean personalDatorieValidation(){
        if(scopRemunerareTextField.getText().length() < 5){
            invalidScopText.setText("Sunt necesare cel putin 5 caractere");
            return false;
        }else if (produsIdComboBox.getSelectionModel().isEmpty()){
            invalidIdProdus.setText("Alegeti produsul la care se refera costul");
            return false;
        }
        return true;
    }

    //BUTTONS
    public void saveButtonOnAction(){
        Stream.of(invalidAferentaText, invalidElementId, invalidValoare, invalidTipText,
                invalidTermenLimitaText).forEach(text -> text.setText(""));
        if(!aferentaComboBox.getSelectionModel().isEmpty()){
            if(facturiCaseValidation()){
                if(aferentaComboBox.getValue().equals("Personal") && tipComboBox.getValue().equals("Datorie")){
                    if(personalDatorieValidation()){
                        insertFacturiData();
                    }
                }else{
                    insertFacturiData();
                }
            }
        } else{
            invalidTipText.setText("Campul este obligatoriu");
        }
    }

    public void cancelButtonOnAction(){
        Stage stage = (Stage) valoareTextField.getScene().getWindow();
        stage.close();
    }

    private void succesInsertion(){
        succesPane.setVisible(true);
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
        visiblePause.setOnFinished(event -> succesPane.setVisible(false));
        visiblePause.play();
    }

}

