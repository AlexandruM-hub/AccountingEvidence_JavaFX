package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class AppController implements Initializable {
    // Afisarea din baza de date a tabelului pentru depozite
    @FXML
    private TableView<Depozite> depoziteTableView;

    @FXML
    private TableColumn<Depozite, String> id_depozit;

    @FXML
    private TableColumn<Depozite, String> depozit_localitate;

    ObservableList<Depozite> depoziteObservableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();

        try{
            ResultSet rs_depozite = conn.createStatement().executeQuery("SELECT _id_depozit, localitate FROM depozite");
            while(rs_depozite.next()){
                depoziteObservableList.add(new Depozite(rs_depozite.getString("_id_depozit"),rs_depozite.getString("localitate")));
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        id_depozit.setCellValueFactory(new PropertyValueFactory<>("id_depozit"));
        depozit_localitate.setCellValueFactory(new PropertyValueFactory<>("localitate"));

        depoziteTableView.setItems(depoziteObservableList);
    }




 // Add asset button


    @FXML
    public void addAssetButtonOnAction(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Add_Asset_Stage.fxml"));
        Stage addAssetStage = new Stage(StageStyle.DECORATED);
        addAssetStage.setScene(new Scene(root));
        addAssetStage.setTitle("Add asset");
        addAssetStage.show();
    }



    @FXML
    private Button changeAssetButton;










    @FXML
    private Button exitButton;

    public void exitButtonOnAction(ActionEvent e){
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private Button dashboardButton, transactionButton, incomesButton, costsButton, productsButton, assetsButton, claimsButton, debtsButton;

    @FXML
    private AnchorPane dashboardAnchor, transactionAnchor, incomesAnchor, costsAnchor, productsAnchor, assetsAnchor, claimsAnchor, debtsAnchor;

    public void menuButtonsOnAction(ActionEvent e){

        if(e.getSource() == dashboardButton){
            dashboardAnchor.toFront();
            dashboardButton.setStyle("-fx-text-fill: #A0CF64; -fx-background-color: #5F5F5F");
            Stream.of(transactionButton,incomesButton,costsButton,productsButton,assetsButton,claimsButton,debtsButton).forEach(button -> button.setStyle("menuButton"));
        }
        else if(e.getSource() == transactionButton){
            transactionAnchor.toFront();
            transactionButton.setStyle("-fx-text-fill: #A0CF64; -fx-background-color: #5F5F5F");
            Stream.of(dashboardButton,incomesButton,costsButton,productsButton,assetsButton,claimsButton,debtsButton).forEach(button -> button.setStyle("menuButton"));
        }
        else if(e.getSource() == incomesButton){
            incomesAnchor.toFront();
            incomesButton.setStyle("-fx-text-fill: #A0CF64; -fx-background-color: #5F5F5F");
            Stream.of(dashboardButton,transactionButton,costsButton,productsButton,assetsButton,claimsButton,debtsButton).forEach(button -> button.setStyle("menuButton"));
        }
        else if(e.getSource() == costsButton){
            costsAnchor.toFront();
            costsButton.setStyle("-fx-text-fill: #A0CF64; -fx-background-color: #5F5F5F");
            Stream.of(dashboardButton,incomesButton,transactionButton,productsButton,assetsButton,claimsButton,debtsButton).forEach(button -> button.setStyle("menuButton"));
        }
        else if(e.getSource() == productsButton){
            productsAnchor.toFront();
            productsButton.setStyle("-fx-text-fill: #A0CF64; -fx-background-color: #5F5F5F");
            Stream.of(dashboardButton,incomesButton,costsButton,transactionButton,assetsButton,claimsButton,debtsButton).forEach(button -> button.setStyle("menuButton"));
        }
        else if(e.getSource() == assetsButton){
            assetsAnchor.toFront();
            assetsButton.setStyle("-fx-text-fill: #A0CF64; -fx-background-color: #5F5F5F");
            Stream.of(dashboardButton,incomesButton,costsButton,productsButton,transactionButton,claimsButton,debtsButton).forEach(button -> button.setStyle("menuButton"));
        }
        else if(e.getSource() == claimsButton){
            claimsAnchor.toFront();
            claimsButton.setStyle("-fx-text-fill: #A0CF64; -fx-background-color: #5F5F5F");
            Stream.of(dashboardButton,incomesButton,costsButton,productsButton,assetsButton,transactionButton,debtsButton).forEach(button -> button.setStyle("menuButton"));
        }
        else if(e.getSource() == debtsButton){
            debtsAnchor.toFront();
            debtsButton.setStyle("-fx-text-fill: #A0CF64; -fx-background-color: #5F5F5F");
            Stream.of(dashboardButton,incomesButton,costsButton,productsButton,assetsButton,claimsButton,transactionButton).forEach(button -> button.setStyle("menuButton"));
        }
    }




}
