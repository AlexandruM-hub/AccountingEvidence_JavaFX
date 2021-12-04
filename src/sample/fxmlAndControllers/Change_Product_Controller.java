package sample.fxmlAndControllers;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.DatabaseConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class Change_Product_Controller implements Initializable {
    @FXML
    private ComboBox<Integer> idProdusComboBox, grupTerenComboBox, idDepozitcomboBox;
    @FXML
    private TextField denumireTextField, cantitateRecoltataTextField, cantitateDepozitataTextField, anProducereTextField;
    @FXML
    private Text invalidIdProdus, invalidDenumireProdus, invalidCantitateRecoltata, invalidCantitateDepozitata, invalidAn, invalidIdDepozit;
    @FXML
    private Pane succesPane;
    @FXML
    private Text invalidGrupTeren;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getIdProdus();
        idProdusComboBox.getSelectionModel().selectedItemProperty().addListener((ObservableValue, s, t1) -> {
            grupTerenComboBox.getItems().clear();
            idDepozitcomboBox.getItems().clear();
            getDataAboutProductFromDb();
            getGrupTerenuriAndIdDepoziteFromDb();
        });
    }

    //GET DATA FROM DB
    private void getIdProdus(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getIdQuery = "SELECT _id_produs FROM produse ORDER BY _id_produs";
        try{
            ResultSet getIDResultSet = conn.createStatement().executeQuery(getIdQuery);
            while(getIDResultSet.next()){
                idProdusComboBox.getItems().add(getIDResultSet.getInt("_id_produs"));
            }
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void getDataAboutProductFromDb(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String dataAboutProductQuery = "SELECT denumire_produs, cantitate_recoltata, "
                +"cantitate_ramasa, coalesce(an,0) as an FROM produse WHERE _id_produs = '"
                + idProdusComboBox.getValue() +"'";
        try{
            ResultSet getDataAboutProduct = conn.createStatement().executeQuery(dataAboutProductQuery);
            if(getDataAboutProduct.next()){
                denumireTextField.setText(getDataAboutProduct.getString("denumire_produs"));
                cantitateRecoltataTextField.setText(getDataAboutProduct.getString("cantitate_recoltata"));
                cantitateDepozitataTextField.setText(getDataAboutProduct.getString("cantitate_ramasa"));
                anProducereTextField.setText(getDataAboutProduct.getString("an"));
            }
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void getGrupTerenuriAndIdDepoziteFromDb(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getGrupTerenuriQuery = "SELECT DISTINCT teren_grup FROM terenuri_agricole WHERE teren_grup <> '"
                + grupTerenComboBox.getValue() + "' ORDER BY teren_grup";
        String getDepoziteIdQuery = "SELECT _id_depozit FROM depozite WHERE _id_depozit <> '"
                + idDepozitcomboBox.getValue() +"' ORDER BY _id_depozit";
        try{
            ResultSet grupTerenResultSet = conn.createStatement().executeQuery(getGrupTerenuriQuery);
            while(grupTerenResultSet.next()){
                grupTerenComboBox.getItems().add(grupTerenResultSet.getInt("teren_grup"));
            }
            ResultSet idDepozitResultSet = conn.createStatement().executeQuery(getDepoziteIdQuery);
            while(idDepozitResultSet.next()){
                idDepozitcomboBox.getItems().add(idDepozitResultSet.getInt("_id_depozit"));
            }
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    //VALIDATION
    private boolean validareDenumire(){
        if(denumireTextField.getText().length() < 5){
            invalidDenumireProdus.setText("Sunte necesare minim 5 caractere");
            return false;
        }
        return true;
    }

    private boolean validareIdProdus(){
        if(idProdusComboBox.getSelectionModel().isEmpty()){
            invalidIdProdus.setText("Alegeti produsul");
            return false;
        }
        return true;
    }

    private boolean validareCantitateRecoltata(){
        if(cantitateRecoltataTextField.getText().isBlank()){
            invalidCantitateRecoltata.setText("Campul nu poate fi gol");
            return false;
        }else{
            try{
                Float.parseFloat(cantitateRecoltataTextField.getText());
                if(Float.parseFloat(cantitateRecoltataTextField.getText()) < 0){
                    invalidCantitateRecoltata.setText("Numarul nu poate fi mai mic ca 0");
                    return false;
                }
            }catch (NumberFormatException e){
                invalidCantitateRecoltata.setText("Se cere un numar real");
                return false;
            }
        }
        return true;
    }

    private boolean validareCantitateDepozit(){
        if(cantitateDepozitataTextField.getText().isBlank()){
            invalidCantitateDepozitata.setText("Campul nu poate fi gol");
            return false;
        } else {
            try{
                Float.parseFloat(cantitateDepozitataTextField.getText());
                if(Float.parseFloat(cantitateDepozitataTextField.getText()) < 0){
                    invalidCantitateDepozitata.setText("Numarul trebuie sa fie mai mare ca 0");
                    return false;
                } else if(Float.parseFloat(cantitateDepozitataTextField.getText()) > Float.parseFloat(cantitateRecoltataTextField.getText())){
                    invalidCantitateDepozitata.setText("Valoare invalida");
                    return false;
                }
            } catch (NumberFormatException e){
                invalidCantitateDepozitata.setText("Se cere un numar real");
                return false;
            }
        }
        return true;
    }

    private boolean validareAnProdus(){
        if(anProducereTextField.getText().length() != 4 || anProducereTextField.getText().isBlank()){
            invalidAn.setText("Campul trebuie sa 4 cifre");
            return false;
        }else{
            try{
                int year = Calendar.getInstance().get(Calendar.YEAR);
                int checkNumber = Integer.parseInt(anProducereTextField.getText());
                if(checkNumber < year-1 || checkNumber > year+1){
                    invalidAn.setText("An disponibil: "+(year-1) +" - " + (year+1));
                    return false;
                }
            }catch (NumberFormatException e){
                invalidAn.setText("Sunt necesare 4 cifre!");
                return false;
            }
        }
        return true;
    }

    private boolean checkIdDepozit(){
        if(Float.parseFloat(cantitateDepozitataTextField.getText()) > 0
        && idDepozitcomboBox.getSelectionModel().isEmpty() ){
            invalidIdDepozit.setText("Este necesara alegerea depozitului!");
            return false;
        } else if(grupTerenComboBox.getSelectionModel().isEmpty()){
            invalidGrupTeren.setText("Alegeti terenul!");
            return false;
        }
        return true;
    }

    //CHANGE DATA IN DB
    private boolean changeDataForProductInDB(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String updateProductQuery = "UPDATE produse SET denumire_produs = '" + denumireTextField.getText() + "', "
                + "cantitate_recoltata = '" + cantitateRecoltataTextField.getText() + "', cantitate_ramasa = '"
                + cantitateDepozitataTextField.getText() + "', an = '" + anProducereTextField.getText() + "', "
                + "depozit_id = " + idDepozitcomboBox.getValue() + ", teren_grup = '" + grupTerenComboBox.getValue() + "' WHERE "
                + "_id_produs = '" + idProdusComboBox.getValue() + "'";
        try{
            conn.createStatement().execute(updateProductQuery);
            return true;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    //BUTTONS
    public void cancelButtonOnAction(){
        Stage stage = (Stage) denumireTextField.getScene().getWindow();
        stage.close();
    }

    public void submitButtonOnAction(){
        Stream.of(invalidIdDepozit, invalidAn, invalidCantitateDepozitata, invalidCantitateRecoltata,
                invalidDenumireProdus, invalidIdProdus).forEach(text -> text.setText(""));
        if(validareIdProdus() && validareDenumire()){
            if(validareCantitateRecoltata()){
                if(validareCantitateDepozit() && validareAnProdus()){
                    if(checkIdDepozit()){
                        if(changeDataForProductInDB()){
                            succesChangedProdus();
                        }
                    }
                }
            }
        }
    }

    private void succesChangedProdus(){
        succesPane.setVisible(true);
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
        visiblePause.setOnFinished(event -> succesPane.setVisible(false));
        visiblePause.play();
    }

}
