package sample.fxmlAndControllers;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.DatabaseConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class Change_Asset_Controller implements Initializable {

    @FXML
    private ComboBox<String> tipChangeActivComboBox;
    @FXML
    private ComboBox<Integer> idChangeAssetsComboBox, idDepozitComboBox;
    @FXML
    private Text firstText, secondText, thirdText, fourthText, invalidTipActiv, invalidIdActiv, firstInvalidText;
    @FXML
    private Text  secondInvalidText, thirdInvalidText, fourthInvalidText;
    @FXML
    private TextField firstTextField, secondTextField, thirdTextField, fourthTextField;
    @FXML
    private Button cancelButton;
    @FXML
    private Pane successChangePane;

    ObservableList<Integer> choiceChangeAssetObservableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tipChangeActivComboBox.getItems().addAll("Materiale", "OMVSD", "Mijloc Fix", "Teren", "Depozit");
        tipChangeActivComboBox.getSelectionModel().selectedItemProperty().addListener((ObservableValue, s, t1) -> selectIdType());
        idChangeAssetsComboBox.getSelectionModel().selectedItemProperty().addListener((ObservableValue, s, t1) -> idAssetChoosed());
    }

    // ID CHOOSED
    private void idAssetChoosed(){
        switch (tipChangeActivComboBox.getValue()){
            case "Depozit":
                getDataFromDbForDepozit();
                break;
            case "Teren":
                getDataFromDbForTeren();
                break;
            default:
                getDataFromDbForOtherAssets();
                break;

        }
    }

    private void selectIdType(){
        String tipActivChoice = tipChangeActivComboBox.getValue();
        choiceChangeAssetObservableList.clear();
        idChangeAssetsComboBox.setPromptText("ID " + tipActivChoice);
        switch (tipActivChoice){
            case "Teren":
                String terenGetIdQuery = "SELECT _id_teren FROM terenuri_agricole ORDER BY _id_teren";
                getDataFromDB(terenGetIdQuery, "_id_teren");
                setTerainVisibility();
                break;
            case "Depozit":
                String depozitGetIdQuery = "SELECT _id_depozit FROM depozite ORDER BY _id_depozit";
                getDataFromDB(depozitGetIdQuery, "_id_depozit");
                setDepozitVisibility();
                break;
            default:
                String getOtherAssetsIDQuery = "SELECT assets._id_asset FROM assets INNER JOIN facturi ON assets._id_asset = facturi.activ_id WHERE facturi.tip_marfa = '"
                        + tipActivChoice + "' AND facturi.tip_intrare_iesire = 'Intrare' ORDER BY assets._id_asset";
                getDataFromDB(getOtherAssetsIDQuery, "assets._id_asset");
                otherAssetsVisibility();
                break;
        }
    }

    // ASSETS VISIBILITY
    private void setDepozitVisibility(){
        idDepozitComboBox.setVisible(false);
        firstTextField.setVisible(true);
        secondTextField.setVisible(false);
        thirdTextField.setVisible(false);
        fourthTextField.setVisible(false);
        firstText.setText("Localitate");
        secondText.setText("");
        thirdText.setText("");
        fourthText.setText("");
        firstTextField.setText("");

    }

    private void setTerainVisibility(){
        Stream.of(firstTextField,secondTextField,thirdTextField,fourthTextField).forEach(TextField -> TextField.setText(""));
        Stream.of(firstTextField,secondTextField,thirdTextField,fourthTextField).forEach(TextField -> TextField.setVisible(true));
        firstText.setText("Grup teren");
        secondText.setText("Nr. Cadastral");
        thirdText.setText("Suprafata");
        fourthText.setText("Localitate");
    }

    private void otherAssetsVisibility(){
        firstTextField.setVisible(true);
        secondTextField.setVisible(false);
        thirdTextField.setVisible(false);
        fourthTextField.setVisible(false);
        firstText.setText("Cantitate stock");
        secondText.setText("Depozit ID");
        thirdText.setText("");
        fourthText.setText("");
        idDepozitComboBox.setVisible(true);
        idDepozitComboBox.getItems().clear();
        firstTextField.setText("");
    }

    // GETTING DATA FROM DATABASE
    private void getDataFromDB(String getIdQuery, String columnNameInDB){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        try{
            ResultSet assetsIDResultSet = conn.createStatement().executeQuery(getIdQuery);
            while(assetsIDResultSet.next()){
                choiceChangeAssetObservableList.add(assetsIDResultSet.getInt(columnNameInDB));
            }
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        idChangeAssetsComboBox.getItems().clear();
        idChangeAssetsComboBox.getItems().addAll(choiceChangeAssetObservableList);
    }

    private void getDataFromDbForDepozit(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String depozitQuery = "SELECT localitate FROM depozite WHERE _id_depozit ='" + idChangeAssetsComboBox.getValue() +"'";
        try{
            ResultSet getDepozitInfoResultSet = conn.createStatement().executeQuery(depozitQuery);
            if(getDepozitInfoResultSet.next()){
                firstTextField.setText(getDepozitInfoResultSet.getString("localitate"));
            }
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void getDataFromDbForTeren(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String depozitQuery = "SELECT teren_grup, nr_cadastral, suprafata, localitate FROM terenuri_agricole WHERE _id_teren ='"
                + idChangeAssetsComboBox.getValue() +"'";
        try{
            ResultSet getDepozitInfoResultSet = conn.createStatement().executeQuery(depozitQuery);
            if(getDepozitInfoResultSet.next()){
                firstTextField.setText(getDepozitInfoResultSet.getString("teren_grup"));
                secondTextField.setText(getDepozitInfoResultSet.getString("nr_cadastral"));
                thirdTextField.setText(getDepozitInfoResultSet.getString("suprafata"));
                fourthTextField.setText(getDepozitInfoResultSet.getString("localitate"));
            }
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void getDataFromDbForOtherAssets(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String otherAssetsGetDataDBQuery = "SELECT cantitate_stock, depozit_id FROM assets WHERE _id_asset ='"
                + idChangeAssetsComboBox.getValue() + "'";
        String getDepozitIdQuery = "SELECT _id_depozit FROM depozite";
        try{
            ResultSet getOtherAssetsDbResultSet = conn.createStatement().executeQuery(otherAssetsGetDataDBQuery);
            if(getOtherAssetsDbResultSet.next()){
                firstTextField.setText(getOtherAssetsDbResultSet.getString("cantitate_stock"));
                idDepozitComboBox.setValue(getOtherAssetsDbResultSet.getInt("depozit_id"));
            }
            ResultSet getDepozitIdResultSet = conn.createStatement().executeQuery(getDepozitIdQuery);
            while(getDepozitIdResultSet.next()){
                idDepozitComboBox.getItems().add(getDepozitIdResultSet.getInt("_id_depozit"));
            }
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    //BUTTONS
    public void cancelButtonOnAction(ActionEvent e){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void saveButtonOnAction(ActionEvent e){
        Stream.of(invalidIdActiv,invalidTipActiv,firstInvalidText,secondInvalidText,thirdInvalidText,
                fourthInvalidText).forEach(Text -> Text.setText(""));
        if(tipChangeActivComboBox.getSelectionModel().isEmpty()){
            invalidTipActiv.setText("Acest camp nu poate fi gol");
        }else{
            if(idChangeAssetsComboBox.getSelectionModel().isEmpty()){
                invalidIdActiv.setText("Acest camp nu poate fi gol");
            }else{
                switch (tipChangeActivComboBox.getValue()){
                    case "Depozit":
                        if(depoziteValidare()){
                            depozitSavedChanges();
                            successChangedAssets();
                        }
                        break;
                    case "Teren":
                        if(terenuriValidation()){
                            terenSavedChanges();
                            successChangedAssets();
                        }
                        break;
                    default:
                        if (otherAssetsValidation()){
                            otherAssetsSavedChanges();
                            successChangedAssets();
                        }
                        break;
                }
            }
        }
    }

    //DATA VALIDATION
    private boolean depoziteValidare(){
        if (firstTextField.getText().isBlank()){
            firstInvalidText.setText("Campul nu poate fi gol");
            return false;
        } else if(firstTextField.getText().length() < 4){
            firstInvalidText.setText("Sunt necesare cel putin 4 caractere");
            return false;
        }else if(!firstTextField.getText().matches("[a-zA-Z.]+")){
            firstInvalidText.setText("Sunt necesare litere!");
            return false;
        }
        return true;
    }

    private boolean terenuriValidation(){
        if(firstTextField.getText().isBlank()){
            firstInvalidText.setText("Campul nu poate fi gol");
            return false;
        }else if(secondTextField.getText().isBlank() || secondTextField.getText().length()<10){
            secondInvalidText.setText("Nr cadastral trebuie sa fie cel putin 10 numere");
            return false;
        }else if(thirdTextField.getText().isBlank()){
            thirdInvalidText.setText("Campul nu poate fi gol");
            return false;
        }else if(fourthTextField.getText().isBlank()){
            fourthInvalidText.setText("Campul nu poate fi gol");
            return false;
        }else{
            try{
                Float.parseFloat(secondTextField.getText());
            }catch(NumberFormatException e){
                secondInvalidText.setText("Acest camp nu poate contine litere!");
                return false;
            }
            try {
                Float.parseFloat(thirdTextField.getText());
            }catch (NumberFormatException e){
                thirdInvalidText.setText("Acest camp nu poate contine litere!");
                return false;
            }
        }
        return true;
    }

    private boolean otherAssetsValidation(){
        if(firstTextField.getText().isBlank()){
            firstInvalidText.setText("Campul nu poate fi gol");
            return false;
        }else if(idDepozitComboBox.getSelectionModel().isEmpty()){
            secondInvalidText.setText("Campul nu poate fi gol");
            return false;
        }else{
            try{
                Float.parseFloat(firstTextField.getText());
            }catch (NumberFormatException e){
                firstInvalidText.setText("Campul nu poate contine litere");
                return false;
            }
        }
        return true;
    }

    private void successChangedAssets(){
        successChangePane.setVisible(true);
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
        visiblePause.setOnFinished(event -> successChangePane.setVisible(false));
        visiblePause.play();
    }

    //INTRODUCERE IN BD
    private void depozitSavedChanges(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String depozitChangeQuery = "UPDATE depozite SET localitate  ='" + firstTextField.getText() + "' "
                + "WHERE _id_depozit = '" +idChangeAssetsComboBox.getValue() + "'";
        try{
            conn.createStatement().execute(depozitChangeQuery);
            conn.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void terenSavedChanges(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String terenuriChangeQuery = "UPDATE terenuri_agricole SET teren_grup = '" + firstTextField.getText() +"', "
                + "nr_cadastral = '" + secondTextField.getText() + "', suprafata = '" + thirdTextField.getText()
                + "', localitate = '" + fourthTextField.getText() +"' WHERE _id_teren = '" +idChangeAssetsComboBox.getValue()
                +"'";
        try{
            conn.createStatement().execute(terenuriChangeQuery);
            conn.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void otherAssetsSavedChanges(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String otherAssetsChangeQuery = "UPDATE assets SET cantitate_stock ='" +firstTextField.getText() +"', "
                +"depozit_id = '" + idDepozitComboBox.getValue() + "' WHERE _id_asset = '" + idChangeAssetsComboBox.getValue()
                +"'";
        try{
            conn.createStatement().execute(otherAssetsChangeQuery);
            conn.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    //depozite -> localitatea
    //terenuri -> grup teren, nr cadastral, suprafata, localitate
    //alteActive -> cantitate stock depozitID

}
