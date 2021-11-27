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
import sample.Claims_Debts;
import sample.DatabaseConnection;
import sample.Produse;
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
    private AnchorPane dashboardAnchor, transactionAnchor, incomesAnchor, costsAnchor, productsAnchor, assetsAnchor, claimsAnchor;
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
    private TextField currentAssetsSearchTextField, mijloaceFixeSearchTextField, terenuriSearchTextField, depoziteSearchTextField, produseSearchTextField;
    @FXML
    private TextField produseInDepozitSearchTextField, claimsSearchTextField, debtsSearchTextField;
    @FXML
    private TableColumn<Produse, Integer> idProdusTableColumn, terenGrupProdusTableColumn, anProdusTableColumn;
    @FXML
    private TableColumn<Produse, String> denumireProdusTableColumn;
    @FXML
    private TableColumn<Produse, Float> cantitateRecoltataProduseTableColumn, cantitateRamasaProduseTableColumn;
    @FXML
    private TableView<Produse> produseTableView, produseInDepozitTableView;
    @FXML
    private TableColumn<Produse, Integer> idProdusInDepozitTableColumn, idDepozitProduseInStockTableColumn;
    @FXML
    private TableColumn<Produse,String> denumireProdusStockTableColumn, localitateDepozitProduseStockTableColumn;
    @FXML
    private TableColumn<Produse, Float> cantitateProduseInDepozitTableColumn;

    //CLAIMS
    @FXML
    private TableColumn<Claims_Debts, Integer> idClaimTableColumn;
    @FXML
    private TableColumn<Claims_Debts, String> aferentClaimTableColumn, numeClaimTableColumn;
    @FXML
    private TableColumn<Claims_Debts, Date> termenAchitareClaimTableColumn, dataAchitatClaimTableColumn;
    @FXML
    private TableColumn<Claims_Debts, Float> valueClaimTableColumn;
    @FXML
    private TableView<Claims_Debts> claimsTableView;
    
    //DEBTS
    @FXML
    private TableColumn<Claims_Debts, Integer> idDebtTableColumn;
    @FXML
    private TableColumn<Claims_Debts, String> aferentDebtTableColumn, numeDebtTableColumn;
    @FXML
    private TableColumn<Claims_Debts, Date> termenAchitareDebtTableColumn, dataAchitatDebtTableColumn;
    @FXML
    private TableColumn<Claims_Debts, Float> valueDebtTableColumn;
    @FXML
    private TableView<Claims_Debts> debtsTableView;

    //OBSV ACTIVE
    ObservableList<Assets> depoziteObservableList = FXCollections.observableArrayList();
    ObservableList<Assets> terenuriObservableList = FXCollections.observableArrayList();
    ObservableList<Assets> mijloaceFixeObservableList = FXCollections.observableArrayList();
    ObservableList<Assets> otherAssetsObservableList = FXCollections.observableArrayList();

    //OBSV PRODUSE
    ObservableList<Produse> produseObservableList = FXCollections.observableArrayList();
    ObservableList<Produse> produseInDepozitObservableList = FXCollections.observableArrayList();

    ObservableList<Claims_Debts> claimsObsList = FXCollections.observableArrayList();
    ObservableList<Claims_Debts> debtsObsList = FXCollections.observableArrayList();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //ASSETS
        currentAssetsSearchTextField.textProperty().addListener((observableValue, s, t1) -> searchCurrentAssetsInDB());
        mijloaceFixeSearchTextField.textProperty().addListener((observableValue, s, t1) -> searchMijloaceFixeInDB());
        terenuriSearchTextField.textProperty().addListener((observableValue, s, t1) -> searchPamanturiInDB());
        depoziteSearchTextField.textProperty().addListener((observableValue, s, t1) -> searchDepoziteInDB());

        //PRODUSE
        produseSearchTextField.textProperty().addListener((ObservableValue, s, t1) -> searchProduseInDB());
        produseInDepozitSearchTextField.textProperty().addListener((ObservableValue, s, t1) -> searchProduseInDepozitDB());

        //CLAIMS
        claimsSearchTextField.textProperty().addListener((observable, s, t1) -> searchClaims());
        debtsSearchTextField.textProperty().addListener((observable, s, t1) -> searchDebts());

    }

    //GET DEBTS CLAIMS FROM DB
    private void getClaimsDebtsFromDb(){
        debtsObsList.clear();
        claimsObsList.clear();
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getClaimsFacturiQuery = "SELECT datorii_creante._id_datorie_creanta AS id, datorii_creante.aferenta AS aferenta, " +
                "datorii_creante.termen_limita as termenlimita, datorii_creante.valoare AS valoare, " +
                "datorii_creante.data_achitat AS dataachitat, facturi.contractant AS contractant FROM datorii_creante INNER JOIN facturi " +
                "ON datorii_creante.element_id = facturi._id_factura WHERE datorii_creante.tip = " +
                "'Creanta' AND datorii_creante.aferenta = 'Factura'";
        String getClaimsPersonal = "SELECT datorii_creante._id_datorie_creanta AS id, datorii_creante.aferenta AS aferenta, " +
                "datorii_creante.termen_limita AS termenlimita, datorii_creante.valoare AS valoare, " +
                "datorii_creante.data_achitat AS dataachitat, personal.nume AS nume FROM datorii_creante INNER JOIN personal " +
                "ON datorii_creante.element_id = personal._id_personal WHERE datorii_creante.tip = " +
                "'Creanta' AND datorii_creante.aferenta = 'Personal'";
        String getDebtsFacturiQuery = "SELECT datorii_creante._id_datorie_creanta AS id, datorii_creante.aferenta AS aferenta, " +
                "datorii_creante.termen_limita as termenlimita, datorii_creante.valoare AS valoare, " +
                "datorii_creante.data_achitat AS dataachitat, facturi.contractant AS contractant FROM datorii_creante INNER JOIN facturi " +
                "ON datorii_creante.element_id = facturi._id_factura WHERE datorii_creante.tip = " +
                "'Datorie' AND datorii_creante.aferenta = 'Factura'";
        String getDebtsPersonalQuery = "SELECT datorii_creante._id_datorie_creanta AS id, datorii_creante.aferenta AS aferenta, " +
                "datorii_creante.termen_limita AS termenlimita, datorii_creante.valoare AS valoare, " +
                "datorii_creante.data_achitat AS dataachitat, personal.nume AS nume FROM datorii_creante INNER JOIN personal " +
                "ON datorii_creante.element_id = personal._id_personal WHERE datorii_creante.tip = " +
                "'Datorie' AND datorii_creante.aferenta = 'Personal'";
        try{
            ResultSet getFacturiClaimsResultSet = conn.createStatement().executeQuery(getClaimsFacturiQuery);
            while(getFacturiClaimsResultSet.next()){
                claimsObsList.add(new Claims_Debts(getFacturiClaimsResultSet.getInt("id"), getFacturiClaimsResultSet.getString("aferenta"),
                        getFacturiClaimsResultSet.getDate("termenlimita"), getFacturiClaimsResultSet.getFloat("valoare"),
                        getFacturiClaimsResultSet.getDate("dataachitat"), getFacturiClaimsResultSet.getString("contractant")));
            }
            ResultSet getPersonalClaimsResultSet = conn.createStatement().executeQuery(getClaimsPersonal);
            while(getPersonalClaimsResultSet.next()){
                claimsObsList.add(new Claims_Debts(getPersonalClaimsResultSet.getInt("id"), getPersonalClaimsResultSet.getString("aferenta"),
                        getPersonalClaimsResultSet.getDate("termenlimita"), getPersonalClaimsResultSet.getFloat("valoare"),
                        getPersonalClaimsResultSet.getDate("dataachitat"), getPersonalClaimsResultSet.getString("nume")));
            }
            //DEBTS
            ResultSet getFacturiDebtsResultSet = conn.createStatement().executeQuery(getDebtsFacturiQuery);
            while(getFacturiDebtsResultSet.next()){
                debtsObsList.add(new Claims_Debts(getFacturiDebtsResultSet.getInt("id"), getFacturiDebtsResultSet.getString("aferenta"),
                        getFacturiDebtsResultSet.getDate("termenlimita"), getFacturiDebtsResultSet.getFloat("valoare"),
                        getFacturiDebtsResultSet.getDate("dataachitat"), getFacturiDebtsResultSet.getString("contractant")));
            }
            ResultSet getPersonalDebtsResultSet = conn.createStatement().executeQuery(getDebtsPersonalQuery);
            while(getPersonalDebtsResultSet.next()){
                debtsObsList.add(new Claims_Debts(getPersonalDebtsResultSet.getInt("id"), getPersonalDebtsResultSet.getString("aferenta"),
                        getPersonalDebtsResultSet.getDate("termenlimita"), getPersonalDebtsResultSet.getFloat("valoare"),
                        getPersonalDebtsResultSet.getDate("dataachitat"), getPersonalDebtsResultSet.getString("nume")));
            }
            conn.close();
            loadClaimsInTable();
            loadDebtsInTable();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void searchClaims(){
        ObservableList<Claims_Debts> aux = FXCollections.observableArrayList();
        for (Claims_Debts obj : claimsObsList){
            if(obj.toString().toLowerCase().contains(claimsSearchTextField.getText().toLowerCase())){
                aux.add(obj);
            }
        }
        claimsTableView.setItems(aux);
    }

    private void searchDebts(){
        ObservableList<Claims_Debts> aux = FXCollections.observableArrayList();
        for (Claims_Debts x : debtsObsList){
            if(x.toString().toLowerCase().contains(debtsSearchTextField.getText().toLowerCase())){
                aux.add(x);
            }
        }
        debtsTableView.setItems(aux);
    }

    //LOAD DEBTS CLAIMS IN TABLES
    private void loadClaimsInTable(){
        idClaimTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        aferentClaimTableColumn.setCellValueFactory(new PropertyValueFactory<>("aferenta"));
        termenAchitareClaimTableColumn.setCellValueFactory(new PropertyValueFactory<>("termenAchitare"));
        valueClaimTableColumn.setCellValueFactory(new PropertyValueFactory<>("valoare"));
        dataAchitatClaimTableColumn.setCellValueFactory(new PropertyValueFactory<>("dataAchitat"));
        numeClaimTableColumn.setCellValueFactory(new PropertyValueFactory<>("contractant"));
        claimsTableView.setItems(claimsObsList);
    }

    private void loadDebtsInTable(){
        idDebtTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        aferentDebtTableColumn.setCellValueFactory(new PropertyValueFactory<>("aferenta"));
        termenAchitareDebtTableColumn.setCellValueFactory(new PropertyValueFactory<>("termenAchitare"));
        valueDebtTableColumn.setCellValueFactory(new PropertyValueFactory<>("valoare"));
        dataAchitatDebtTableColumn.setCellValueFactory(new PropertyValueFactory<>("dataAchitat"));
        numeDebtTableColumn.setCellValueFactory(new PropertyValueFactory<>("contractant"));
        debtsTableView.setItems(debtsObsList);
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

    //SEARCH DATA IN PRODUSE
    private void searchProduseInDB(){
        produseObservableList.clear();
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String produseQuery = "SELECT _id_produs, denumire_produs, cantitate_recoltata, cantitate_ramasa, "
                + "an, teren_grup FROM produse WHERE _id_produs LIKE '%" + produseSearchTextField.getText() +"%' OR "
                + "denumire_produs LIKE '%" + produseSearchTextField.getText() + "%' OR cantitate_recoltata LIKE '%"
                + produseSearchTextField.getText() + "%' OR cantitate_ramasa LIKE '%" + produseSearchTextField.getText()
                + "%' OR an LIKE '%" + produseSearchTextField.getText() + "%' OR teren_grup LIKE '%"
                + produseSearchTextField.getText() + "%'";
        try{
            ResultSet produseResultSet = conn.createStatement().executeQuery(produseQuery);
            while(produseResultSet.next()){
                produseObservableList.add( new Produse(produseResultSet.getInt("_id_produs"), produseResultSet.getString("denumire_produs"),
                        produseResultSet.getFloat("cantitate_recoltata"), produseResultSet.getFloat("cantitate_ramasa"),
                        produseResultSet.getInt("an"), produseResultSet.getInt("teren_grup")));
            }
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
        loadProduseInTable();
    }

    private void searchProduseInDepozitDB(){
        produseInDepozitObservableList.clear();
        String produseSearch = produseInDepozitSearchTextField.getText();
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String produseStockQuery = "SELECT produse._id_produs, produse.denumire_produs, produse.cantitate_ramasa, depozite._id_depozit, "
                +"depozite.localitate FROM produse INNER JOIN depozite ON produse.depozit_id = depozite._id_depozit WHERE produse.cantitate_ramasa > 0 "
                +"AND (produse._id_produs LIKE '%" + produseSearch + "%' OR produse.denumire_produs LIKE '%" + produseSearch + "%' OR "
                +" produse.cantitate_ramasa LIKE '%" + produseSearch + "%' OR depozite._id_depozit LIKE '%" + produseSearch + "%' OR "
                +"depozite.localitate LIKE '%" + produseSearch +"%')";
        try{
            ResultSet produseInDepozitResultSet = conn.createStatement().executeQuery(produseStockQuery);
            while(produseInDepozitResultSet.next()){
                produseInDepozitObservableList.add(new Produse(produseInDepozitResultSet.getInt("produse._id_produs"),
                        produseInDepozitResultSet.getString("produse.denumire_produs"),
                        produseInDepozitResultSet.getFloat("produse.cantitate_ramasa"),
                        produseInDepozitResultSet.getInt("depozite._id_depozit"),
                        produseInDepozitResultSet.getString("depozite.localitate")));
            }
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
        loadProduseDepozitInTable();
    }

    //LOAD DATA IN TABLES PRODUSE
    private void loadProduseInTable(){
        idProdusTableColumn.setCellValueFactory(new PropertyValueFactory<>("idProdus"));
        denumireProdusTableColumn.setCellValueFactory(new PropertyValueFactory<>("denumireProdus"));
        cantitateRecoltataProduseTableColumn.setCellValueFactory(new PropertyValueFactory<>("cantitateRecoltata"));
        cantitateRamasaProduseTableColumn.setCellValueFactory(new PropertyValueFactory<>("cantitateDepozitata"));
        anProdusTableColumn.setCellValueFactory(new PropertyValueFactory<>("anProdus"));
        terenGrupProdusTableColumn.setCellValueFactory(new PropertyValueFactory<>("grupTeren"));
        produseTableView.setItems(produseObservableList);
    }

    private void loadProduseDepozitInTable(){
        idProdusInDepozitTableColumn.setCellValueFactory(new PropertyValueFactory<>("idProdus"));
        denumireProdusStockTableColumn.setCellValueFactory(new PropertyValueFactory<>("denumireProdus"));
        cantitateProduseInDepozitTableColumn.setCellValueFactory(new PropertyValueFactory<>("cantitateDepozitata"));
        idDepozitProduseInStockTableColumn.setCellValueFactory(new PropertyValueFactory<>("idDepozit"));
        localitateDepozitProduseStockTableColumn.setCellValueFactory(new PropertyValueFactory<>("localitateDepozit"));
        produseInDepozitTableView.setItems(produseInDepozitObservableList);
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

    /////?????
    public void exitButtonOnAction(ActionEvent e){
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
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

    public void sellProductButtonOnAction() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("Sell_Product.fxml"));
        Stage sellProduct = new Stage(StageStyle.DECORATED);
        sellProduct.setScene(new Scene(root));
        sellProduct.setTitle("Comercializarea produsului");
        sellProduct.show();
    }

    //CLAIMS & DEBTS BUTTONS
    public void addDebtClaimButtonOnAction() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("Creante_Datorii.fxml"));
        Stage sellProduct = new Stage(StageStyle.DECORATED);
        sellProduct.setScene(new Scene(root));
        sellProduct.setTitle("Creanta/Datorie noua");
        sellProduct.show();
    }

    public void changeDebtClaimButtonOnAction() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("Change_Debts_Claims.fxml"));
        Stage sellProduct = new Stage(StageStyle.DECORATED);
        sellProduct.setScene(new Scene(root));
        sellProduct.setTitle("Modificare Creanta / Datorie");
        sellProduct.show();
    }

    public void deleteDebtClaimButtonOnAction() throws  IOException{
        Parent root = FXMLLoader.load(getClass().getResource("Delete_Debt_Claim.fxml"));
        Stage deleteDebtClaim = new Stage(StageStyle.DECORATED);
        deleteDebtClaim.setScene(new Scene(root));
        deleteDebtClaim.setTitle("Stergere Creanta / Datorie");
        deleteDebtClaim.show();
    }

    //COSTS BUTTONS
    public void addCostButtonOnAction() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("New_Costs.fxml"));
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene(root));
        stage.setTitle("Costuri de productie noi");
        stage.show();
    }

    public void changeCostButtonOnAction() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("Costs_Change.fxml"));
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene(root));
        stage.setTitle("Modificarea costurilor");
        stage.show();
    }

    public void deleteCostButtonOnAction() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("Costs_Delete.fxml"));
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene(root));
        stage.setTitle("Stergerea costurilo");
        stage.show();
    }

    //MENU BUTTONS
    public void menuButtonsOnAction(ActionEvent e){
        Stream.of(dashboardButton,transactionButton,incomesButton,costsButton,productsButton,assetsButton,claimsButton).forEach(Button -> Button.setStyle("menuButton"));
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
            incomesButton.setStyle("-fx-text-fill: #cfcb5b; -fx-background-color: rgba(89,50,15,0.2)");
        }
        else if(e.getSource() == costsButton){
            costsAnchor.toFront();
            costsButton.setStyle("-fx-text-fill: #cfcb5b; -fx-background-color: rgba(89,50,15,0.2)");
        }
        else if(e.getSource() == productsButton){
            productsAnchor.toFront();
            productsButton.setStyle("-fx-text-fill: #cfcb5b; -fx-background-color: rgba(89,50,15,0.2)");
            searchProduseInDB();
            searchProduseInDepozitDB();
        }
        else if(e.getSource() == assetsButton){
            assetsAnchor.toFront();
            assetsButton.setStyle("-fx-text-fill: #cfcb5b; -fx-background-color: rgba(89,50,15,0.2)");
            loadAllAssetsInAssetTableViews();
        }
        else if(e.getSource() == claimsButton){
            claimsAnchor.toFront();
            claimsButton.setStyle("-fx-text-fill: #cfcb5b; -fx-background-color: rgba(89,50,15,0.2)");
            getClaimsDebtsFromDb();
        }
    }
}
