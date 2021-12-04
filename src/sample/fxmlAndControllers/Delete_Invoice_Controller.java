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

    private boolean checkTipFactura;
    private String facturaTipMarfa;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getIdFromDb();
    }

    private void getIdFromDb(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getIdQuery = "SELECT _id_factura from facturi order by _id_factura";
        try{
            ResultSet getIdResultSet = conn.createStatement().executeQuery(getIdQuery);
            while(getIdResultSet.next()){
                idFacturaComboBox.getItems().add(getIdResultSet.getInt("_id_factura"));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void getInfoAboutInvoice(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getInfoQuery = "SELECT tip_intrare_iesire,tip_marfa from facturi where _id_factura = " +
                idFacturaComboBox.getValue();
        try{
            ResultSet getInfoResultSet = conn.createStatement().executeQuery(getInfoQuery);
            if(getInfoResultSet.next()){
                facturaTipMarfa = getInfoResultSet.getString("tip_marfa");
                checkTipFactura = getInfoResultSet.getString("tip_intrare_iesire").equals("Intrare");
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
            getInfoAboutInvoice();
            confirmationPane.setVisible(true);
            invalidId.setText("");
        } else {
            invalidId.setText("Alegeti ID-ul");
        }
    }

    public void confirmedDeletionButtonOnAction(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String deleteQuery;
        if(checkTipFactura){
            switch (facturaTipMarfa) {
                case "Materiale":
                case "Mijloc FIx":
                case "OMVSD":
                    deleteQuery = "delete from assets where _id_asset = (select activ_id from facturi where _id_factura = " +
                            idFacturaComboBox.getValue()+")";
                    break;
                case "Depozit":
                    deleteQuery = "delete from depozite where _id_depozit = (select depozit_id from facturi where _id_factura = " +
                            idFacturaComboBox.getValue()+")";
                    break;
                case "Teren":
                    deleteQuery = "delete from terenuri_agricole where _id_teren = (select teren_id from facturi where _id_factura = " +
                            idFacturaComboBox.getValue()+")";
                    break;
                default:
                    deleteQuery = "delete from facturi where _id_factura = " + idFacturaComboBox.getValue();
                    break;
            }
        } else{
            deleteQuery = "delete from facturi where _id_factura = " + idFacturaComboBox.getValue();
            if(facturaTipMarfa.equals("Produse")){
                String updateCantitateStock = "UPDATE produse set cantitate_ramasa = ((select cantitate_ramasa from produse " +
                        "where _id_produs = (select produse_id from facturi where _id_factura = " + idFacturaComboBox.getValue() +
                        ")) + (select cantitate from facturi where _id_factura = " + idFacturaComboBox.getValue()+")) " +
                        "where _id_produs = (select produse_id from facturi where _id_factura = " + idFacturaComboBox.getValue() +")";
                try{
                    conn.createStatement().execute(updateCantitateStock);
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
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
