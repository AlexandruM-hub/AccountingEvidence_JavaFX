package sample.fxmlAndControllers;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.DatabaseConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Delete_Asset_Controller implements Initializable {
    @FXML
    private ComboBox<String> tipActivComboBox;
    @FXML
    private ComboBox<Integer> idAssetsComboBox;
    @FXML
    private Button cancelButton;
    @FXML
    private Label invalidDepozit;
    @FXML
    private Pane confirmationPane, succesDeletionPane;

    ObservableList<Integer> choiceObservableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tipActivComboBox.getItems().addAll("Materiale", "OMVSD", "Mijloc Fix", "Teren", "Depozit");
        tipActivComboBox.getSelectionModel().selectedItemProperty().addListener((ObservableValue, s, t1) -> selectIdType());
    }

    private void selectIdType(){
        String tipActivChoice = tipActivComboBox.getValue();
        choiceObservableList.clear();
        idAssetsComboBox.setPromptText("ID " + tipActivChoice);
        switch (tipActivChoice){
            case "Teren":
                String terenGetIdQuery = "SELECT _id_teren FROM terenuri_agricole ORDER BY _id_teren";
                getDataFromDB(terenGetIdQuery, "_id_teren");
                break;
            case "Depozit":
                String depozitGetIdQuery = "SELECT _id_depozit FROM depozite ORDER BY _id_depozit";
                getDataFromDB(depozitGetIdQuery, "_id_depozit");
                break;
            default:
                String getOtherAssetsIDQuery = "SELECT assets._id_asset FROM assets INNER JOIN facturi ON assets._id_asset = facturi.activ_id WHERE facturi.tip_marfa = '"
                        + tipActivChoice + "'AND facturi.tip_intrare_iesire = 'Intrare' ORDER BY assets._id_asset";
                getDataFromDB(getOtherAssetsIDQuery, "assets._id_asset");
                break;
        }
    }

    private void getDataFromDB(String getIdQuery, String columnNameInDB){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        try{
            ResultSet assetsIDResultSet = conn.createStatement().executeQuery(getIdQuery);
            while(assetsIDResultSet.next()){
                choiceObservableList.add(assetsIDResultSet.getInt(columnNameInDB));
            }
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        idAssetsComboBox.getItems().clear();
        idAssetsComboBox.getItems().addAll(choiceObservableList);
    }

    public void cancelButtonOnAction(ActionEvent e){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void denyButtonOnAction(ActionEvent e){
        confirmationPane.setVisible(false);
    }

    public void submitButtonOnAction(ActionEvent e){
        invalidDepozit.setText("");
        if(idAssetsComboBox.getSelectionModel().isEmpty() || tipActivComboBox.getSelectionModel().isEmpty()){
            invalidDepozit.setText("Trebuie sa alegeti tipul activului si ID-ul acestuia");
        }else{
            if(tipActivComboBox.getValue().equals("Depozit")){
                if(depozitValidation()){
                    confirmationPane.setVisible(true);
                }
            }else {
                confirmationPane.setVisible(true);
            }
        }
    }

    public void confirmButtonOnAction(ActionEvent e){
        String tipActivChoice = tipActivComboBox.getValue();
        switch (tipActivChoice){
            case "Teren":
                String terenDeletingQuery = "DELETE FROM terenuri_agricole WHERE _id_teren = '"
                        + idAssetsComboBox.getValue() + "'";
                deletingAssetsFromDB(terenDeletingQuery);
                confirmationPane.setVisible(false);
                successDeletedAssets();
                break;
            case "Depozit":
                String depozitDeletingQuery = "DELETE FROM depozite WHERE _id_depozit = '"
                        + idAssetsComboBox.getValue() +"'";
                deletingAssetsFromDB(depozitDeletingQuery);
                confirmationPane.setVisible(false);
                successDeletedAssets();
                break;
            default:
                String otherAssetsDeletingQuery = "DELETE FROM assets WHERE _id_asset = '"
                        +idAssetsComboBox.getValue() + "'";
                deletingAssetsFromDB(otherAssetsDeletingQuery);
                confirmationPane.setVisible(false);
                successDeletedAssets();
                break;
        }
    }

    private void successDeletedAssets(){
        succesDeletionPane.setVisible(true);
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
        visiblePause.setOnFinished(event -> succesDeletionPane.setVisible(false));
        visiblePause.play();
    }

    private void deletingAssetsFromDB(String query){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        try{
            conn.createStatement().execute(query);
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private boolean depozitValidation(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String checkAsssetsInDepozit = "SELECT SUM(cantitate_stock) AS sumaCantitateStock FROM assets WHERE depozit_id = '" + idAssetsComboBox.getValue() + "'";
        try{
            ResultSet checkForAssetsInDepozitResultSet = conn.createStatement().executeQuery(checkAsssetsInDepozit);
            if(checkForAssetsInDepozitResultSet.next()){
                float checkForAssetsInDepositValue = checkForAssetsInDepozitResultSet.getFloat("sumaCantitateStock");
                if(checkForAssetsInDepositValue > 0 ){
                    invalidDepozit.setText("Acest depozit contine Active!");
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

}
