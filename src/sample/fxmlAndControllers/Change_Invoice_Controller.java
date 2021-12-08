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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class Change_Invoice_Controller implements Initializable {
    @FXML
    private ComboBox<Integer> idFacturaComboBox;
    @FXML
    private TextField nrFacturaTextField, contractantTextField, denumireMarfaTextField, cantitateTextField, pretTextField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Text invalidIdFactura, invalidNrFactura, invalidCotractant, invalidDate, invalidDenumireMarfa, invalidCantitate, invalidPret;
    @FXML
    private Pane succesPane;

    private String tipMarfa;
    private int activId;
    private float maxVal;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getIdFacturi();
        idFacturaComboBox.getSelectionModel().selectedItemProperty().addListener(((observableValue, integer, t1) -> getDataById()));
    }

    //GETTING DATA FROM DB
    private void getIdFacturi(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getIdQuery = "SELECT _id_factura from facturi";
        try{
            ResultSet getIdResultSet = conn.createStatement().executeQuery(getIdQuery);
            while(getIdResultSet.next()){
                idFacturaComboBox.getItems().add(getIdResultSet.getInt("_id_factura"));
            }
            
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private String tipFactura;
    private void getDataById(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getDataByIdQuery = "SELECT nr_factura, contractant, DATE_FORMAT(data, '%d.%m.%Y') as data, tip_intrare_iesire, " +
                "tip_marfa, denumire_marfa, cantitate, pret, activ_id, produse_id " +
                "from facturi where _id_factura = " + idFacturaComboBox.getValue();
        try{
            ResultSet getDataByIdResultSet = conn.createStatement().executeQuery(getDataByIdQuery);
            if(getDataByIdResultSet.next()){
                nrFacturaTextField.setText(getDataByIdResultSet.getString("nr_factura"));
                contractantTextField.setText(getDataByIdResultSet.getString("contractant"));
                denumireMarfaTextField.setText(getDataByIdResultSet.getString("denumire_marfa"));
                cantitateTextField.setText(getDataByIdResultSet.getString("cantitate"));
                pretTextField.setText(getDataByIdResultSet.getString("pret"));
                datePicker.setValue(LocalDate.parse(getDataByIdResultSet.getString("data"), DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                tipMarfa = getDataByIdResultSet.getString("tip_marfa");
                tipFactura = getDataByIdResultSet.getString("tip_intrare_iesire");
                if(tipMarfa.equals("Produse")){
                    activId = getDataByIdResultSet.getInt("produse_id");
                }else if(tipMarfa.equals("Materiale") || tipMarfa.equals("OMVSD") || tipMarfa.equals("Mijloc Fix")){
                    activId = getDataByIdResultSet.getInt("activ_id");
                }
            }
            if(tipMarfa.equals("Produse")){
                String getMaxValueProduseQuery = "select cantitate_ramasa from produse where _id_produs = " + activId;
                ResultSet getMaxValueProduse = conn.createStatement().executeQuery(getMaxValueProduseQuery);
                if(getMaxValueProduse.next()){
                    maxVal = getMaxValueProduse.getFloat("cantitate_ramasa");
                    maxVal += Float.parseFloat(cantitateTextField.getText());
                }
            } else if(tipFactura.equals("Iesire") && (tipMarfa.equals("Materiale") || tipMarfa.equals("OMVSD") || tipMarfa.equals("Mijloc Fix"))){
                String getMaxValueActiveQuery = "SELECT cantitate_stock from assets where _id_asset = " + activId;
                ResultSet getMaxValActive = conn.createStatement().executeQuery(getMaxValueActiveQuery);
                if(getMaxValActive.next()){
                    maxVal = getMaxValActive.getFloat("cantitate_stock");
                    maxVal += Float.parseFloat(cantitateTextField.getText());
                } else{
                    maxVal = Integer.MAX_VALUE;
                }
            } else{
                maxVal = Integer.MAX_VALUE;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //VALIDATION
    private boolean validation(){
        if(idFacturaComboBox.getSelectionModel().isEmpty()){
            invalidIdFactura.setText("Alegeti id-ul facturii");
            return false;
        } else if(nrFacturaTextField.getText().length() < 5){
            invalidNrFactura.setText("Sunt necesare minim 5 caractere");
            return false;
        } else if(contractantTextField.getText().length() < 4){
            invalidCotractant.setText("Sunt necesare minim 4 caractere");
            return false;
        } else if(datePicker.getEditor().getText().isEmpty()){
            invalidDate.setText("Campul nu poate fi gol");
            return false;
        }else if (denumireMarfaTextField.getText().length()<4){
            invalidDenumireMarfa.setText("Sunt necesare minim 4 caractere");
            return false;
        } else{
            try{
                datePicker.getConverter().fromString(datePicker.getEditor().getText());
            } catch (DateTimeParseException e){
                invalidDate.setText("Format de data invalid");
                return false;
            }
            try{
                if(Float.parseFloat(cantitateTextField.getText()) < 0 || Float.parseFloat(cantitateTextField.getText()) > maxVal){
                    invalidCantitate.setText("Valoare invalida");
                    return false;
                }
            } catch (NumberFormatException e){
                invalidCantitate.setText("Se cere un numar real");
                return false;
            }
            try{
                if(Float.parseFloat(pretTextField.getText()) < 0){
                    invalidPret.setText("Valoare invalida");
                    return false;
                }
            } catch (NumberFormatException e){
                invalidPret.setText("Se cere un numar real");
            }
        }
        return true;
    }

    //UPDATE DATA IN DB
    private void updateDateInDb(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String updateQuery = "UPDATE facturi SET nr_factura = ? , contractant = ?, data = ?, denumire_marfa = ?," +
                "cantitate = ?, pret = ? where _id_factura =" + idFacturaComboBox.getValue();
        try{
            PreparedStatement preparedStatement = conn.prepareStatement(updateQuery);
            preparedStatement.setString(1,nrFacturaTextField.getText());
            preparedStatement.setString(2, contractantTextField.getText());
            preparedStatement.setDate(3, Date.valueOf(datePicker.getValue()));
            preparedStatement.setString(4, denumireMarfaTextField.getText());
            preparedStatement.setFloat(5, Float.parseFloat(cantitateTextField.getText()));
            preparedStatement.setFloat(6, Float.parseFloat(pretTextField.getText()));
            preparedStatement.execute();
            succesUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void updateOtherTables(String query){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        try{
            conn.createStatement().execute(query);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    //maxVal - cantitatea noua

    //BUTTONS
    public void submitButtonOnAction(){
        Stream.of(invalidCotractant, invalidCantitate, invalidPret, invalidDate, invalidDenumireMarfa,invalidIdFactura,
                invalidNrFactura).forEach(text -> text.setText(""));
        if(validation()){
            if(tipMarfa.equals("Produse")){
                String updateProduseQuery = "UPDATE produse SET cantitate_ramasa = " + (maxVal - Float.parseFloat(cantitateTextField.getText())) +
                        "where _id_produs = " + activId;
                updateOtherTables(updateProduseQuery);
            } else if(tipFactura.equals("Iesire") && (tipMarfa.equals("Materiale") || tipMarfa.equals("Mijloc Fix") || tipMarfa.equals("OMVSD"))){
                String updateAssetsQuery = "UPDATE assets SET cantitate_stock = " + (maxVal - Float.parseFloat(cantitateTextField.getText())) +
                        "where _id_asset = " + activId;
                updateOtherTables(updateAssetsQuery);
            } else if(tipFactura.equals("Intrare") && (tipMarfa.equals("Materiale") || tipMarfa.equals("Mijloc Fix") || tipMarfa.equals("OMVSD"))){
                String updateAssetsIntrareQuery = "update assets set cantitate_stock = " + Float.parseFloat(cantitateTextField.getText())+
                        " - (select coalesce(sum(cantitate),0) from facturi where tip_intrare_iesire = 'Iesire' and activ_id = " + activId +
                        " ) - (select coalesce(sum(cantitate),0) from costul_productiei where factura_id = (select _id_factura from facturi " +
                        "where tip_intrare_iesire = 'Intrare' and activ_id = " + activId + ")) where _id_asset = " + activId;
                updateOtherTables(updateAssetsIntrareQuery);
            }
            updateDateInDb();
        }
    }

    public void cancelButtonOnAction(){
        Stage stage = (Stage) idFacturaComboBox.getScene().getWindow();
        stage.close();
    }

    private void succesUpdate(){
        succesPane.setVisible(true);
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
        visiblePause.setOnFinished(event -> succesPane.setVisible(false));
        visiblePause.play();
    }
}
