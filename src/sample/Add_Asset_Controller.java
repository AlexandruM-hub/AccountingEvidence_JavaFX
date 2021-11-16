package sample;


import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class Add_Asset_Controller implements Initializable {
    @FXML
    private Button cancelAddAssetButton, saveAddAssetButton;
    @FXML
    private ComboBox<String> facturaFiscalaTipComboBox, idProduseComboBox, tipContinutComboBox;
    @FXML
    private ComboBox<String> idTerenComboBox, idDepozitComboBox, idAssetsComboBox;
    @FXML
    private Text produsAlegere, addAssetLocalitateText, addAssetNrCadastralText, addAssetSuprafataText, addAssetGrupTeren;
    @FXML
    private TextField addAssetLocalitateTextField, addAssetNrCadastralTextField, addAssetSuprafataTextField;
    @FXML
    private TextField addAssetNrFacturiiTextField, addAssetContractantTextField, addAssetDenumireTextField, addAssetCantitateTextField, addAssetPretTextField;
    @FXML
    private DatePicker facturaDatePicker;
    @FXML
    private Label invalidNrFacturiiLabel, invalidContractantLabel, invalidDatePickerLabel, invalidDenumireLabel;
    @FXML
    private Label invalidCantitateLabel, invalidPretLabel, invalidTipContinut, invalidIdProdus, invalidLocalitateLabel;
    @FXML
    private Label invalidNrCadastralLabel, invalidSuprafataLabel, invalidTipFactura;
    @FXML
    private Pane succesValidationPane;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        facturaFiscalaTipComboBox.getItems().addAll("Intrare", "Iesire");
        tipContinutComboBox.getItems().addAll( "Materiale", "OMVSD", "Mijloc Fix", "Depozit", "Teren");

        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        try{
            ResultSet idProdusResultSet = conn.createStatement().executeQuery("SELECT _id_produs FROM produse WHERE cantitate_ramasa > 0");
            while(idProdusResultSet.next()){
                idProduseComboBox.getItems().add(idProdusResultSet.getString("_id_produs"));
            }
            ResultSet idTerenResultSet = conn.createStatement().executeQuery("SELECT _id_teren FROM terenuri_agricole");
            while(idTerenResultSet.next()){
                idTerenComboBox.getItems().add(idTerenResultSet.getString("_id_teren"));
            }
            ResultSet idDepozitResultSet = conn.createStatement().executeQuery("SELECT _id_depozit FROM depozite");
            while(idDepozitResultSet.next()){
                idDepozitComboBox.getItems().add(idDepozitResultSet.getString("_id_depozit"));
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    // DATA VALIDATION
    public void saveAddAssetButtonOnAction(ActionEvent e) {
        Stream.of(invalidNrFacturiiLabel, invalidContractantLabel, invalidDatePickerLabel, invalidDenumireLabel,
                invalidCantitateLabel, invalidPretLabel, invalidTipContinut,invalidLocalitateLabel,
                invalidSuprafataLabel, invalidNrCadastralLabel, invalidIdProdus, invalidTipFactura).forEach(label -> label.setText(""));
        if(facturaFiscalaTipComboBox.getSelectionModel().isEmpty()){
            invalidTipFactura.setText("Alegeti o optiune");
        }
        else{
            if(generalValidation()){
                if (facturaFiscalaTipComboBox.getValue().equals("Iesire")) {
                    switch (tipContinutComboBox.getValue()) {
                        case "Produse":
                            if (produsCaseValidation()) {
                                saveAssetInDataBase();
                                succesAddedAssets();
                            }
                            break;
                        case "Teren":
                            if (terenCaseValidation()) {
                                saveAssetInDataBase();
                                succesAddedAssets();
                            }
                            break;
                        case "Depozit":
                            if (depozitCaseValidation()) {
                                saveAssetInDataBase();
                                succesAddedAssets();
                            }
                            break;
                        default:
                            if (OtherAssetsCaseValidation()) {
                                saveAssetInDataBase();
                                succesAddedAssets();
                            }
                            break;
                    }
                } else if (facturaFiscalaTipComboBox.getValue().equals("Intrare")) {
                    if (tipContinutComboBox.getValue().equals("Teren")) {
                        if (terenCaseValidation()) {
                            saveAssetInDataBase();
                            succesAddedAssets();
                        }
                    } else if (tipContinutComboBox.getValue().equals("Depozit")) {
                        if (depozitCaseValidation()) {
                            saveAssetInDataBase();
                            succesAddedAssets();
                        }
                    } else {
                        if(checkIdDepositAssetEntering()){
                            saveAssetInDataBase();
                            succesAddedAssets();
                        }
                    }
                }
            }
        }
    }

    // MANIPULATING DATA IN DB
    public void saveAssetInDataBase(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();

        if(!tipContinutComboBox.getValue().equals("Produse")) {
            String facturaQueryIntrare = "INSERT INTO facturi(nr_factura, tip_intrare_iesire, contractant,"
                    + "data, tip_marfa, denumire_marfa, cantitate, pret) VALUES(?,?,?,?,?,?,?,?)";
            String ContinutTeren;
            if (tipContinutComboBox.getValue().equals("Teren")) {
                ContinutTeren = "1";
            } else {
                ContinutTeren = tipContinutComboBox.getValue();
            }
            try {
                PreparedStatement facturaPreparedStatemen = conn.prepareStatement(facturaQueryIntrare);
                facturaPreparedStatemen.setString(1, addAssetNrFacturiiTextField.getText());
                facturaPreparedStatemen.setString(2, facturaFiscalaTipComboBox.getValue());
                facturaPreparedStatemen.setString(3, addAssetContractantTextField.getText());
                facturaPreparedStatemen.setDate(4, Date.valueOf(facturaDatePicker.getValue()));
                facturaPreparedStatemen.setString(5, ContinutTeren);
                facturaPreparedStatemen.setString(6, addAssetDenumireTextField.getText());
                facturaPreparedStatemen.setFloat(7, Float.parseFloat(addAssetCantitateTextField.getText()));
                facturaPreparedStatemen.setFloat(8, Float.parseFloat(addAssetPretTextField.getText()));
                facturaPreparedStatemen.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            String facturaQueryIntrare = "INSERT INTO facturi(nr_factura, tip_intrare_iesire, contractant,"
                    +"data, tip_marfa, denumire_marfa, cantitate, pret, produs_id) VALUES(?,?,?,?,?,?,?,?,?)";
            try{
                PreparedStatement facturaPreparedStatemen = conn.prepareStatement(facturaQueryIntrare);
                facturaPreparedStatemen.setString(1, addAssetNrFacturiiTextField.getText());
                facturaPreparedStatemen.setString(2, facturaFiscalaTipComboBox.getValue());
                facturaPreparedStatemen.setString(3, addAssetContractantTextField.getText());
                facturaPreparedStatemen.setDate(4, Date.valueOf(facturaDatePicker.getValue()));
                facturaPreparedStatemen.setString(5, tipContinutComboBox.getValue());
                facturaPreparedStatemen.setString(6, addAssetDenumireTextField.getText());
                facturaPreparedStatemen.setFloat(7, Float.parseFloat(addAssetCantitateTextField.getText()));
                facturaPreparedStatemen.setFloat(8, Float.parseFloat(addAssetPretTextField.getText()));
                facturaPreparedStatemen.setString(9, idProduseComboBox.getValue());
                facturaPreparedStatemen.execute();
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
        if(facturaFiscalaTipComboBox.getValue().equals("Intrare")){
            if(tipContinutComboBox.getValue().equals("Depozit")){
                String depozitQuery = "INSERT INTO depozite(localitate, nr_factura) VALUES(?,?)";
                try{
                    PreparedStatement depozitPreparedStatement = conn.prepareStatement(depozitQuery);
                    depozitPreparedStatement.setString(1, addAssetLocalitateTextField.getText());
                    depozitPreparedStatement.setString(2, addAssetNrFacturiiTextField.getText());
                    depozitPreparedStatement.execute();
                }
                catch (SQLException e){
                    e.printStackTrace();
                }
            }else if(tipContinutComboBox.getValue().equals("Teren")){
                String terenQuery = "INSERT INTO terenuri_agricole(teren_grup, nr_cadastral, suprafata, localitate)"
                        +"VALUES(?,?,?,?)";
                try{
                    PreparedStatement terenPreparedStatement = conn.prepareStatement(terenQuery);
                    terenPreparedStatement.setString(1, addAssetCantitateTextField.getText());
                    terenPreparedStatement.setString(2, addAssetNrFacturiiTextField.getText());
                    terenPreparedStatement.setString(3, addAssetSuprafataTextField.getText());
                    terenPreparedStatement.setString(4, addAssetLocalitateTextField.getText());
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }else{
                String otherAssetsQuery = "INSERT INTO assets(cantitate_stock, depozit_id, nr_factura)"
                        +"VALUES (?,?,?)";
                try{
                    PreparedStatement otherAssetsPreparedStatement = conn.prepareStatement(otherAssetsQuery);
                    otherAssetsPreparedStatement.setFloat(1,Float.parseFloat(addAssetCantitateTextField.getText()));
                    otherAssetsPreparedStatement.setInt(2, Integer.parseInt(idDepozitComboBox.getValue()));
                    otherAssetsPreparedStatement.setString(3, addAssetNrFacturiiTextField.getText());
                    otherAssetsPreparedStatement.execute();
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
        } else {
            if ("Teren".equals(tipContinutComboBox.getValue())) {
                String terenSellQuery = "DELETE FROM terenuri_agricole WHERE _id_teren = " + idTerenComboBox.getValue();
                try {
                    conn.createStatement().execute(terenSellQuery);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else if(tipContinutComboBox.getValue().equals("Depozit")){
                String depozitSellQuery = "DELETE FROM depozite WHERE _id_depozit = "+ idDepozitComboBox.getValue();
                try{
                    conn.createStatement().execute(depozitSellQuery);
                }catch(SQLException e){
                    e.printStackTrace();
                }
            } else if(tipContinutComboBox.getValue().equals("Produse")){
                String getQuantityOfProduse = "SELECT cantitate_ramasa FROM produse WHERE _id_produs = " + idProduseComboBox.getValue();
                try{
                    ResultSet quantityOfProdus = conn.createStatement().executeQuery(getQuantityOfProduse);
                    if(quantityOfProdus.next()){
                        float quantityInStock = quantityOfProdus.getFloat("cantitate_ramasa");
                        String updateQuantityProduse = "UPDATE produse SET cantitate_ramasa = '" + (quantityInStock - Float.parseFloat(addAssetCantitateTextField.getText()))
                                +"' WHERE _id_produs ='" + idProduseComboBox.getValue() + "'";
                        conn.createStatement().execute(updateQuantityProduse);
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                    System.out.println("Produse fail");
                }
            }else{
                String getQuantityInStockQuery = "SELECT cantitate_stock FROM assets WHERE _id_asset = " + idAssetsComboBox.getValue();
                try{
                    ResultSet quatityInStockResultSet = conn.createStatement().executeQuery(getQuantityInStockQuery);
                    if(quatityInStockResultSet.next()){
                        float quantityInStock = quatityInStockResultSet.getFloat("cantitate_stock");
                        String otherAssetsSellQuery = "UPDATE assets SET cantitate_stock = '" + (quantityInStock - Float.parseFloat(addAssetCantitateTextField.getText()))
                                +"' WHERE _id_asset = '" + idAssetsComboBox.getValue() + "'";
                        conn.createStatement().execute(otherAssetsSellQuery);

                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void succesAddedAssets(){
        succesValidationPane.setVisible(true);
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
        visiblePause.setOnFinished(
                event -> succesValidationPane.setVisible(false)

        );
        visiblePause.play();
    }

    //ASSETS VALIDATION
    public boolean checkIdDepositAssetEntering(){
        if(idDepozitComboBox.getSelectionModel().isEmpty()){
            invalidIdProdus.setText("Alegeti Depozitul");
            return false;
        }
        return true;
    }

    public boolean OtherAssetsCaseValidation(){
        if(idAssetsComboBox.getSelectionModel().isEmpty()){
            invalidIdProdus.setText("Alegeti Materialul");
            return false;
        }
        if(facturaFiscalaTipComboBox.getValue().equals("Iesire")){
            DatabaseConnection db = new DatabaseConnection();
            Connection conn = db.getConnection();
            String checkQuatityInStock = "SELECT cantitate_stock FROM assets where _id_asset = " + idAssetsComboBox.getValue();
            try{
                ResultSet checkQuantityResultSet = conn.createStatement().executeQuery(checkQuatityInStock);
                if(checkQuantityResultSet.next()){
                    float checkQuantityFloat = checkQuantityResultSet.getFloat("cantitate_stock");
                    if(checkQuantityFloat < Float.parseFloat(addAssetCantitateTextField.getText())){
                        invalidCantitateLabel.setText("Cantitatea in depozit este mai mica");
                        return false;
                    }else{
                        return true;
                    }
                }
            }catch (SQLException e){
                e.printStackTrace();
                System.out.println("Other assets validation");
            }
        }
        return true;
    }

    public boolean generalValidation() {
        if (addAssetContractantTextField.getText().length() < 3) {
            invalidContractantLabel.setText("Sunt necesare cel putin 4 caractere");
            return false;
        } else if (facturaDatePicker.getEditor().getText().isBlank()) {
            invalidDatePickerLabel.setText("Data este obligatorie");
            return false;
        } else if (tipContinutComboBox.getSelectionModel().isEmpty()) {
            invalidTipContinut.setText("Alegeti o optiune");
            return false;
        } else if (addAssetDenumireTextField.getText().length() < 3) {
            invalidDenumireLabel.setText("Sunt necesare cel putin 4 caractere");
            return false;
        } else if (addAssetCantitateTextField.getText().isBlank()) {
            invalidCantitateLabel.setText("Campul nu poate fi gol");
            return false;
        }  else if (addAssetPretTextField.getText().isBlank()) {
            invalidPretLabel.setText("Campul nu poate fi gol");
            return false;
        } else if(!tipContinutComboBox.getValue().equals("Teren")){
            try{
                Float.parseFloat(addAssetCantitateTextField.getText());
            }catch (NumberFormatException e){
                invalidCantitateLabel.setText("Se cere un numar rational");
                return false;
            }
            try{
                Float.parseFloat(addAssetPretTextField.getText());
            }catch (NumberFormatException e){
                invalidPretLabel.setText("Se cere un numar rational");
                return false;
            }
        } else if(Float.parseFloat(addAssetCantitateTextField.getText()) < 0) {
            invalidCantitateLabel.setText("Valoare invalida");
            return false;
        } else if(Float.parseFloat(addAssetPretTextField.getText()) < 0){
            invalidPretLabel.setText("Valoare invalida");
            return false;
        }
        return true;
    }

    public boolean produsCaseValidation() {
        if (addAssetNrFacturiiTextField.getText().length() < 5) {
            invalidNrFacturiiLabel.setText("Sunt necesare cel putin 5 caractere");
            return false;
        } else if (idProduseComboBox.getSelectionModel().isEmpty()) {
            invalidIdProdus.setText("Alegeti produsul");
            return false;
        }else {
            DatabaseConnection db = new DatabaseConnection();
            Connection conn = db.getConnection();
            try{
                String getCantitateProdus = "SELECT cantitate_ramasa FROM produse where _id_produs =" + idProduseComboBox.getValue();
                ResultSet cantitateProdusResultSet = conn.createStatement().executeQuery(getCantitateProdus);
                if(cantitateProdusResultSet.next()){
                    float cantitateProdus = cantitateProdusResultSet.getFloat("cantitate_ramasa");
                    if(cantitateProdus < Float.parseFloat(addAssetCantitateTextField.getText())){
                        invalidCantitateLabel.setText("Cantitatea in depozit este mai mica");
                        return false;
                    }else {
                        return true;
                    }
                }else{
                    return false;
                }
            }
            catch (SQLException e){
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean terenCaseValidation() {
        if(facturaFiscalaTipComboBox.getValue().equals("Intrare")){
            if (addAssetLocalitateTextField.getText().length() < 3) {
                invalidLocalitateLabel.setText("Sunt necesare cel putin 3 caractere");
                return false;
            } else if (addAssetNrCadastralTextField.getText().length() < 10) {
                invalidNrCadastralLabel.setText("Sunt necesare cel putin 10 caractere");
                return false;
            } else if (addAssetSuprafataTextField.getText().isBlank()) {
                invalidSuprafataLabel.setText("Campul nu trebuie sa fie gol");
                return false;
            }else{
                try{
                    Float.parseFloat(addAssetSuprafataTextField.getText());
                }catch (NumberFormatException e){
                    invalidSuprafataLabel.setText("Introduceti un numar!");
                    return false;
                }
                try{
                    Float.parseFloat(addAssetNrCadastralTextField.getText());
                }catch (NumberFormatException e){
                    invalidNrCadastralLabel.setText("Introduceti un numar!");
                    return false;
                }
            }
        }else if(facturaFiscalaTipComboBox.getValue().equals("Iesire")){
            if(idTerenComboBox.getSelectionModel().isEmpty()){
                invalidIdProdus.setText("Alegeti terenul");
                return false;
            }
        }
        return true;
    }

    public boolean depozitCaseValidation() {
        if(facturaFiscalaTipComboBox.getValue().equals("Intrare")){
            if (addAssetLocalitateTextField.getText().length() < 3) {
                invalidLocalitateLabel.setText("Sunt necesare cel putin 3 caractere");
                return false;
            } else if (addAssetNrFacturiiTextField.getText().length() < 5) {
                invalidNrFacturiiLabel.setText("Sunt necesare cel putin 5 caractere");
                return false;
            }
        } else if(facturaFiscalaTipComboBox.getValue().equals("Iesire")){
            if(idDepozitComboBox.getSelectionModel().isEmpty()){
                invalidIdProdus.setText("Alegeti Depozitul");
                return false;
            }
            DatabaseConnection db = new DatabaseConnection();
            Connection conn = db.getConnection();
            String checkAsssetsInDepozit = "SELECT SUM(cantitate_stock) AS sumaCantitateStock FROM assets WHERE depozit_id = '" + idDepozitComboBox.getValue() + "'";
            try{
                ResultSet checkForAssetsInDepozitResultSet = conn.createStatement().executeQuery(checkAsssetsInDepozit);
                if(checkForAssetsInDepozitResultSet.next()){
                    float checkForAssetsInDepositValue = checkForAssetsInDepozitResultSet.getFloat("sumaCantitateStock");
                    if(checkForAssetsInDepositValue > 0 ){
                        invalidIdProdus.setText("Acest depozit contine Active!");
                        return false;
                    }else{
                        return true;
                    }
                }else{
                    return false;
                }
            } catch (SQLException e){
                e.printStackTrace();
                return false;
            }

        }
        return true;
    }

    //Cancel button
    public void cancelAddAssetButtonOnAction(ActionEvent e) {
        Stage stage = (Stage) cancelAddAssetButton.getScene().getWindow();
        stage.close();
    }

    //Vizibilitatea Activelor
    public void produsAlegereVisibility(ActionEvent e) {
        if (facturaFiscalaTipComboBox.getValue().equals("Iesire")) {
            tipContinutComboBox.getItems().add("Produse");
            tipContinutComboBox.setValue("Produse");

        }
        if(facturaFiscalaTipComboBox.getValue().equals("Intrare")){
            tipContinutComboBox.getItems().remove("Produse");
        }
    }

    public void addAssetChooseVisability(ActionEvent e) {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();


        if(facturaFiscalaTipComboBox.getValue().equals("Iesire")){
            String idAssetGeneralQuery = "SELECT assets._id_asset FROM assets INNER JOIN facturi ON assets.nr_factura = facturi.nr_factura "
                    +"WHERE assets.cantitate_stock > 0 and facturi.tip_marfa = '";
            idAssetsComboBox.getItems().clear();
            try{
                ResultSet idAssetsResultSet = conn.createStatement().executeQuery(idAssetGeneralQuery + tipContinutComboBox.getValue()+"'");
                while(idAssetsResultSet.next()){
                    idAssetsComboBox.getItems().add(idAssetsResultSet.getString("assets._id_asset"));
                }
            }catch (SQLException error){
                error.printStackTrace();
                System.out.println("cangeListener error");
            }
            produsAlegere.setVisible(true);
            switch (tipContinutComboBox.getValue()) {
                case "Teren":
                    produsAlegere.setText("ID teren");
                    idTerenComboBox.setVisible(true);
                    Stream.of(idAssetsComboBox, idDepozitComboBox, idAssetsComboBox, idAssetsComboBox,
                            idProduseComboBox).forEach(stringComboBox -> stringComboBox.setVisible(false));
                    break;
                case "Depozit":
                    produsAlegere.setText("ID Depozit");
                    idDepozitComboBox.setVisible(true);
                    Stream.of(idAssetsComboBox, idAssetsComboBox, idAssetsComboBox,
                            idProduseComboBox, idTerenComboBox).forEach(stringComboBox -> stringComboBox.setVisible(false));
                    break;
                case "Mijloc Fix":
                    produsAlegere.setText("ID Mijloc Fix");
                    idAssetsComboBox.setVisible(true);
                    idAssetsComboBox.setPromptText("ID Mijloc Fix");
                    Stream.of(idDepozitComboBox,idProduseComboBox,
                             idTerenComboBox).forEach(stringComboBox -> stringComboBox.setVisible(false));
                    break;
                case "OMVSD":
                    produsAlegere.setText("ID OMVSD");
                    idAssetsComboBox.setVisible(true);
                    idAssetsComboBox.setPromptText("ID OMVSD");
                    Stream.of( idDepozitComboBox, idProduseComboBox,
                             idTerenComboBox).forEach(stringComboBox -> stringComboBox.setVisible(false));
                    break;
                case "Materiale":
                    produsAlegere.setText("ID Material");
                    idAssetsComboBox.setVisible(true);
                    idAssetsComboBox.setPromptText("ID Material");
                    Stream.of(idDepozitComboBox, idProduseComboBox,
                             idTerenComboBox).forEach(stringComboBox -> stringComboBox.setVisible(false));
                    break;
                default:
                    produsAlegere.setText("ID Produs");
                    idProduseComboBox.setVisible(true);
                    Stream.of(idAssetsComboBox, idDepozitComboBox, idAssetsComboBox, idAssetsComboBox,
                            idTerenComboBox).forEach(stringComboBox -> stringComboBox.setVisible(false));
                    break;
            }
        }else if(facturaFiscalaTipComboBox.getValue().equals("Intrare")){
            produsAlegere.setVisible(false);
            Stream.of(idAssetsComboBox,idDepozitComboBox,idAssetsComboBox, idAssetsComboBox,
                    idProduseComboBox,idTerenComboBox).forEach(stringComboBox -> stringComboBox.setVisible(false));
            if(tipContinutComboBox.getValue().equals("Teren") || tipContinutComboBox.getValue().equals("Depozit")){
                addAssetLocalitateText.setVisible(true);
                addAssetLocalitateTextField.setVisible(true);
            }else{
                addAssetLocalitateText.setVisible(false);
                addAssetLocalitateTextField.setVisible(false);
            }
            if (tipContinutComboBox.getValue().equals("Teren")) {
                stage.setHeight(704);
                addAssetNrCadastralTextField.setVisible(true);
                addAssetNrCadastralText.setVisible(true);
                addAssetSuprafataText.setVisible(true);
                addAssetSuprafataTextField.setVisible(true);
                addAssetGrupTeren.setText("Grupul terenului");
                addAssetCantitateTextField.setPromptText("Grupul terenului");
            }else{
                stage.setHeight(604);
                addAssetNrCadastralTextField.setVisible(false);
                addAssetNrCadastralText.setVisible(false);
                addAssetSuprafataText.setVisible(false);
                addAssetSuprafataTextField.setVisible(false);
                addAssetGrupTeren.setText("Cantitate");
                addAssetCantitateTextField.setPromptText("Cantitate");
            }
            if(!tipContinutComboBox.getValue().equals("Depozit") && !tipContinutComboBox.getValue().equals("Teren")){
                produsAlegere.setVisible(true);
                produsAlegere.setText("ID Depozit");
                idDepozitComboBox.setVisible(true);
            }else{
                idDepozitComboBox.setVisible(false);
                produsAlegere.setVisible(false);
            }
        }
    }
}
