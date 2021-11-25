package sample.fxmlAndControllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
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

public class Delete_Debt_Claim_Controller implements Initializable {
    @FXML
    private ComboBox<Integer> idCreantaDatorieComboBox;
    @FXML
    private Pane succesPane, confirmationPane;
    @FXML
    private Text invalidId;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getIdCreantaDatorieComboBox();
    }

    private void getIdCreantaDatorieComboBox(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getIdCreantaDatorieQuery = "SELECT _id_datorie_creanta FROM datorii_creante";
        try{
            ResultSet getIdResultSet = conn.createStatement().executeQuery(getIdCreantaDatorieQuery);
            while(getIdResultSet.next()){
                idCreantaDatorieComboBox.getItems().add(getIdResultSet.getInt("_id_datorie_creanta"));
            }
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void submitButtonOnAction(){
        if(!idCreantaDatorieComboBox.getSelectionModel().isEmpty()){
            confirmationPane.setVisible(true);
            invalidId.setText("");
        } else{
            invalidId.setText("Alegeti Creanta / Datoria");
        }
    }

    public void cancelButtonOnAction(){
        Stage stage = (Stage) idCreantaDatorieComboBox.getScene().getWindow();
        stage.close();
    }

    public void confirmedDeletionButtonOnAction(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String deleteDebtClaimQuery = "DELETE FROM datorii_creante WHERE _id_datorie_creanta = "
                +idCreantaDatorieComboBox.getValue();
        try{
            conn.createStatement().execute(deleteDebtClaimQuery);
            succesDeletion();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void nuButtonOnAction(){
        confirmationPane.setVisible(false);
    }

    private void succesDeletion(){
        confirmationPane.setVisible(false);
        succesPane.setVisible(true);
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
        visiblePause.setOnFinished(event -> succesPane.setVisible(false));
        visiblePause.play();
    }
}
