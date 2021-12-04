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

public class New_Costs_Controller implements Initializable {

    @FXML
    private ComboBox<String> tipComboBox;
    @FXML
    private ComboBox<Integer> produsIdComboBox, elementIdComboBox;
    @FXML
    private Text elementText, valoareText;
    @FXML
    private TextField scopTextField, valoareTextField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Text invalidTipText, invalidScopText, invalidDateText, invalidValoareText, invalidElementIdText;
    @FXML
    private Text invalidProdusIdText;
    @FXML
    private Pane succesPane;

    private float maxValue;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tipComboBox.getItems().addAll("Activ", "Remunerarea muncii", "Amortizare MF", "Alte costuri");
        getIdProduse();
        tipComboBox.getSelectionModel().selectedItemProperty().addListener(((observableValue, s, t1) -> {
            if(tipComboBox.getValue().equals("Activ")){
                valoareText.setText("Cantitate");
                valoareTextField.setPromptText("Cantitate");
            } else{
                valoareText.setText("Valoare");
                valoareTextField.setPromptText("Valoare");
            }
            switch (tipComboBox.getValue()) {
                case "Activ":
                    String getIdActiveQuery = "SELECT DISTINCT assets._id_asset, assets.cantitate_stock from assets inner join facturi on assets._id_asset = " +
                            "facturi.activ_id WHERE assets.cantitate_stock > 0 and (facturi.tip_marfa = 'Materiale' OR " +
                            "facturi.tip_marfa = 'OMVSD')";
                    getIdElemente(getIdActiveQuery, "assets._id_asset");
                    elementText.setText("ID Activ");
                    elementIdComboBox.setPromptText("ID Activ");
                    break;
                case "Remunerarea muncii":
                    String getIdPersonal = "SELECT _id_personal from personal";
                    getIdElemente(getIdPersonal, "_id_personal");
                    maxValue = 20000;
                    elementText.setText("ID Persoana");
                    elementIdComboBox.setPromptText("ID Persoana");
                    break;
                case "Alte costuri":
                    elementText.setText("ID alte costuri");
                    elementIdComboBox.setPromptText("ID alte costuri");
                    String getIdAlteElementeQuery = "SELECT _id_factura FROM facturi WHERE tip_marfa <> 'Produse' and " +
                            "tip_marfa <> 'Mijloc Fix' and tip_marfa <> 'OMVSD' and tip_marfa <> 'Depozit' " +
                            "and tip_marfa <> 'Materiale' and pret > COALESCE((select sum(valoare) from costul_productiei " +
                            "where factura_id = _id_factura), 0);";
                    getIdElemente(getIdAlteElementeQuery, "_id_factura");
                    break;
                default:
                    //MF
                    String getIdMijlocFix ="SELECT DISTINCT assets._id_asset from assets inner join facturi on assets._id_asset = " +
                            "facturi.activ_id WHERE assets.cantitate_stock > 0 and facturi.tip_marfa = 'Mijloc Fix'";
                    getIdElemente(getIdMijlocFix, "assets._id_asset");
                    elementText.setText("ID Mijloc Fix");
                    elementIdComboBox.setPromptText("ID Mijloc Fix");
                    break;
            }
        }));
        elementIdComboBox.getSelectionModel().selectedItemProperty().addListener(((observableValue, integer, t1) -> {
            if(!tipComboBox.getSelectionModel().isEmpty() && !elementIdComboBox.getSelectionModel().isEmpty()){
                switch (tipComboBox.getValue()) {
                    case "Activ":
                        getMaxValueActive();
                        break;
                    case "Amortizare MF":
                        String getMaxValueQuery = "SELECT pret * (select cantitate_stock from assets where _id_asset = " +
                                +elementIdComboBox.getValue() + ") - coalesce((select sum(valoare) from costul_productiei where factura_id = " +
                                "(select factura_id from facturi where tip_intrare_iesire = 'Intrare' and activ_id = "
                                + elementIdComboBox.getValue() + " )), 0) as valoaremax from facturi where activ_id = "
                                + elementIdComboBox.getValue() + " and tip_intrare_iesire = 'Intrare'";
                        //coalesce in caz ca returneaza null
                        getMaxValueFactura(getMaxValueQuery, "valoaremax");
                        break;
                    case "Alte costuri":
                        String getMaxValueAlteCosturiQuery = "SELECT pret - COALESCE((select sum(valoare) from costul_productiei where " +
                                " factura_id ="+ elementIdComboBox.getValue() +"), 0) as valmax from facturi " +
                                "WHERE _id_factura = " + elementIdComboBox.getValue();

                        getMaxValueFactura(getMaxValueAlteCosturiQuery, "valmax");
                        break;
                }
                valoareTextField.setText(String.valueOf(maxValue));
            }
        }));
    }

    //GET ID FROM DB
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

    private void getIdElemente(String query, String column){
        elementIdComboBox.getItems().clear();
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        try{
            ResultSet getId = conn.createStatement().executeQuery(query);
            while(getId.next()){
                elementIdComboBox.getItems().add(getId.getInt(column));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    //GETTING MAX VALUES
    private void getMaxValueActive(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getMaxvalueQuery = "SELECT cantitate_stock from assets where _id_asset = " + elementIdComboBox.getValue();
        try{
            ResultSet getMaxValueResultSet = conn.createStatement().executeQuery(getMaxvalueQuery);
            if(getMaxValueResultSet.next()){
                maxValue = getMaxValueResultSet.getFloat("cantitate_stock");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void getMaxValueFactura(String query, String column){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        try{
            ResultSet getMaxvalueResultSet = conn.createStatement().executeQuery(query);
            if(getMaxvalueResultSet.next()){
                maxValue = getMaxvalueResultSet.getFloat(column);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    // active -> cantitate_stock
    // remunerarea muncii --
    // alte costuri -> valfacturii - alte costuri pe factura data
    // amortizarea -> valoarea MF - alte costuri pe MF

    //VALIDATION
    private boolean generalValidation(){
        if(tipComboBox.getSelectionModel().isEmpty()){
            invalidTipText.setText("Alegeti aferenta costului");
            return false;
        } else if(scopTextField.getText().length() < 4){
            invalidScopText.setText("Sunt necesare minim 4 caractere");
            return false;
        } else if(datePicker.getEditor().getText().isBlank()){
            invalidDateText.setText("Alegeti data");
            return false;
        } else if(elementIdComboBox.getSelectionModel().isEmpty()){
            invalidElementIdText.setText("Acest camp nu poate fi gol");
            return false;
        } else if(produsIdComboBox.getSelectionModel().isEmpty()){
            invalidProdusIdText.setText("Acest camp nu poate fi gol");
            return false;
        } else if(valoareTextField.getText().isBlank()){
            invalidValoareText.setText("Campul nu poate fi gol");
            return false;
        } else {
            try{
                Float.parseFloat(valoareTextField.getText());
                if(Float.parseFloat(valoareTextField.getText()) > maxValue || Float.parseFloat(valoareTextField.getText()) < 0 ){
                    invalidValoareText.setText("Valoare nevalabila");
                    return false;
                }
            } catch (NumberFormatException e){
                invalidValoareText.setText("Introduceti un numar real");
                return false;
            }
        }
        return true;
    }

    private void insertionActiveCostsInDb(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String activeCostsQuery = "INSERT INTO costul_productiei(tip, scop, data_cost" +
                ", cantitate, valoare, factura_id, produs_id) VALUES(?,?,?,?,?,(select _id_factura from facturi where activ_id = "+
                elementIdComboBox.getValue()+" and tip_intrare_iesire = 'Intrare'),?)";
        String getPriceActive = "select pret from facturi where tip_intrare_iesire = 'Intrare' and (tip_marfa = " +
                "'Materiale' OR tip_marfa = 'OMVSD') and " +
                "activ_id = "+ elementIdComboBox.getValue();
        String updateStock = "UPDATE assets SET cantitate_stock = " + (maxValue - Float.parseFloat(valoareTextField.getText())) +
                " where _id_asset = " + elementIdComboBox.getValue();
        try{
            float activPret;
            ResultSet getPriceActiveResultSet = conn.createStatement().executeQuery(getPriceActive);
            if(getPriceActiveResultSet.next()){
                activPret = getPriceActiveResultSet.getFloat("pret");
                activPret *= Float.parseFloat(valoareTextField.getText());
            } else{
                activPret = 0;
            }
            PreparedStatement activePrepareStatemen = conn.prepareStatement(activeCostsQuery);
            activePrepareStatemen.setString(1,tipComboBox.getValue());
            activePrepareStatemen.setString(2,scopTextField.getText());
            activePrepareStatemen.setDate(3, Date.valueOf(datePicker.getValue()));
            activePrepareStatemen.setFloat(4,Float.parseFloat(valoareTextField.getText()));
            activePrepareStatemen.setFloat(5,activPret);
            activePrepareStatemen.setInt(6,produsIdComboBox.getValue());
            activePrepareStatemen.execute();
            conn.createStatement().execute(updateStock);
            succesInsertion();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void insertCostsOtherElementsInDb(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String insertQuery;
        if(tipComboBox.getValue().equals("Remunerarea muncii")){
            insertQuery = "insert into costul_productiei(tip,scop,data_cost, valoare, persoana_id, produs_id) values(?,?,?,?,?,?)";
        }else{
            insertQuery = "INSERT INTO costul_productiei(tip, scop, data_cost" +
                    ", valoare, factura_id, produs_id) VALUES(?,?,?,?,?,?)";
        }
        try{
            PreparedStatement personalPreparedStatement = conn.prepareStatement(insertQuery);
            personalPreparedStatement.setString(1,tipComboBox.getValue());
            personalPreparedStatement.setString(2,scopTextField.getText());
            personalPreparedStatement.setDate(3, Date.valueOf(datePicker.getValue()));
            personalPreparedStatement.setFloat(4,Float.parseFloat(valoareTextField.getText()));
            personalPreparedStatement.setInt(5, elementIdComboBox.getValue());
            personalPreparedStatement.setInt(6,produsIdComboBox.getValue());
            personalPreparedStatement.execute();
            succesInsertion();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    
    private void insertCostsAmortizareInDb(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String insertAmortizareQuery = "INSERT INTO costul_productiei(tip, scop, data_cost" +
                ", valoare, factura_id, produs_id) VALUES(?,?,?,?,(select _id_factura from facturi  where activ_id = "+elementIdComboBox.getValue()+
                "),?)";
        try{
            PreparedStatement preparedStatement = conn.prepareStatement(insertAmortizareQuery);
            preparedStatement.setString(1,tipComboBox.getValue());
            preparedStatement.setString(2,scopTextField.getText());
            preparedStatement.setDate(3, Date.valueOf(datePicker.getValue()));
            preparedStatement.setFloat(4,Float.parseFloat(valoareTextField.getText()));
            preparedStatement.setInt(5,produsIdComboBox.getValue());
            preparedStatement.execute();
            succesInsertion();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    
    //BUTTONS
    public void saveButtonOnAction(){
        Stream.of(invalidDateText, invalidValoareText, invalidElementIdText, invalidScopText, invalidTipText,
                invalidProdusIdText).forEach(text -> text.setText(""));
        if(generalValidation()){
            if(tipComboBox.getValue().equals("Activ")){
                insertionActiveCostsInDb();
            } else if(tipComboBox.getValue().equals("Amortizare MF")){
                insertCostsAmortizareInDb();
            } else{
                insertCostsOtherElementsInDb();
            }
        }
    }

    public void cancelButtonOnAction(){
        Stage stage = (Stage) elementIdComboBox.getScene().getWindow();
        stage.close();
    }

    private void succesInsertion(){
        succesPane.setVisible(true);
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
        visiblePause.setOnFinished(event -> succesPane.setVisible(false));
        visiblePause.play();
    }

}
