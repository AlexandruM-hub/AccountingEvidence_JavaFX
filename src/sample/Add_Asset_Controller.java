package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
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
    private ComboBox<String> idTerenComboBox, idDepozitComboBox, idMijlocFixComboBox, idMaterialComboBox, idOMVSDComboBox;
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
            String idAssetGeneralQuery = "SELECT assets._id_asset FROM assets INNER JOIN facturi ON assets.nr_factura = facturi.nr_factura "
                    +"WHERE assets.cantitate_stock > 0 and facturi.tip_marfa = ";
            ResultSet idMaterialeResultSet = conn.createStatement().executeQuery(idAssetGeneralQuery + "'Materiale'");
            while(idMaterialeResultSet.next()){
                idMaterialComboBox.getItems().add(idMaterialeResultSet.getString("assets._id_asset"));
            }
            ResultSet idMijloaceFixeResultSet = conn.createStatement().executeQuery(idAssetGeneralQuery + "'Mijloc Fix'");
            while(idMijloaceFixeResultSet.next()){
                idMijlocFixComboBox.getItems().add(idMijloaceFixeResultSet.getString("assets._id_asset"));
            }
            ResultSet idOMVSDResultSet = conn.createStatement().executeQuery(idAssetGeneralQuery + "'OMVSD'");
            while(idOMVSDResultSet.next()){
                idOMVSDComboBox.getItems().add(idOMVSDResultSet.getString("assets._id_asset"));
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
                            }
                            break;
                        case "Teren":
                            if (terenCaseValidation()) {
                                System.out.println("Validare cu succes");
                            }
                            break;
                        case "Depozit":
                            if (depozitCaseValidation()) {
                                System.out.println("Validare cu succes");
                            }
                            break;
                        case "OMVSD":
                            if (omvsdCaseValidation()) {
                                System.out.println("Validare succes");
                            }
                            break;
                        case "Mijloc Fix":
                            if (mijlocFixCaseValidation()) {
                                System.out.println("Validare cu succes");
                            }
                            break;
                        case "Materiale":
                            if (materialeCaseValidation()) {
                                System.out.println("Validare cu succes");
                            }
                            break;
                    }
                } else if (facturaFiscalaTipComboBox.getValue().equals("Intrare")) {
                    if (tipContinutComboBox.getValue().equals("Teren")) {
                        if (terenCaseValidation()) {
                            saveAssetInDataBase();
                        }
                    } else if (tipContinutComboBox.getValue().equals("Depozit")) {
                        if (depozitCaseValidation()) {
                            saveAssetInDataBase();
                        }
                    } else {
                        if(checkIdDepositAssetEntering()){
                            saveAssetInDataBase();
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
                    e.printStackTrace(); // check ca in depozit sa nu fie nimic trebuie de adaugat
                }
            }else if (tipContinutComboBox.getValue().equals("OMVSD")){
                String getQuantityInStockQuery = "SELECT cantitate_stock FROM assets WHERE _id_asset = " + idOMVSDComboBox.getValue();
                try{
                    ResultSet quatityInStockResultSet = conn.createStatement().executeQuery(getQuantityInStockQuery);
                    float quantityInStock = quatityInStockResultSet.getFloat("cantitate_stock");
                    String otherAssetsSellQuery = "UPDATE assets SET cantitate_stock = " + (quantityInStock - Float.parseFloat(addAssetCantitateTextField.getText()))
                            +" WHERE _id_asset = " + idOMVSDComboBox.getValue();
                    conn.createStatement().execute(otherAssetsSellQuery);
                }catch (SQLException e){
                    e.printStackTrace();
                }
            } else if(tipContinutComboBox.getValue().equals("Mijloc Fix")){
                String getQuantityInStockQuery = "SELECT cantitate_stock FROM assets WHERE _id_asset = " + idMijlocFixComboBox.getValue();
                try{
                    ResultSet quatityInStockResultSet = conn.createStatement().executeQuery(getQuantityInStockQuery);
                    float quantityInStock = quatityInStockResultSet.getFloat("cantitate_stock");
                    String otherAssetsSellQuery = "UPDATE assets SET cantitate_stock = " + (quantityInStock - Float.parseFloat(addAssetCantitateTextField.getText()))
                            +" WHERE _id_asset = " + idMijlocFixComboBox.getValue();
                    conn.createStatement().execute(otherAssetsSellQuery);
                }catch (SQLException e){
                    e.printStackTrace();
                }
            } else {
                String getQuantityInStockQuery = "SELECT cantitate_stock FROM assets WHERE _id_asset = " + idMaterialComboBox.getValue();
                try{
                    ResultSet quatityInStockResultSet = conn.createStatement().executeQuery(getQuantityInStockQuery);
                    float quantityInStock = quatityInStockResultSet.getFloat("cantitate_stock");
                    String otherAssetsSellQuery = "UPDATE assets SET cantitate_stock = " + (quantityInStock - Float.parseFloat(addAssetCantitateTextField.getText()))
                            +" WHERE _id_asset = " + idMaterialComboBox.getValue();
                    conn.createStatement().execute(otherAssetsSellQuery);
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
        // Iesirea activelor din baza
    }

    public boolean checkIdDepositAssetEntering(){
        if(idDepozitComboBox.getSelectionModel().isEmpty()){
            invalidIdProdus.setText("Alegeti Depozitul");
            return false;
        }
        return true;
    }

    public boolean omvsdCaseValidation(){
        if(idOMVSDComboBox.getSelectionModel().isEmpty()){
            invalidIdProdus.setText("Alegeti OMVSD-ul");
            return false;
        }
        return true;
    }

    public boolean mijlocFixCaseValidation(){
        if(idMijlocFixComboBox.getSelectionModel().isEmpty()){
            invalidIdProdus.setText("Alegeti Mijlocul Fix");
            return false;
        }
        return true;
    }

    public boolean materialeCaseValidation(){
        if(idMaterialComboBox.getSelectionModel().isEmpty()){
            invalidIdProdus.setText("Alegeti Materialul");
            return false;
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
        } else if (addAssetPretTextField.getText().isBlank()) {
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
                float cantitateProdus = cantitateProdusResultSet.getFloat("cantitate_ramasa");
                    if(cantitateProdus < Float.parseFloat(addAssetCantitateTextField.getText())){
                        invalidCantitateLabel.setText("Cantitatea in depozit este mai mica");
                        return false;
                    }else {
                        float cantitateProdusVanduta = Float.parseFloat(addAssetCantitateTextField.getText());
                        String produsScadereCantitateQuery ="UPDATE produse SET cantitate_ramasa =" + (cantitateProdus-cantitateProdusVanduta)
                                +" WHERE _id_produs =" + idProduseComboBox.getValue();
                        conn.createStatement().execute(produsScadereCantitateQuery);
                        return true;
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
        }
        return true;
    }

    public void cancelAddAssetButtonOnAction(ActionEvent e) {
        Stage stage = (Stage) cancelAddAssetButton.getScene().getWindow();
        stage.close();
    }

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
        if(facturaFiscalaTipComboBox.getValue().equals("Iesire")){
            produsAlegere.setVisible(true);
            switch (tipContinutComboBox.getValue()) {
                case "Teren":
                    produsAlegere.setText("ID teren");
                    idTerenComboBox.setVisible(true);
                    Stream.of(idMaterialComboBox, idDepozitComboBox, idMijlocFixComboBox, idOMVSDComboBox,
                            idProduseComboBox).forEach(stringComboBox -> stringComboBox.setVisible(false));
                    break;
                case "Depozit":
                    produsAlegere.setText("ID Depozit");
                    idDepozitComboBox.setVisible(true);
                    Stream.of(idMaterialComboBox, idMijlocFixComboBox, idOMVSDComboBox,
                            idProduseComboBox, idTerenComboBox).forEach(stringComboBox -> stringComboBox.setVisible(false));
                    break;
                case "Mijloc Fix":
                    produsAlegere.setText("ID Mijloc Fix");
                    idMijlocFixComboBox.setVisible(true);
                    Stream.of(idMaterialComboBox, idDepozitComboBox, idOMVSDComboBox,
                            idProduseComboBox, idTerenComboBox).forEach(stringComboBox -> stringComboBox.setVisible(false));
                    break;
                case "OMVSD":
                    produsAlegere.setText("ID OMVSD");
                    idOMVSDComboBox.setVisible(true);
                    Stream.of(idMaterialComboBox, idDepozitComboBox, idMijlocFixComboBox,
                            idProduseComboBox, idTerenComboBox).forEach(stringComboBox -> stringComboBox.setVisible(false));
                    break;
                case "Materiale":
                    produsAlegere.setText("ID Material");
                    idMaterialComboBox.setVisible(true);
                    Stream.of(idDepozitComboBox, idMijlocFixComboBox, idOMVSDComboBox,
                            idProduseComboBox, idTerenComboBox).forEach(stringComboBox -> stringComboBox.setVisible(false));
                    break;
                default:
                    produsAlegere.setText("ID Produs");
                    idProduseComboBox.setVisible(true);
                    Stream.of(idMaterialComboBox, idDepozitComboBox, idMijlocFixComboBox, idOMVSDComboBox,
                            idTerenComboBox).forEach(stringComboBox -> stringComboBox.setVisible(false));
                    break;
            }
        }else if(facturaFiscalaTipComboBox.getValue().equals("Intrare")){
            produsAlegere.setVisible(false);
            Stream.of(idMaterialComboBox,idDepozitComboBox,idMijlocFixComboBox,idOMVSDComboBox,
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
