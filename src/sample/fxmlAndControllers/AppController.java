package sample.fxmlAndControllers;

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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.Assets;
import sample.DatabaseConnection;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class AppController implements Initializable {
    @FXML
    private Button exitButton, changeAssetButton;
    @FXML
    private Button dashboardButton, transactionButton, incomesButton, costsButton, productsButton, assetsButton, claimsButton, debtsButton;
    @FXML
    private AnchorPane dashboardAnchor, transactionAnchor, incomesAnchor, costsAnchor, productsAnchor, assetsAnchor, claimsAnchor, debtsAnchor;
    @FXML
    private TableView<Assets> depoziteTableView, terenuriTableView, mijloaceFixeTableView, otherAssetsTableView;
    @FXML
    private TableColumn<Assets, String>  depozit_localitate, terenNrCadastral, terenLocalitate ,denumireMijlocFix;
    @FXML
    private TableColumn<Assets,Integer> id_facturaMF, id_depozit, terenID, terenGrup, idAsset, idDepozitAlteActive;
    @FXML
    private TableColumn<Assets, String> nrFacturaMijlocFix, contractantMijlocFix, nrFacturaAlteActive, contractantAlteActive, tipAlteActive, denumireAlteActive;
    @FXML
    private TableColumn<Assets,Float> cantitateStocMijlocFix, valoareActivMijlocFix, terenSuprafata, cantitateAlteActive, pretAlteActive, valoareAlteActive;
    @FXML
    private TableColumn<Assets, Date> dataIntrareMijlocFix, dataIntrareAlteActive;
    @FXML
    private TextField currentAssetsSearchTextField, mijloaceFixeSearchTextField, terenuriSearchTextField, depoziteSearchTextField;

    ObservableList<Assets> depoziteObservableList = FXCollections.observableArrayList();
    ObservableList<Assets> terenuriObservableList = FXCollections.observableArrayList();
    ObservableList<Assets> mijloaceFixeObservableList = FXCollections.observableArrayList();
    ObservableList<Assets> otherAssetsObservableList = FXCollections.observableArrayList();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentAssetsSearchTextField.textProperty().addListener((observableValue, s, t1) -> searchCurrentAssetsInDB());
        mijloaceFixeSearchTextField.textProperty().addListener((observableValue, s, t1) -> searchMijloaceFixeInDB());
        terenuriSearchTextField.textProperty().addListener((observableValue, s, t1) -> searchPamanturiInDB());
        depoziteSearchTextField.textProperty().addListener((observableValue, s, t1) -> searchDepoziteInDB());
    }

    //SEARCH DATA IN ASSETS
    private void searchCurrentAssetsInDB(){
        otherAssetsObservableList.clear();
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String checkForCurrentAssetsQuery = "SELECT assets._id_asset,facturi.nr_factura, facturi.contractant, facturi.data, facturi.tip_marfa, "
                +"facturi.denumire_marfa, facturi.cantitate, facturi.pret, assets.cantitate_stock * facturi.pret AS valoareActive, "
                +"assets.depozit_id FROM assets INNER JOIN facturi ON assets._id_asset = facturi.activ_id WHERE facturi.tip_marfa <> 'Mijloc Fix' AND "
                +"facturi.tip_intrare_iesire = 'Intrare' AND facturi.tip_marfa <> 'Depozit'";
        String currentAssetsSearchQuery = " AND (facturi.tip_marfa LIKE '%" + currentAssetsSearchTextField.getText() +"%' OR assets._id_asset LIKE '%"
                + currentAssetsSearchTextField.getText() +"%' OR facturi.nr_factura LIKE '%" + currentAssetsSearchTextField.getText() + "%' OR "
                +"facturi.contractant LIKE '%" + currentAssetsSearchTextField.getText() +"%' OR facturi.data LIKE '%" + currentAssetsSearchTextField.getText() + "%' OR "
                + "facturi.denumire_marfa LIKE '%" + currentAssetsSearchTextField.getText() + "%' OR facturi.cantitate LIKE '%" +currentAssetsSearchTextField.getText()
                +"%' OR facturi.pret LIKE '%" + currentAssetsSearchTextField.getText() + "%' OR assets.cantitate_stock * facturi.pret LIKE '%"
                + currentAssetsSearchTextField.getText() + "%' OR assets.depozit_id LIKE '%" +currentAssetsSearchTextField.getText() +"%')";
        try{
            ResultSet otherAssetsResultSet = conn.createStatement().executeQuery(checkForCurrentAssetsQuery + currentAssetsSearchQuery);
            while(otherAssetsResultSet.next()){
                otherAssetsObservableList.add(new Assets(otherAssetsResultSet.getInt("_id_asset"), otherAssetsResultSet.getString("nr_factura"),
                        otherAssetsResultSet.getString("contractant"), otherAssetsResultSet.getDate("data"), otherAssetsResultSet.getString("tip_marfa"),
                        otherAssetsResultSet.getString("denumire_marfa"), otherAssetsResultSet.getFloat("cantitate"), otherAssetsResultSet.getFloat("pret"),
                        otherAssetsResultSet.getFloat("valoareActive"), otherAssetsResultSet.getInt("depozit_id")));
            }
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        loadOtherAssetsInTable();
    }

    private void searchMijloaceFixeInDB(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        mijloaceFixeObservableList.clear();
        String mijloaceFixeQuery = "SELECT facturi._id_factura, facturi.nr_factura, facturi.contractant, "
                + "facturi.data, facturi.denumire_marfa AS denumire, assets.cantitate_stock, assets.cantitate_stock * facturi.pret AS valoareActiv FROM "
                +"facturi INNER JOIN assets ON facturi.activ_id = assets._id_asset WHERE facturi.tip_marfa = 'Mijloc Fix' AND facturi.tip_intrare_iesire = 'Intrare'";
        String mijloaceFixeSearchQuery = " AND (facturi._id_factura LIKE '%" + mijloaceFixeSearchTextField.getText() +"%' OR facturi.nr_factura LIKE '%"
                + mijloaceFixeSearchTextField.getText() + "%' OR facturi.contractant LIKE '%" + mijloaceFixeSearchTextField.getText() + "%' OR "
                +"facturi.data LIKE '%" + mijloaceFixeSearchTextField.getText() +"%' OR facturi.denumire_marfa LIKE '%" + mijloaceFixeSearchTextField.getText() +"%' OR "
                +"assets.cantitate_stock LIKE '%" + mijloaceFixeSearchTextField.getText() + "%' OR assets.cantitate_stock * facturi.pret LIKE '%"
                + mijloaceFixeSearchTextField.getText() + "%')";
        try{
            ResultSet mijloaceFixeResultSet = conn.createStatement().executeQuery(mijloaceFixeQuery + mijloaceFixeSearchQuery);
            while (mijloaceFixeResultSet.next()) {
                mijloaceFixeObservableList.add(new Assets(mijloaceFixeResultSet.getInt("_id_factura"), mijloaceFixeResultSet.getString("nr_factura"),
                        mijloaceFixeResultSet.getString("contractant"), mijloaceFixeResultSet.getDate("data"), mijloaceFixeResultSet.getString("denumire"),
                        mijloaceFixeResultSet.getFloat("cantitate_stock"), mijloaceFixeResultSet.getFloat("valoareActiv")));
            }
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        loadMijloaceFixeInTable();
    }

    private void searchPamanturiInDB(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        terenuriObservableList.clear();
        String terenuriQuery = "SELECT * FROM terenuri_agricole";
        String terenuriSearchQuery = " WHERE _id_teren LIKE '%" + terenuriSearchTextField.getText() + "%' OR teren_grup LIKE '%"
                + terenuriSearchTextField.getText() +"%' OR nr_cadastral LIKE '%" + terenuriSearchTextField.getText() + "%'"
                +" OR suprafata LIKE '%" + terenuriSearchTextField.getText() +"%' OR localitate LIKE '%"
                + terenuriSearchTextField.getText() +"%'";
        try{
            ResultSet rs_terenuri = conn.createStatement().executeQuery(terenuriQuery +terenuriSearchQuery);
            while(rs_terenuri.next()){
                terenuriObservableList.add(new Assets(rs_terenuri.getInt("_id_teren"), rs_terenuri.getInt("teren_grup"),
                        rs_terenuri.getString("nr_cadastral"), rs_terenuri.getFloat("suprafata"), rs_terenuri.getString("localitate")));
            }
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        loadTerenuriInTable();
    }

    private void searchDepoziteInDB(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        depoziteObservableList.clear();
        String getDepoziteQuery = "SELECT _id_depozit, localitate FROM depozite";
        String depoziteSearchQuery = " WHERE _id_depozit LIKE '%" + depoziteSearchTextField.getText() + "%' OR localitate LIKE '%"
                + depoziteSearchTextField.getText() + "%'";
        try{
            ResultSet rs_depozite = conn.createStatement().executeQuery(getDepoziteQuery + depoziteSearchQuery);
            while(rs_depozite.next()){
                depoziteObservableList.add(new Assets(rs_depozite.getInt("_id_depozit"),rs_depozite.getString("localitate")));
            }
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        loadDepoziteInTable();
    }

    //LOAD DATA IN TABLES ASSETS
    private void loadTerenuriInTable(){
        terenID.setCellValueFactory(new PropertyValueFactory<>("idTeren"));
        terenNrCadastral.setCellValueFactory(new PropertyValueFactory<>("nrCadastral"));
        terenSuprafata.setCellValueFactory(new PropertyValueFactory<>("suprafata"));
        terenLocalitate.setCellValueFactory(new PropertyValueFactory<>("localitate"));
        terenGrup.setCellValueFactory(new PropertyValueFactory<>("grupTeren"));
        terenuriTableView.setItems(terenuriObservableList);
    }

    private void loadMijloaceFixeInTable(){
        id_facturaMF.setCellValueFactory(new PropertyValueFactory<>("idFactura"));
        nrFacturaMijlocFix.setCellValueFactory(new PropertyValueFactory<>("nrFactura"));
        contractantMijlocFix.setCellValueFactory(new PropertyValueFactory<>("contractant"));
        dataIntrareMijlocFix.setCellValueFactory(new PropertyValueFactory<>("data"));
        denumireMijlocFix.setCellValueFactory(new PropertyValueFactory<>("denumireMijlocFix"));
        cantitateStocMijlocFix.setCellValueFactory(new PropertyValueFactory<>("cantitateStock"));
        valoareActivMijlocFix.setCellValueFactory(new PropertyValueFactory<>("valoareaActiv"));
        mijloaceFixeTableView.setItems(mijloaceFixeObservableList);
    }

    private void loadDepoziteInTable(){
        id_depozit.setCellValueFactory(new PropertyValueFactory<>("id_depozit"));
        depozit_localitate.setCellValueFactory(new PropertyValueFactory<>("localitate"));
        depoziteTableView.setItems(depoziteObservableList);
    }

    private void loadOtherAssetsInTable(){
        idAsset.setCellValueFactory(new PropertyValueFactory<>("idAsset"));
        nrFacturaAlteActive.setCellValueFactory(new PropertyValueFactory<>("nrFactura"));
        contractantAlteActive.setCellValueFactory(new PropertyValueFactory<>("contractant"));
        dataIntrareAlteActive.setCellValueFactory(new PropertyValueFactory<>("data"));
        tipAlteActive.setCellValueFactory(new PropertyValueFactory<>("tipMarfa"));
        denumireAlteActive.setCellValueFactory(new PropertyValueFactory<>("denumireActiv"));
        cantitateAlteActive.setCellValueFactory(new PropertyValueFactory<>("cantitateStock"));
        pretAlteActive.setCellValueFactory(new PropertyValueFactory<>("pretActiv"));
        valoareAlteActive.setCellValueFactory(new PropertyValueFactory<>("valoareaActiv"));
        idDepozitAlteActive.setCellValueFactory(new PropertyValueFactory<>("id_depozit"));
        otherAssetsTableView.setItems(otherAssetsObservableList);

    }

    private void loadAllAssetsInAssetTableViews(){
        searchCurrentAssetsInDB();
        searchMijloaceFixeInDB();
        searchPamanturiInDB();
        searchDepoziteInDB();
    }

    //ASSETS BUTTONS
    public void addAssetButtonOnAction(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("New_Asset_FXML.fxml"));
        Stage addAssetStage = new Stage(StageStyle.DECORATED);
        addAssetStage.setScene(new Scene(root));
        addAssetStage.setTitle("Add asset");
        addAssetStage.show();
    }

    public void changeAssetButtonOnAction(ActionEvent e) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("Change_Asset_Stage.fxml"));
        Stage DeleteAssetStage = new Stage(StageStyle.DECORATED);
        DeleteAssetStage.setScene(new Scene(root));
        DeleteAssetStage.setTitle("Change Asset");
        DeleteAssetStage.show();
    }

    public void deleteAssetButtonOnAction(ActionEvent e) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("Delete_Asset_Stage.fxml"));
        Stage DeleteAssetStage = new Stage(StageStyle.DECORATED);
        DeleteAssetStage.setScene(new Scene(root));
        DeleteAssetStage.setTitle("Delete Asset");
        DeleteAssetStage.show();
    }

    public void exitButtonOnAction(ActionEvent e){
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    public void menuButtonsOnAction(ActionEvent e){
        Stream.of(dashboardButton,transactionButton,incomesButton,costsButton,productsButton,assetsButton,claimsButton,debtsButton).forEach(Button -> Button.setStyle("menuButton"));
        if(e.getSource() == dashboardButton){
            dashboardAnchor.toFront();
            dashboardButton.setStyle("-fx-text-fill: #cfcb5b; -fx-background-color: rgba(89,50,15,0.2)");

        }
        else if(e.getSource() == transactionButton){
            transactionAnchor.toFront();
            transactionButton.setStyle("-fx-text-fill: #cfcb5b; -fx-background-color: rgba(89,50,15,0.2)");
        }
        else if(e.getSource() == incomesButton){
            incomesAnchor.toFront();
            incomesButton.getStyleClass().clear();
            incomesButton.setStyle("-fx-text-fill: #cfcb5b; -fx-background-color: rgba(89,50,15,0.2)");
        }
        else if(e.getSource() == costsButton){
            costsAnchor.toFront();
            costsButton.setStyle("-fx-text-fill: #cfcb5b; -fx-background-color: rgba(89,50,15,0.2)");
        }
        else if(e.getSource() == productsButton){
            productsAnchor.toFront();
            productsButton.setStyle("-fx-text-fill: #cfcb5b; -fx-background-color: rgba(89,50,15,0.2)");
        }
        else if(e.getSource() == assetsButton){
            assetsAnchor.toFront();

            assetsButton.setStyle("-fx-text-fill: #cfcb5b; -fx-background-color: rgba(89,50,15,0.2)");
            //loadAllAssetsInAssetTableViews();
            loadAllAssetsInAssetTableViews();

        }
        else if(e.getSource() == claimsButton){
            claimsAnchor.toFront();
            claimsButton.setStyle("-fx-text-fill: #cfcb5b; -fx-background-color: rgba(89,50,15,0.2)");
        }
        else if(e.getSource() == debtsButton){
            debtsAnchor.toFront();
            debtsButton.setStyle("-fx-text-fill: #cfcb5b; -fx-background-color: rgba(89,50,15,0.2)");
        }
        
    }

    //PRODUCTS BUTTONS
    public void addProdusButtonOnAction(ActionEvent e) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("New_Product.fxml"));
        Stage addAssetStage = new Stage(StageStyle.DECORATED);
        addAssetStage.setScene(new Scene(root));
        addAssetStage.setTitle("Produs nou");
        addAssetStage.show();
    }

    public void deleteProductButtonOnAction(ActionEvent e) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("Delete_Product.fxml"));
        Stage addAssetStage = new Stage(StageStyle.DECORATED);
        addAssetStage.setScene(new Scene(root));
        addAssetStage.setTitle("Stergerea produsului");
        addAssetStage.show();
    }

    public void changeProductButtonOnAction() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("Change_Product.fxml"));
        Stage addAssetStage = new Stage(StageStyle.DECORATED);
        addAssetStage.setScene(new Scene(root));
        addAssetStage.setTitle("Modificare Produs");
        addAssetStage.show();
    }
}
