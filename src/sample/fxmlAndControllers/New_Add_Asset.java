package sample.fxmlAndControllers;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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

public class New_Add_Asset implements Initializable {

    @FXML
    private Button cancelButton;
    @FXML
    private TextField nrFacturiiTextField, contractantTextField, denumireTextField, cantitateTextField, pretTextField;
    @FXML
    private TextField firstTextField, secondTextField, thirdTextField;
    @FXML
    private ComboBox<String> tipFacturaComboBox, tipMarfaComboBox;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Text firstText, secondText, thirdText, cantitateText;
    @FXML
    private ComboBox<Integer> idActiveIesireComboBox;
    @FXML
    private Text invalidNrFacturii, invalidFirstText,invalidSecondText, invalidThirdText;
    @FXML
    private Text invalidContractant, invalidData, invalidTipMarfa, invalidDenumire, invalidCantitate, invalidPret;
    @FXML
    private Pane succesPane;

    private float cantitateActivStock;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tipFacturaComboBox.getItems().addAll("Intrare", "Iesire");
        tipFacturaComboBox.setValue("Intrare");
        tipMarfaComboBox.getItems().addAll("Materiale", "Mijloc Fix", "OMVSD", "Teren", "Depozit");
        tipMarfaComboBox.getSelectionModel().selectedItemProperty().addListener((ObservableValue, s,t1) -> tipContinut());
        tipFacturaComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            standardVisibility();
            if(!tipMarfaComboBox.getSelectionModel().isEmpty()){
                tipContinut();
            }
        });
    }

    private void tipContinut(){
        standardVisibility();
        idActiveIesireComboBox.getItems().clear();
        if(tipFacturaComboBox.getValue().equals("Iesire")){
            firstText.setText("ID " + tipMarfaComboBox.getValue());
            idActiveIesireComboBox.setVisible(true);
            idActiveIesireComboBox.setPromptText("ID "+tipMarfaComboBox.getValue());
            switch (tipMarfaComboBox.getValue()){
                case "Teren":
                    String getIdTerenuriQuery = "SELECT _id_teren FROM terenuri_Agricole ORDER BY _id_teren";
                    getIdIesireActive(getIdTerenuriQuery, "_id_teren");
                    break;
                case "Depozit":
                    String getIdDepozitQuery = "SELECT _id_depozit FROM depozite ORDER BY _id_depozit";
                    getIdIesireActive(getIdDepozitQuery, "_id_depozit");
                    break;
                default:
                    String getIdAlteActiveQuery = "SELECT DISTINCT assets._id_asset FROM assets INNER JOIN facturi ON assets._id_asset = facturi.activ_id WHERE facturi.tip_marfa = '"
                            + tipMarfaComboBox.getValue() + "'AND assets.cantitate_stock > 0 ORDER BY assets._id_asset";
                    getIdIesireActive(getIdAlteActiveQuery, "assets._id_asset");
                    break;
            }
        } else{
            switch (tipMarfaComboBox.getValue()){
                case "Teren":
                    terenIntrareVizibilitate();
                    break;
                case "Depozit":
                    depozitIntrareVisibility();
                    break;
                default:
                    otherAssetsVisibility();
                    break;
            }
        }
    }

    //VIZIBILITATEA
    private void standardVisibility(){

        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.setHeight(604);
        Stream.of(firstText,secondText,thirdText).forEach(text -> text.setText(""));
        Stream.of(firstTextField,secondTextField,thirdTextField).forEach(textField -> textField.setVisible(false));
        Stream.of(firstTextField,secondTextField,thirdTextField,pretTextField,cantitateTextField,
                denumireTextField).forEach(textField -> textField.setText(""));
        idActiveIesireComboBox.setVisible(false);
        cantitateText.setText("Cantitate");
        cantitateTextField.setPromptText("Cantitate");
    }

    private void terenIntrareVizibilitate(){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.setHeight(704);
        cantitateText.setText("Grup teren");
        cantitateTextField.setPromptText("Grup teren");
        firstText.setText("Localitate");
        secondText.setText("Nr Cadastral");
        thirdText.setText("Suprafata");
        Stream.of(firstTextField,secondTextField,thirdTextField).forEach(textField -> textField.setVisible(true));

    }

    private void depozitIntrareVisibility(){
        firstText.setText("Localitate");
        firstTextField.setVisible(true);
    }

    private void otherAssetsVisibility(){
        firstText.setText("ID Depozit");
        idActiveIesireComboBox.setVisible(true);
        idActiveIesireComboBox.setPromptText("ID Depozit");
        String getIdDepozitQuery = "SELECT _id_depozit FROM depozite ORDER BY _id_depozit";
        getIdIesireActive(getIdDepozitQuery, "_id_depozit");
    }

    //DATA VALIDATION
    private boolean generalValidation() {
        if (contractantTextField.getText().length() < 3) {
            invalidContractant.setText("Sunt necesare cel putin 4 caractere");
            return false;
        } else if (datePicker.getEditor().getText().isBlank()) {
            invalidData.setText("Data este obligatorie");
            return false;
        } else if (tipMarfaComboBox.getSelectionModel().isEmpty()) {
            invalidTipMarfa.setText("Alegeti o optiune");
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
        } else if(!tipMarfaComboBox.getValue().equals("Teren")){
            try{
                Float.parseFloat(cantitateTextField.getText());
            }catch (NumberFormatException e){
                invalidCantitate.setText("Se cere un numar rational");
                return false;
            }
            try{
                Float.parseFloat(pretTextField.getText());
            }catch (NumberFormatException e){
                invalidPret.setText("Se cere un numar rational");
                return false;
            }
        } else if(Float.parseFloat(cantitateTextField.getText()) < 0) {
            invalidCantitate.setText("Valoare invalida");
            return false;
        } else if(Float.parseFloat(pretTextField.getText()) < 0){
            invalidPret.setText("Valoare invalida");
            return false;
        }
        return true;
    }

    private boolean otherAssetsValidation(){
        if(nrFacturiiTextField.getText().isBlank()){
            invalidNrFacturii.setText("Campul nu poate fi gol");
            return false;
        }else if(nrFacturiiTextField.getText().length() < 5) {
            invalidNrFacturii.setText("Sunt necesare minim 4 caractere");
            return false;
        }else if (idActiveIesireComboBox.getSelectionModel().isEmpty()) {
            invalidFirstText.setText("Campul nu poate fi gol");
            return false;
        }
        if(tipFacturaComboBox.getValue().equals("Iesire")) {
            DatabaseConnection db = new DatabaseConnection();
            Connection conn = db.getConnection();
            try{
                String getCantitateProdus = "SELECT cantitate_stock FROM assets where _id_asset =" + idActiveIesireComboBox.getValue();
                ResultSet cantitateActivResultSet = conn.createStatement().executeQuery(getCantitateProdus);
                if(cantitateActivResultSet.next()){
                    float cantitateActiv = cantitateActivResultSet.getFloat("cantitate_stock");
                    if(cantitateActiv < Float.parseFloat(cantitateTextField.getText())){
                        invalidCantitate.setText("Cantitatea in depozit este mai mica");
                        return false;
                    }else {
                        cantitateActivStock = cantitateActiv;
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
        return true;
    }

    private boolean depozitCaseValidation() {
        if(tipFacturaComboBox.getValue().equals("Intrare")){
            if (firstTextField.getText().length() < 3) {
                invalidFirstText.setText("Sunt necesare cel putin 3 caractere");
                return false;
            } else if (nrFacturiiTextField.getText().length() < 5) {
                invalidNrFacturii.setText("Sunt necesare cel putin 5 caractere");
                return false;
            }
        } else if(tipFacturaComboBox.getValue().equals("Iesire")){
            if(idActiveIesireComboBox.getSelectionModel().isEmpty()){
                invalidFirstText.setText("Alegeti Depozitul");
                return false;
            }
            DatabaseConnection db = new DatabaseConnection();
            Connection conn = db.getConnection();
            String checkAsssetsInDepozit = "SELECT SUM(cantitate_stock) AS sumaCantitateStock FROM assets WHERE depozit_id = '"
                    + idActiveIesireComboBox.getValue() + "'";
            try{
                ResultSet checkForAssetsInDepozitResultSet = conn.createStatement().executeQuery(checkAsssetsInDepozit);
                if(checkForAssetsInDepozitResultSet.next()){
                    float checkForAssetsInDepositValue = checkForAssetsInDepozitResultSet.getFloat("sumaCantitateStock");
                    if(checkForAssetsInDepositValue > 0 ){
                        invalidFirstText.setText("Acest depozit contine Active!");
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

    private boolean terenCaseValidation() {
        if(tipFacturaComboBox.getValue().equals("Intrare")){
            if (firstTextField.getText().length() < 3) {
                invalidFirstText.setText("Sunt necesare cel putin 3 caractere");
                return false;
            } else if (secondTextField.getText().length() < 10) {
                invalidSecondText.setText("Sunt necesare cel putin 10 caractere");
                return false;
            } else if (thirdTextField.getText().isBlank()) {
                invalidThirdText.setText("Campul nu trebuie sa fie gol");
                return false;
            }else{
                try{
                    Float.parseFloat(thirdTextField.getText());
                }catch (NumberFormatException e){
                    invalidThirdText.setText("Introduceti un numar!");
                    return false;
                }
                try{
                    Float.parseFloat(secondTextField.getText());
                }catch (NumberFormatException e){
                    invalidSecondText.setText("Introduceti un numar!");
                    return false;
                }
            }
        }else if(tipFacturaComboBox.getValue().equals("Iesire")){
            if(idActiveIesireComboBox.getSelectionModel().isEmpty()){
                firstTextField.setText("Alegeti terenul");
                return false;
            }
        }
        return true;
    }

    //GET ID FROM DB
    private void getIdIesireActive(String query, String columnName){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        try{
            ResultSet getIdResultSet = conn.createStatement().executeQuery(query);
            while(getIdResultSet.next()){
                idActiveIesireComboBox.getItems().add(getIdResultSet.getInt(columnName));
            }
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    //SAVE DATA IN DB
    private void saveDataInDB(String Query){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        try{
            conn.createStatement().execute(Query);
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void saveFacturaInDB(String table, String column){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getIdActiv = "SELECT MAX(" + column + ") FROM " + table + "";
        String ContinutTeren;
        if (tipMarfaComboBox.getValue().equals("Teren") || tipMarfaComboBox.getValue().equals("Depozit")) {
            ContinutTeren = "1";
        } else {
            ContinutTeren = cantitateTextField.getText();
        }
        String facturaQueryIntrare;
        if(tipMarfaComboBox.getValue().equals("Depozit")){
            facturaQueryIntrare = "INSERT INTO facturi(nr_factura, tip_intrare_iesire, contractant,"
                    + "data, tip_marfa, denumire_marfa, cantitate, pret, depozit_id) VALUES(?,?,?,?,?,?,?,?,?)";
        } else if(tipMarfaComboBox.getValue().equals("Teren")){
            facturaQueryIntrare = "INSERT INTO facturi(nr_factura, tip_intrare_iesire, contractant,"
                    + "data, tip_marfa, denumire_marfa, cantitate, pret, teren_id) VALUES(?,?,?,?,?,?,?,?,?)";
        } else {
            facturaQueryIntrare = "INSERT INTO facturi(nr_factura, tip_intrare_iesire, contractant,"
                    + "data, tip_marfa, denumire_marfa, cantitate, pret, activ_id) VALUES(?,?,?,?,?,?,?,?,?)";
        }
        try{
            int id;
            if(tipFacturaComboBox.getValue().equals("Intrare")){
                ResultSet idFacturaResultSet = conn.createStatement().executeQuery(getIdActiv);
                if(idFacturaResultSet.next()){
                    id = idFacturaResultSet.getInt("MAX("+column+")");
                }else{
                    id = -1;
                }
            }else{
                id = idActiveIesireComboBox.getValue();
            }
            PreparedStatement facturaPreparedStatemen = conn.prepareStatement(facturaQueryIntrare);
            facturaPreparedStatemen.setString(1, nrFacturiiTextField.getText());
            facturaPreparedStatemen.setString(2, tipFacturaComboBox.getValue());
            facturaPreparedStatemen.setString(3, contractantTextField.getText());
            facturaPreparedStatemen.setDate(4, Date.valueOf(datePicker.getValue()));
            facturaPreparedStatemen.setString(5, tipMarfaComboBox.getValue());
            facturaPreparedStatemen.setString(6, denumireTextField.getText());
            facturaPreparedStatemen.setFloat(7, Float.parseFloat(ContinutTeren));
            facturaPreparedStatemen.setFloat(8, Float.parseFloat(pretTextField.getText()));
            facturaPreparedStatemen.setInt(9,id);
            facturaPreparedStatemen.execute();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    //BUTTONS
    public void cancelButtonOnAction(ActionEvent e){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void submitButtonOnAction(ActionEvent e){
        Stream.of(invalidContractant, invalidCantitate, invalidData, invalidDenumire, invalidPret, invalidFirstText,
                invalidNrFacturii, invalidSecondText, invalidTipMarfa, invalidThirdText).forEach(text -> text.setText(""));
        if(generalValidation()){
            if(tipMarfaComboBox.getValue().equals("Depozit")){
                if(depozitCaseValidation()){
                    if(tipFacturaComboBox.getValue().equals("Intrare")){
                        String addInDBDepozitQuery = "INSERT INTO depozite (localitate, stare) VALUES('"
                                + firstTextField.getText() +"', 'In proprietate')";
                        saveDataInDB(addInDBDepozitQuery);
                    }else {
                        String updateStareDepozit = "UPDATE depozite SET stare = 'vandut' where _id_depozit = '"+
                                idActiveIesireComboBox.getValue()+"'";
                        saveDataInDB(updateStareDepozit);
                    }
                    saveFacturaInDB("depozite", "_id_depozit");
                    successAddAssets();
                }
            }else if(tipMarfaComboBox.getValue().equals("Teren")){
                if(terenCaseValidation()){
                    if(tipFacturaComboBox.getValue().equals("Intrare")){
                        String addTerenInDBQuery = "INSERT INTO terenuri_agricole("
                                + "teren_grup, nr_cadastral, suprafata, localitate, stare) VALUES ('"
                                + cantitateTextField.getText() + "', '" + secondTextField.getText()
                                + "', '" + thirdTextField.getText() + "', '" + firstTextField.getText() +"', 'In proprietate')";
                        saveDataInDB(addTerenInDBQuery);

                    }else{
                        String updateStareTerenuri = "update terenuri_agricole SET stare = 'vandut' where _id_teren = '"+
                                idActiveIesireComboBox.getValue() + "'";
                        saveDataInDB(updateStareTerenuri);
                    }
                    saveFacturaInDB("terenuri_agricole", "_id_teren");
                    successAddAssets();
                }
            }else{
                if(otherAssetsValidation()){
                    if(tipFacturaComboBox.getValue().equals("Intrare")){
                        String addNewAssetInDbQuery = "INSERT INTO assets (cantitate_stock, depozit_id) VALUES('"+
                                cantitateTextField.getText() +"', '" + idActiveIesireComboBox.getValue() + "')";
                        saveDataInDB(addNewAssetInDbQuery);
                    }else{
                        String sellAssetQuery ="UPDATE assets SET cantitate_stock ='" + (cantitateActivStock - Float.parseFloat(cantitateTextField.getText()))
                                + "' WHERE _id_asset = '" +idActiveIesireComboBox.getValue() + "'";
                        saveDataInDB(sellAssetQuery);
                    }
                    saveFacturaInDB("assets","_id_asset");
                    successAddAssets();
                }
            }
        }
    }

    private void successAddAssets(){
        succesPane.setVisible(true);
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
        visiblePause.setOnFinished(event -> succesPane.setVisible(false));
        visiblePause.play();
    }
}
