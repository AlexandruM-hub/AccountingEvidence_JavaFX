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

public class Delete_Invoice_Controller implements Initializable {
    @FXML
    private ComboBox<Integer> idFacturaComboBox;
    @FXML
    private Text invalidId;
    @FXML
    private Pane succesPane, confirmationPane;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getIdFromDb();
    }

    private void getIdFromDb(){
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

    public void cancelButtonOnAction(){
        Stage stage = (Stage) idFacturaComboBox.getScene().getWindow();
        stage.close();
    }

    public void submitButtonOnAction(){
        if(!idFacturaComboBox.getSelectionModel().isEmpty()){
            confirmationPane.setVisible(true);
            invalidId.setText("");
        } else {
            invalidId.setText("Alegeti ID-ul");
        }
    }

    public void confirmedDeletionButtonOnAction(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String deleteQuery = "DELETE FROM facturi where _id_factura = " + idFacturaComboBox.getValue();
        try{
            conn.createStatement().execute(deleteQuery);
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
