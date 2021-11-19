package sample.fxmlAndControllers;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
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

public class New_Product_Controller implements Initializable {

    @FXML
    private TextField denumireProdusTextField, anulProdusuluiTextField;
    @FXML
    private ComboBox<Integer> grupTerenComboBox;
    @FXML
    private Text invalidDenumireProdus, invalidAnProdus, invalidGrupTeren;
    @FXML
    private Pane succesPane;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getGrupTeren();
    }

    //GET GRUP TEREN FROM DB
    private void getGrupTeren(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getGrupTerenQuery = "SELECT DISTINCT teren_grup FROM terenuri_agricole ORDER BY teren_grup";
        try{
            ResultSet getGrupTerenResultSet = conn.createStatement().executeQuery(getGrupTerenQuery);
            while(getGrupTerenResultSet.next()){
                grupTerenComboBox.getItems().add(getGrupTerenResultSet.getInt("teren_grup"));
            }
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    //INSERT DATA IN DB
    private void insertProdusInDb(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String insertProduseQuery = "INSERT INTO produse(denumire_produs, "
                +"an, teren_grup) VALUES(?,?,?)";
        try{
            PreparedStatement produsePreparedStatement = conn.prepareStatement(insertProduseQuery);
            produsePreparedStatement.setString(1, denumireProdusTextField.getText());
            produsePreparedStatement.setInt(2, Integer.parseInt(anulProdusuluiTextField.getText()));
            produsePreparedStatement.setInt(3, grupTerenComboBox.getValue());
            produsePreparedStatement.execute();
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    //VALIDARE
    private boolean validareDenumire(){
        if(denumireProdusTextField.getText().isBlank()){
            invalidDenumireProdus.setText("Campul nu poate fi gol");
            return false;
        }else if(denumireProdusTextField.getText().length() < 5){
            invalidDenumireProdus.setText("Sunte necesare minim 5 caractere");
            return false;
        }
        return true;
    }

    private boolean validareAnProdus(){
        if(anulProdusuluiTextField.getText().isBlank()){
            invalidAnProdus.setText("Campul nu poate fi gol");
            return false;
        }else if(anulProdusuluiTextField.getText().length() != 4){
            invalidAnProdus.setText("Campul trebuie sa 4 cifre");
            return false;
        }else{
            try{
                int year = Calendar.getInstance().get(Calendar.YEAR);
                int checkNumber = Integer.parseInt(anulProdusuluiTextField.getText());
                if(checkNumber < year-1 || checkNumber > year+1){
                    invalidAnProdus.setText("An disponibil: "+(year-1) +" - " + (year+1));
                    return false;
                }
            }catch (NumberFormatException e){
                invalidAnProdus.setText("Sunt necesare 4 cifre!");
                return false;
            }
        }
        return true;
    }

    private boolean validareGrupTeren(){
        if(grupTerenComboBox.getSelectionModel().isEmpty()){
            invalidGrupTeren.setText("Alegeti grupul terenului");
            return false;
        }
        return true;
    }

    //BUTTONS
    public void cancelButtonOnAction(ActionEvent e){
        Stage stage = (Stage) denumireProdusTextField.getScene().getWindow();
        stage.close();
    }

    public void saveProductOnAction(ActionEvent e){
        Stream.of(invalidAnProdus, invalidDenumireProdus, invalidGrupTeren).forEach(text -> text.setText(""));
        if(validareDenumire() && validareAnProdus() && validareGrupTeren()){
            insertProdusInDb();
            succesIntroducereProdus();
        }
    }

    //SUCCESS INSERTION
    private void succesIntroducereProdus(){
        succesPane.setVisible(true);
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
        visiblePause.setOnFinished(event -> succesPane.setVisible(false));
        visiblePause.play();
    }
}
