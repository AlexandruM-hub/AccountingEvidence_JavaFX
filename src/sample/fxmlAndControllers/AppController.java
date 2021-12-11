package sample.fxmlAndControllers;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import sample.*;
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
    private Button staffButton;
    @FXML
    private Button dashboardButton, transactionButton, incomesButton, costsButton, productsButton, assetsButton, claimsButton, debtsButton;
    @FXML
    private AnchorPane staffAnchorPane;
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
    private TableColumn<Assets, String> stareDepoziteTableColumn, stareTerenuriTableColumn;
    @FXML
    private TextField currentAssetsSearchTextField, mijloaceFixeSearchTextField, terenuriSearchTextField, depoziteSearchTextField, produseSearchTextField;
    @FXML
    private TextField produseInDepozitSearchTextField, claimsSearchTextField, debtsSearchTextField, costsSearchTextField, incomesSearchTextField;
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

    //COSTS
    @FXML
    private TableView<Costs> costsTableView;
    @FXML
    private TableColumn<Integer, Costs> idCostTableColumn, elementIdCostsTableColumn, produsIdCostsTableColumn;
    @FXML
    private TableColumn<String, Costs>  tipCostsTableColumn, scopCostsTableColumn;
    @FXML
    private TableColumn<Float, Costs> cantitateCostsTableColumn, valoareCostsTableColumn;
    @FXML
    private TableColumn<Date, Costs> dataCostsTableColumn;
    @FXML
    private TableColumn<Integer, Costs> persoanaIdTableColumn, activIdTableColumn;

    //INCOMES
    @FXML
    private TableView<Invoices> incomesTableView;
    @FXML
    private TableColumn<Integer, Invoices> idFacturaIncomesTableColumn, idProdusIncomesTableColumn;
    @FXML
    private TableColumn<String, Invoices> nrFacturaIncomesTableColumn, cumparatorIncomesTableColumn, denumireProdusIncomesTableColumn;
    @FXML
    private TableColumn<Date, Invoices> dataIncomesTableColumn;
    @FXML
    private TableColumn<Float, Invoices> cantitateIncomesTableColumn, pretIncomesTableColumn, valoareIncomesTableColumn;

    //INVOICES
    @FXML
    private TableView<Invoices> invoicesTableView;
    @FXML
    private TableColumn<Integer, Invoices> idFacturaInvoicesTableColumn, idProdusInvoicesTableColumn;
    @FXML
    private TableColumn<Date, Invoices> invoicesDateTableColumn;
    @FXML
    private TableColumn<String, Invoices> nrFacturaInvoicesTableColumn, contractantInvoicesTableColumn, tipInvoiceTableColumn;
    @FXML
    private TableColumn<String, Invoices> tipMarfaInvoiceTableColumn, denumireMarfaInvoiceTableColumn;
    @FXML
    private TableColumn<Float, Invoices> cantitateInvoiceTableColumn, pretInvoiceTableColumn, valoareInvoiceTableColumn;
    @FXML
    private Tab transactionTab, inTab, outTab;
    @FXML
    private TextField invoicesSearchTextField;
    @FXML
    private TableColumn<Integer, Invoices> idTerenInvoicesTableColumn, idDepozitInvoiceTableColumn, idAssetInvoicesTableColumn;

    private boolean checkTransitions = true;
    @FXML
    private ComboBox<String> newInvoiceComboBox;
    @FXML
    private Pane newInvoicePane;

    //STAFF
    @FXML
    private TableColumn<String, Staff> numeStaffTableColumn, prenumeStaffTableColumn, emailStaffTablecolumn, telefonStaffTableColumn;
    @FXML
    private TableColumn<Integer, Staff> idPersoanaStaffTableColumn;
    @FXML
    private TableColumn<Date, Staff> dataNasterePersoanaTableColumn, dataAngajarePersoanaTableColumn;
    @FXML
    private TableView<Staff> staffTableView;
    @FXML
    private TextField searchStaffTextField;
    @FXML
    private Button addStaffButton, changeStaffButton, deleteStaffButton;
    @FXML
    private Button allowAccesButton, changePasswordButton;
    @FXML
    private TableColumn<Staff, String> functieStaffTableColumn;
    @FXML
    private TableColumn<Staff, Float> salariuStaffTableColumn;

    //DASHBOARD
    @FXML
    private Text profitText, venituriText, costuriText, cheltuieliText, creanteText, datoriiText;
    @FXML
    private PieChart costsPieChart;
    @FXML
    private BarChart<String, Float> productsBarChart;

    //OBSV ACTIVE
    ObservableList<Assets> depoziteObservableList = FXCollections.observableArrayList();
    ObservableList<Assets> terenuriObservableList = FXCollections.observableArrayList();
    ObservableList<Assets> mijloaceFixeObservableList = FXCollections.observableArrayList();
    ObservableList<Assets> otherAssetsObservableList = FXCollections.observableArrayList();

    //OBSV PRODUSE
    ObservableList<Produse> produseObservableList = FXCollections.observableArrayList();
    ObservableList<Produse> produseInDepozitObservableList = FXCollections.observableArrayList();

    //OBSV CLAIMS & DEBTS
    ObservableList<Claims_Debts> claimsObsList = FXCollections.observableArrayList();
    ObservableList<Claims_Debts> debtsObsList = FXCollections.observableArrayList();

    //OBSV COSTS
    ObservableList<Costs> costsObservableList = FXCollections.observableArrayList();

    //INCOMES
    ObservableList<Invoices> incomesObservableList = FXCollections.observableArrayList();

    //INVOICES
    ObservableList<Invoices> invoicesObservableList = FXCollections.observableArrayList();

    //STAFF
    ObservableList<Staff> staffObservableList = FXCollections.observableArrayList();

    Image iconIMG = new Image("file:D:\\Lucrari de laborator\\Semestrul 3\\Teza de an\\TezaProiect\\src\\css\\tractor.png");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Runnable loadingDataFromDbOtherThread = () -> Platform.runLater(this::loadAllAssetsInAssetTableViews);
        new Thread(loadingDataFromDbOtherThread).start();

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

        //COSTS
        costsSearchTextField.textProperty().addListener(((observableValue, s, t1) -> costsSearch()));

        //INCOMES
        incomesSearchTextField.textProperty().addListener(((observableValue, s, t1) -> searchIncomes()));

        //INVOICES
        invoicesSearchTextField.textProperty().addListener(((observableValue, s, t1) -> searchInvoices()));
        newInvoiceComboBox.getItems().addAll("Active", "Produse", "Alte");
        newInvoiceComboBox.getSelectionModel().selectedItemProperty().addListener(((observableValue, s, t1) -> {
            try{
                switch (newInvoiceComboBox.getValue()) {
                    case "Active":
                        addAssetButtonOnAction();
                        break;
                    case "Produse":
                        sellProductButtonOnAction();
                        break;
                    case "Alte":
                        addNewInvoice();
                        break;
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }));
        loadStaffInTable();
        searchStaffTextField.textProperty().addListener(((observableValue, s, t1) -> searchStaff()));

        getStatisticsFromDb();
        pieChartCosts();
        ProductsBarChart();
        costsLineChartFunction();
        costsLineChart.getData().addAll(seriaCheltuieli, seriaVenituri, seriaCosturi);
        productsBarChart.getData().addAll(series1, series2);
    }

    XYChart.Series<String, Float> seriaVenituri = new XYChart.Series<>();
    XYChart.Series<String, Float> seriaCosturi = new XYChart.Series<>();
    XYChart.Series<String, Float> seriaCheltuieli = new XYChart.Series<>();


    private void costsLineChartFunction(){
        seriaCheltuieli.getData().clear();
        seriaCosturi.getData().clear();
        seriaVenituri.getData().clear();
        seriaCosturi.setName("Costuri");
        seriaVenituri.setName("Venituri");
        seriaCheltuieli.setName("Cheltuieli");

        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getCostsQuery = "SELECT month(data_cost) as luna, sum(valoare) as valoare from costul_productiei GROUP by month(data_cost)";
        String getVenituriQuery = "select month(data) as luna, sum(pret * cantitate) as valoare from facturi where tip_intrare_iesire = " +
                "'Iesire' and data >= '2021-01-01' group by month(data)";
        String getCheltuieliQuery = "select month(data) as luna, sum(pret * cantitate) as valoare from facturi where tip_intrare_iesire = " +
                "'Intrare' and data >= '2021-01-01' group by month(data)";
        try{
            ResultSet getCostResultSet = conn.createStatement().executeQuery(getCostsQuery);
            while(getCostResultSet.next()){
                seriaCosturi.getData().add(new XYChart.Data<>(getCostResultSet.getString("luna"),
                        getCostResultSet.getFloat("valoare")));
            }
            ResultSet getVenituriResultSet = conn.createStatement().executeQuery(getVenituriQuery);
            while(getVenituriResultSet.next()){
                seriaVenituri.getData().add(new XYChart.Data<>(getVenituriResultSet.getString("luna"),
                        getVenituriResultSet.getFloat("valoare")));
            }
            ResultSet getCheltuieliResultSet = conn.createStatement().executeQuery(getCheltuieliQuery);
            while(getCheltuieliResultSet.next()){
                seriaCheltuieli.getData().add(new XYChart.Data<>(getCheltuieliResultSet.getString("luna"),
                        getCheltuieliResultSet.getFloat("valoare")));
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    @FXML
    private LineChart<String, Float> costsLineChart;

    XYChart.Series<String, Float> series1 = new XYChart.Series<>();
    XYChart.Series<String, Float> series2 = new XYChart.Series<>();

    private void ProductsBarChart(){
        series1.getData().clear();
        series2.getData().clear();
        series1.setName("Venituri");
        series2.setName("Costuri");
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getVenituriQuery = "SELECT sum(facturi.pret * facturi.cantitate) as valoare, produse.denumire_produs " +
                "as denumire from facturi inner join produse on facturi.produse_id = produse._id_produs group by " +
                "produse.denumire_produs";
        String getAverageQuery = "select ((select sum(valoare) from costul_productiei where produs_id IS NULL) / " +
                "(select sum(cantitate_recoltata) from produse)) as average";
        String createViewQuery = "create view costuri_produse (cost_produs, denumire) as (select sum(costul_productiei.valoare), " +
                "produse.denumire_produs from costul_productiei inner join produse on costul_productiei.produs_id = produse._id_produs group by " +
                "produse.denumire_produs)";
        String getProductCosts = "SELECT sum(produse.cantitate_recoltata) as cantitate, costuri_produse.cost_produs as cost, " +
                "costuri_produse.denumire as denumire from produse INNER JOIN costuri_produse on produse.denumire_produs = costuri_produse.denumire " +
                "GROUP BY denumire";
        try{
            ResultSet getVenituriResultSet = conn.createStatement().executeQuery(getVenituriQuery);
            while(getVenituriResultSet.next()){
                series1.getData().add(new XYChart.Data<>(getVenituriResultSet.getString("denumire"), getVenituriResultSet.getFloat("valoare")));
            }
            float averageCost = 0;
            ResultSet getAvgResultSet = conn.createStatement().executeQuery(getAverageQuery);
            if(getAvgResultSet.next()){
                averageCost = getAvgResultSet.getFloat("average");
            }
            conn.createStatement().execute(createViewQuery);
            ResultSet getCostsPerProductRs = conn.createStatement().executeQuery(getProductCosts);
            while(getCostsPerProductRs.next()){
                series2.getData().add(new XYChart.Data<>(getCostsPerProductRs.getString("denumire"),
                        getCostsPerProductRs.getFloat("cost") + (averageCost * getCostsPerProductRs.getFloat("cantitate"))));
            }
            conn.createStatement().execute("drop view costuri_produse");
        } catch (SQLException e){
            e.printStackTrace();
        }

    }

    ObservableList<PieChart.Data> costsPieChartDataObsList = FXCollections.observableArrayList();

    private void pieChartCosts(){
        costsPieChartDataObsList.clear();
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getCostsFromDbQuery = "select tip ,sum(valoare) as valoareCosturi from costul_productiei group by tip";
        try{
            ResultSet getCostsFromDb = conn.createStatement().executeQuery(getCostsFromDbQuery);
            while(getCostsFromDb.next()){
                costsPieChartDataObsList.add(new PieChart.Data(getCostsFromDb.getString("tip"), getCostsFromDb.getFloat("valoareCosturi")));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        costsPieChart.setTitle("Costuri");
        costsPieChart.setStartAngle(120);
        costsPieChart.setData(costsPieChartDataObsList);
    }



    private void getStatisticsFromDb(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getProfitQuery = "select ((select sum(cantitate * pret) from facturi where tip_intrare_iesire = 'Iesire' and data >= '2021-01-01') - " +
                "(select sum(valoare) from costul_productiei where data_cost >= '2021-01-01')) as profit";
        String getVenituriQuery = "select sum(cantitate * pret) as valoareVenituri from facturi where tip_intrare_iesire = 'Iesire' and data >= '2021-01-01'";
        String getCosturiQuery = "select sum(valoare) as valoareCosturi from costul_productiei where data_cost >= '2021-01-01'";
        String getCheltuieliQuery = "select sum(cantitate * pret) as valoareCheltuieli from facturi where tip_intrare_iesire = 'Intrare' and data >= '2021-01-01'";
        String getCreanteQuery = "select sum(valoare) as valoareCreanta from datorii_creante where data_achitat IS NULL and tip = 'Creanta'";
        String getDatoriiQuery = "select sum(valoare) as valoareDatorie from datorii_creante where data_achitat IS NULL and tip = 'Datorie'";
        try{
            ResultSet getProfit = conn.createStatement().executeQuery(getProfitQuery);
            if(getProfit.next()){
                profitText.setText(String.valueOf(getProfit.getFloat("profit")));
            }
            ResultSet getVenituriResultSet = conn.createStatement().executeQuery(getVenituriQuery);
            if(getVenituriResultSet.next()){
                venituriText.setText(String.valueOf(getVenituriResultSet.getFloat("valoareVenituri")));
            }
            ResultSet getCosturiResultSet = conn.createStatement().executeQuery(getCosturiQuery);
            if(getCosturiResultSet.next()){
                costuriText.setText("" + getCosturiResultSet.getFloat("valoareCosturi"));
            }
            ResultSet getCheltuieliResultSet = conn.createStatement().executeQuery(getCheltuieliQuery);
            if(getCheltuieliResultSet.next()){
                cheltuieliText.setText("" + getCheltuieliResultSet.getFloat("valoareCheltuieli"));
            }
            ResultSet getCreanteResultSet = conn.createStatement().executeQuery(getCreanteQuery);
            if(getCreanteResultSet.next()){
                creanteText.setText("" + getCreanteResultSet.getFloat("valoareCreanta"));
            }
            ResultSet getDatoriiResultSet = conn.createStatement().executeQuery(getDatoriiQuery);
            if(getDatoriiResultSet.next()){
                datoriiText.setText("" + getDatoriiResultSet.getFloat("valoareDatorie"));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }



    //STAFF
    private void getStaffFromDb(){
        staffObservableList.clear();
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getStaffQuery = "SELECT _id_personal, nume, prenume, telefon, data_nastere," +
                " email, data_angajare, functie, salariu from personal";
        try{
            ResultSet getStaffResultSet = conn.createStatement().executeQuery(getStaffQuery);
            while(getStaffResultSet.next()){
                staffObservableList.add(new Staff(getStaffResultSet.getInt("_id_personal"), getStaffResultSet.getString("nume"),
                        getStaffResultSet.getString("prenume"), getStaffResultSet.getString("telefon"),
                        getStaffResultSet.getDate("data_nastere"), getStaffResultSet.getString("email"),
                        getStaffResultSet.getDate("data_angajare"), getStaffResultSet.getString("functie"),
                        getStaffResultSet.getFloat("salariu")));
            }
            staffTableView.setItems(staffObservableList);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }


    private void loadStaffInTable(){
        idPersoanaStaffTableColumn.setCellValueFactory(new PropertyValueFactory<>("idPersoana"));
        numeStaffTableColumn.setCellValueFactory(new PropertyValueFactory<>("nume"));
        prenumeStaffTableColumn.setCellValueFactory(new PropertyValueFactory<>("prenume"));
        telefonStaffTableColumn.setCellValueFactory(new PropertyValueFactory<>("telefon"));
        dataNasterePersoanaTableColumn.setCellValueFactory(new PropertyValueFactory<>("dataNastere"));
        emailStaffTablecolumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        dataAngajarePersoanaTableColumn.setCellValueFactory(new PropertyValueFactory<>("dataAngajare"));
        functieStaffTableColumn.setCellValueFactory(new PropertyValueFactory<>("functie"));
        salariuStaffTableColumn.setCellValueFactory(new PropertyValueFactory<>("salariu"));
    }

    private void searchStaff(){
        ObservableList<Staff> aux = FXCollections.observableArrayList();
        for(Staff x : staffObservableList){
            if(x.toString().toLowerCase().contains(searchStaffTextField.getText().toLowerCase())){
                aux.add(x);
            }
        }
        staffTableView.setItems(aux);
    }


    public void transactionOnAction(){
        transactionTab.setOnSelectionChanged(event -> {
            if(transactionTab.isSelected()) {
                String getAllInvoicesQuery = "SELECT *, cantitate * pret as valoare FROM facturi";
                getInvoicesFromDb(getAllInvoicesQuery);
            }
        });
        inTab.setOnSelectionChanged(event -> {
            if(inTab.isSelected()) {
                String getAllInvoicesQuery = "SELECT *, cantitate * pret as valoare FROM facturi where tip_intrare_iesire = 'Intrare'";
                getInvoicesFromDb(getAllInvoicesQuery);
            }
        });
        outTab.setOnSelectionChanged(event -> {
            if(outTab.isSelected()){
                String query = "SELECT *, cantitate * pret as valoare FROM facturi where tip_intrare_iesire = 'Iesire'";
                getInvoicesFromDb(query);
            }
        });
        //initializarea tranzactiilor
        if(checkTransitions){
            String getAllInvoicesQuery = "SELECT *, cantitate * pret as valoare FROM facturi";
            getInvoicesFromDb(getAllInvoicesQuery);
            checkTransitions = false;
        }
    }

    //GET INVOICES FROM DB
    private void getInvoicesFromDb(String query){
        invoicesObservableList.clear();
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        try{
            ResultSet getAllInvoicesResultSet = conn.createStatement().executeQuery(query);
            while(getAllInvoicesResultSet.next()){
                invoicesObservableList.add(new Invoices(getAllInvoicesResultSet.getInt("_id_factura"),
                        getAllInvoicesResultSet.getString("nr_factura"), getAllInvoicesResultSet.getString("tip_intrare_iesire"),
                        getAllInvoicesResultSet.getString("contractant"), getAllInvoicesResultSet.getDate("data"),
                        getAllInvoicesResultSet.getString("tip_marfa"), getAllInvoicesResultSet.getString("denumire_marfa"),
                        getAllInvoicesResultSet.getFloat("cantitate"), getAllInvoicesResultSet.getFloat("pret"),
                        getAllInvoicesResultSet.getFloat("valoare"), getAllInvoicesResultSet.getInt("activ_id"),
                        getAllInvoicesResultSet.getInt("depozit_id"), getAllInvoicesResultSet.getInt("teren_id"),
                        getAllInvoicesResultSet.getInt("produse_id")));
            }
            conn.close();
            loadInvoicesInTable();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void loadInvoicesInTable(){
        idFacturaInvoicesTableColumn.setCellValueFactory(new PropertyValueFactory<>("idFactura"));
        nrFacturaInvoicesTableColumn.setCellValueFactory(new PropertyValueFactory<>("nrFactura"));
        contractantInvoicesTableColumn.setCellValueFactory(new PropertyValueFactory<>("cumparator"));
        invoicesDateTableColumn.setCellValueFactory(new PropertyValueFactory<>("dataVanzare"));
        denumireMarfaInvoiceTableColumn.setCellValueFactory(new PropertyValueFactory<>("denumireProdus"));
        cantitateInvoiceTableColumn.setCellValueFactory(new PropertyValueFactory<>("cantitate"));
        pretInvoiceTableColumn.setCellValueFactory(new PropertyValueFactory<>("pret"));
        valoareInvoiceTableColumn.setCellValueFactory(new PropertyValueFactory<>("valoare"));
        idProdusInvoicesTableColumn.setCellValueFactory(new PropertyValueFactory<>("idProdus"));
        tipInvoiceTableColumn.setCellValueFactory(new PropertyValueFactory<>("tipFactura"));
        tipMarfaInvoiceTableColumn.setCellValueFactory(new PropertyValueFactory<>("tipMarfa"));
        idTerenInvoicesTableColumn.setCellValueFactory(new PropertyValueFactory<>("idTeren"));
        idDepozitInvoiceTableColumn.setCellValueFactory(new PropertyValueFactory<>("idDepozit"));
        idAssetInvoicesTableColumn.setCellValueFactory(new PropertyValueFactory<>("idActiv"));
        invoicesTableView.setItems(invoicesObservableList);
    }

    private void searchInvoices(){
        ObservableList<Invoices> aux = FXCollections.observableArrayList();
        for(Invoices x : invoicesObservableList){
            if(x.toString().toLowerCase().contains(invoicesSearchTextField.getText().toLowerCase())){
                aux.add(x);
            }
        }
        invoicesTableView.setItems(aux);
    }

    //GET INCOMES FROM DB
    private void getIncomesFromDb(){
        incomesObservableList.clear();
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getIncomesQuery = "SELECT _id_factura, nr_factura, contractant, data, denumire_marfa, cantitate, pret, cantitate * pret as valoare, produse_id " +
                "from facturi where tip_intrare_iesire = 'Iesire' and tip_marfa = 'Produse'";
        try{
            ResultSet getIncomesResultSet = conn.createStatement().executeQuery(getIncomesQuery);
            while(getIncomesResultSet.next()){
                incomesObservableList.add(new Invoices(getIncomesResultSet.getInt("_id_factura"), getIncomesResultSet.getString("nr_factura"),
                        getIncomesResultSet.getString("contractant"), getIncomesResultSet.getDate("data"),
                        getIncomesResultSet.getString("denumire_marfa"), getIncomesResultSet.getFloat("cantitate"),
                        getIncomesResultSet.getFloat("pret"), getIncomesResultSet.getFloat("valoare"),
                        getIncomesResultSet.getInt("produse_id")));
            }
            conn.close();
            insertIncomesInTable();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void insertIncomesInTable(){
        idFacturaIncomesTableColumn.setCellValueFactory(new PropertyValueFactory<>("idFactura"));
        nrFacturaIncomesTableColumn.setCellValueFactory(new PropertyValueFactory<>("nrFactura"));
        cumparatorIncomesTableColumn.setCellValueFactory(new PropertyValueFactory<>("cumparator"));
        dataIncomesTableColumn.setCellValueFactory(new PropertyValueFactory<>("dataVanzare"));
        denumireProdusIncomesTableColumn.setCellValueFactory(new PropertyValueFactory<>("denumireProdus"));
        cantitateIncomesTableColumn.setCellValueFactory(new PropertyValueFactory<>("cantitate"));
        pretIncomesTableColumn.setCellValueFactory(new PropertyValueFactory<>("pret"));
        valoareIncomesTableColumn.setCellValueFactory(new PropertyValueFactory<>("valoare"));
        idProdusIncomesTableColumn.setCellValueFactory(new PropertyValueFactory<>("idProdus"));
        incomesTableView.setItems(incomesObservableList);
    }

    private void searchIncomes(){
        ObservableList<Invoices> aux = FXCollections.observableArrayList();
        for(Invoices x : incomesObservableList){
            if(x.toString().toLowerCase().contains(incomesSearchTextField.getText().toLowerCase())){
                aux.add(x);
            }
        }
        incomesTableView.setItems(aux);
    }

    //GET COSTS FROM DB
    private void getCostsFromDb(){
        costsObservableList.clear();
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getCostsFromDbQuery = "SELECT *, (select assets._id_asset from assets INNER JOIN " +
                "facturi ON assets._id_asset = facturi.activ_id where _id_factura = factura_id) as " +
                "activ_id from costul_productiei;";
        try{
            ResultSet getCostsResultSet = conn.createStatement().executeQuery(getCostsFromDbQuery);
            while(getCostsResultSet.next()){
                costsObservableList.add(new Costs(getCostsResultSet.getInt("_id_cost"),
                        getCostsResultSet.getString("tip"), getCostsResultSet.getString("scop"),
                        getCostsResultSet.getDate("data_cost"), getCostsResultSet.getFloat("cantitate"),
                        getCostsResultSet.getFloat("valoare"), getCostsResultSet.getInt("factura_id"),
                        getCostsResultSet.getInt("persoana_id"), getCostsResultSet.getInt("produs_id"),
                        getCostsResultSet.getInt("activ_id")));
            }
            conn.close();
            loadCostsInTable();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void loadCostsInTable(){
        idCostTableColumn.setCellValueFactory(new PropertyValueFactory<>("idCost"));
        tipCostsTableColumn.setCellValueFactory(new PropertyValueFactory<>("tipCost"));
        scopCostsTableColumn.setCellValueFactory(new PropertyValueFactory<>("scopCost"));
        dataCostsTableColumn.setCellValueFactory(new PropertyValueFactory<>("dataCost"));
        cantitateCostsTableColumn.setCellValueFactory(new PropertyValueFactory<>("cantitate"));
        valoareCostsTableColumn.setCellValueFactory(new PropertyValueFactory<>("valoare"));
        elementIdCostsTableColumn.setCellValueFactory(new PropertyValueFactory<>("idElement"));
        persoanaIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("idPersoana"));
        produsIdCostsTableColumn.setCellValueFactory(new PropertyValueFactory<>("idProdus"));
        activIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("idActiv"));
        costsTableView.setItems(costsObservableList);
    }

    private void costsSearch(){
        ObservableList<Costs> aux = FXCollections.observableArrayList();
        for(Costs x : costsObservableList){
            if(x.toString().toLowerCase().contains(costsSearchTextField.getText().toLowerCase())){
                aux.add(x);
            }
        }
        costsTableView.setItems(aux);
    }

    //GET DEBTS CLAIMS FROM DB
    private void getClaimsDebtsFromDb(){
        debtsObsList.clear();
        claimsObsList.clear();
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getClaimsFacturiQuery = "SELECT datorii_creante._id_datorie_creanta AS id, " +
                "datorii_creante.termen_limita as termenlimita, datorii_creante.valoare AS valoare, " +
                "datorii_creante.data_achitat AS dataachitat, facturi.contractant AS contractant FROM datorii_creante INNER JOIN facturi " +
                "ON datorii_creante.factura_id = facturi._id_factura WHERE datorii_creante.tip = " +
                "'Creanta'";
        String getClaimsPersonal = "SELECT datorii_creante._id_datorie_creanta AS id, " +
                "datorii_creante.termen_limita AS termenlimita, datorii_creante.valoare AS valoare, " +
                "datorii_creante.data_achitat AS dataachitat, personal.nume AS nume FROM datorii_creante INNER JOIN personal " +
                "ON datorii_creante.persoana_id = personal._id_personal WHERE datorii_creante.tip = " +
                "'Creanta'";
        String getDebtsFacturiQuery = "SELECT datorii_creante._id_datorie_creanta AS id, " +
                "datorii_creante.termen_limita as termenlimita, datorii_creante.valoare AS valoare, " +
                "datorii_creante.data_achitat AS dataachitat, facturi.contractant AS contractant FROM datorii_creante INNER JOIN facturi " +
                "ON datorii_creante.factura_id = facturi._id_factura WHERE datorii_creante.tip = " +
                "'Datorie'";
        String getDebtsPersonalQuery = "SELECT datorii_creante._id_datorie_creanta AS id, " +
                "datorii_creante.termen_limita AS termenlimita, datorii_creante.valoare AS valoare, " +
                "datorii_creante.data_achitat AS dataachitat, personal.nume AS nume FROM datorii_creante INNER JOIN personal " +
                "ON datorii_creante.persoana_id = personal._id_personal WHERE datorii_creante.tip = " +
                "'Datorie'";
        try{
            ResultSet getFacturiClaimsResultSet = conn.createStatement().executeQuery(getClaimsFacturiQuery);
            while(getFacturiClaimsResultSet.next()){
                claimsObsList.add(new Claims_Debts(getFacturiClaimsResultSet.getInt("id"), "Factura",
                        getFacturiClaimsResultSet.getDate("termenlimita"), getFacturiClaimsResultSet.getFloat("valoare"),
                        getFacturiClaimsResultSet.getDate("dataachitat"), getFacturiClaimsResultSet.getString("contractant")));
            }
            ResultSet getPersonalClaimsResultSet = conn.createStatement().executeQuery(getClaimsPersonal);
            while(getPersonalClaimsResultSet.next()){
                claimsObsList.add(new Claims_Debts(getPersonalClaimsResultSet.getInt("id"), "Persoana",
                        getPersonalClaimsResultSet.getDate("termenlimita"), getPersonalClaimsResultSet.getFloat("valoare"),
                        getPersonalClaimsResultSet.getDate("dataachitat"), getPersonalClaimsResultSet.getString("nume")));
            }
            //DEBTS
            ResultSet getFacturiDebtsResultSet = conn.createStatement().executeQuery(getDebtsFacturiQuery);
            while(getFacturiDebtsResultSet.next()){
                debtsObsList.add(new Claims_Debts(getFacturiDebtsResultSet.getInt("id"), "Factura",
                        getFacturiDebtsResultSet.getDate("termenlimita"), getFacturiDebtsResultSet.getFloat("valoare"),
                        getFacturiDebtsResultSet.getDate("dataachitat"), getFacturiDebtsResultSet.getString("contractant")));
            }
            ResultSet getPersonalDebtsResultSet = conn.createStatement().executeQuery(getDebtsPersonalQuery);
            while(getPersonalDebtsResultSet.next()){
                debtsObsList.add(new Claims_Debts(getPersonalDebtsResultSet.getInt("id"), "Persoana",
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

    //SEARCH CLAIMS DEBTS
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
                +"facturi.denumire_marfa, assets.cantitate_stock, facturi.pret, assets.cantitate_stock * facturi.pret AS valoareActive, "
                +"assets.depozit_id FROM assets INNER JOIN facturi ON assets._id_asset = facturi.activ_id WHERE facturi.tip_marfa <> 'Mijloc Fix' AND "
                +"facturi.tip_intrare_iesire = 'Intrare' AND facturi.tip_marfa <> 'Depozit'";
        String currentAssetsSearchQuery = " AND (facturi.tip_marfa LIKE '%" + currentAssetsSearchTextField.getText() +"%' OR assets._id_asset LIKE '%"
                + currentAssetsSearchTextField.getText() +"%' OR facturi.nr_factura LIKE '%" + currentAssetsSearchTextField.getText() + "%' OR "
                +"facturi.contractant LIKE '%" + currentAssetsSearchTextField.getText() +"%' OR facturi.data LIKE '%" + currentAssetsSearchTextField.getText() + "%' OR "
                + "facturi.denumire_marfa LIKE '%" + currentAssetsSearchTextField.getText() + "%' OR assets.cantitate_stock LIKE '%" +currentAssetsSearchTextField.getText()
                +"%' OR facturi.pret LIKE '%" + currentAssetsSearchTextField.getText() + "%' OR assets.cantitate_stock * facturi.pret LIKE '%"
                + currentAssetsSearchTextField.getText() + "%' OR assets.depozit_id LIKE '%" +currentAssetsSearchTextField.getText() +"%')";
        try{
            ResultSet otherAssetsResultSet = conn.createStatement().executeQuery(checkForCurrentAssetsQuery + currentAssetsSearchQuery);
            while(otherAssetsResultSet.next()){
                otherAssetsObservableList.add(new Assets(otherAssetsResultSet.getInt("_id_asset"), otherAssetsResultSet.getString("nr_factura"),
                        otherAssetsResultSet.getString("contractant"), otherAssetsResultSet.getDate("data"), otherAssetsResultSet.getString("tip_marfa"),
                        otherAssetsResultSet.getString("denumire_marfa"), otherAssetsResultSet.getFloat("cantitate_stock"), otherAssetsResultSet.getFloat("pret"),
                        otherAssetsResultSet.getFloat("valoareActive"), otherAssetsResultSet.getInt("depozit_id")));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        loadOtherAssetsInTable();
    }

    private void searchMijloaceFixeInDB(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        mijloaceFixeObservableList.clear();
        String mijloaceFixeQuery = "SELECT assets._id_asset, facturi.nr_factura, facturi.contractant, "
                + "facturi.data, facturi.denumire_marfa AS denumire, assets.cantitate_stock, assets.cantitate_stock * facturi.pret AS valoareActiv FROM "
                +"facturi INNER JOIN assets ON facturi.activ_id = assets._id_asset WHERE facturi.tip_marfa = 'Mijloc Fix' AND facturi.tip_intrare_iesire = 'Intrare'";
        String mijloaceFixeSearchQuery = " AND (assets._id_asset LIKE '%" + mijloaceFixeSearchTextField.getText() +"%' OR facturi.nr_factura LIKE '%"
                + mijloaceFixeSearchTextField.getText() + "%' OR facturi.contractant LIKE '%" + mijloaceFixeSearchTextField.getText() + "%' OR "
                +"facturi.data LIKE '%" + mijloaceFixeSearchTextField.getText() +"%' OR facturi.denumire_marfa LIKE '%" + mijloaceFixeSearchTextField.getText() +"%' OR "
                +"assets.cantitate_stock LIKE '%" + mijloaceFixeSearchTextField.getText() + "%' OR assets.cantitate_stock * facturi.pret LIKE '%"
                + mijloaceFixeSearchTextField.getText() + "%')";
        try{
            ResultSet mijloaceFixeResultSet = conn.createStatement().executeQuery(mijloaceFixeQuery + mijloaceFixeSearchQuery);
            while (mijloaceFixeResultSet.next()) {
                mijloaceFixeObservableList.add(new Assets(mijloaceFixeResultSet.getInt("_id_asset"), mijloaceFixeResultSet.getString("nr_factura"),
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
                + terenuriSearchTextField.getText() +"%' OR stare LIKE '%" + terenuriSearchTextField.getText() + "%'";
        try{
            ResultSet rs_terenuri = conn.createStatement().executeQuery(terenuriQuery +terenuriSearchQuery);
            while(rs_terenuri.next()){
                terenuriObservableList.add(new Assets(rs_terenuri.getInt("_id_teren"), rs_terenuri.getInt("teren_grup"),
                        rs_terenuri.getString("nr_cadastral"), rs_terenuri.getFloat("suprafata"), rs_terenuri.getString("localitate"),
                        rs_terenuri.getString("stare")));
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
        String getDepoziteQuery = "SELECT _id_depozit, localitate, stare FROM depozite";
        String depoziteSearchQuery = " WHERE _id_depozit LIKE '%" + depoziteSearchTextField.getText() + "%' OR localitate LIKE '%"
                + depoziteSearchTextField.getText() + "%' OR stare LIKE '%" + depoziteSearchTextField.getText() + "%'";
        try{
            ResultSet rs_depozite = conn.createStatement().executeQuery(getDepoziteQuery + depoziteSearchQuery);
            while(rs_depozite.next()){
                depoziteObservableList.add(new Assets(rs_depozite.getInt("_id_depozit"),rs_depozite.getString("localitate"),
                        rs_depozite.getString("stare")));
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
        stareTerenuriTableColumn.setCellValueFactory(new PropertyValueFactory<>("stare"));
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
        stareDepoziteTableColumn.setCellValueFactory(new PropertyValueFactory<>("stare"));
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
    public void addAssetButtonOnAction() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("New_Asset_FXML.fxml"));
        Stage addAssetStage = new Stage(StageStyle.DECORATED);
        addAssetStage.setScene(new Scene(root));
        addAssetStage.setTitle("Add asset");
        addAssetStage.getIcons().add(iconIMG);
        addAssetStage.show();
    }

    public void changeAssetButtonOnAction() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("Change_Asset_Stage.fxml"));
        Stage changeAssetStage = new Stage(StageStyle.DECORATED);
        changeAssetStage.setScene(new Scene(root));
        changeAssetStage.setTitle("Change Asset");
        changeAssetStage.getIcons().add(iconIMG);
        changeAssetStage.show();
    }

    public void deleteAssetButtonOnAction() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("Delete_Asset_Stage.fxml"));
        Stage DeleteAssetStage = new Stage(StageStyle.DECORATED);
        DeleteAssetStage.setScene(new Scene(root));
        DeleteAssetStage.setTitle("Delete Asset");
        DeleteAssetStage.getIcons().add(iconIMG);
        DeleteAssetStage.show();
    }
    
    //PRODUCTS BUTTONS
    public void addProdusButtonOnAction() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("New_Product.fxml"));
        Stage addProduct = new Stage(StageStyle.DECORATED);
        addProduct.setScene(new Scene(root));
        addProduct.setTitle("Produs nou");
        addProduct.getIcons().add(iconIMG);
        addProduct.show();
    }

    public void deleteProductButtonOnAction() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("Delete_Product.fxml"));
        Stage deleteProductStage = new Stage(StageStyle.DECORATED);
        deleteProductStage.setScene(new Scene(root));
        deleteProductStage.setTitle("Stergerea produsului");
        deleteProductStage.getIcons().add(iconIMG);
        deleteProductStage.show();
    }

    public void changeProductButtonOnAction() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("Change_Product.fxml"));
        Stage changeProductStage = new Stage(StageStyle.DECORATED);
        changeProductStage.setScene(new Scene(root));
        changeProductStage.setTitle("Modificare Produs");
        changeProductStage.getIcons().add(iconIMG);
        changeProductStage.show();
    }

    public void sellProductButtonOnAction() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("Sell_Product.fxml"));
        Stage sellProduct = new Stage(StageStyle.DECORATED);
        sellProduct.setScene(new Scene(root));
        sellProduct.setTitle("Comercializarea produsului");
        sellProduct.getIcons().add(iconIMG);
        sellProduct.show();
    }

    //CLAIMS & DEBTS BUTTONS
    public void addDebtClaimButtonOnAction() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("Creante_Datorii.fxml"));
        Stage addDebtClaim = new Stage(StageStyle.DECORATED);
        addDebtClaim.setScene(new Scene(root));
        addDebtClaim.setTitle("Creanta/Datorie noua");
        addDebtClaim.getIcons().add(iconIMG);
        addDebtClaim.show();
    }

    public void changeDebtClaimButtonOnAction() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("Change_Debts_Claims.fxml"));
        Stage changeDebtClaim = new Stage(StageStyle.DECORATED);
        changeDebtClaim.setScene(new Scene(root));
        changeDebtClaim.setTitle("Modificare Creanta / Datorie");
        changeDebtClaim.getIcons().add(iconIMG);
        changeDebtClaim.show();
    }

    public void deleteDebtClaimButtonOnAction() throws  IOException{
        Parent root = FXMLLoader.load(getClass().getResource("Delete_Debt_Claim.fxml"));
        Stage deleteDebtClaim = new Stage(StageStyle.DECORATED);
        deleteDebtClaim.setScene(new Scene(root));
        deleteDebtClaim.setTitle("Stergere Creanta / Datorie");
        deleteDebtClaim.getIcons().add(iconIMG);
        deleteDebtClaim.show();
    }

    //COSTS BUTTONS
    public void addCostButtonOnAction() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("New_Costs.fxml"));
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene(root));
        stage.setTitle("Costuri de productie noi");
        stage.getIcons().add(iconIMG);
        stage.show();
    }

    public void changeCostButtonOnAction() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("Costs_Change.fxml"));
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene(root));
        stage.setTitle("Modificarea costurilor");
        stage.getIcons().add(iconIMG);
        stage.show();
    }

    public void deleteCostButtonOnAction() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("Costs_Delete.fxml"));
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene(root));
        stage.setTitle("Stergerea costurilor");
        stage.getIcons().add(iconIMG);
        stage.show();
    }


    //INVOICES BUTTONS
    public void newInvoiceButton(){
        newInvoicePane.setVisible(true);
        FadeTransition ft = new FadeTransition(Duration.millis(500), newInvoicePane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    public void closeNewInvoiceButtonOnAction(){
        newInvoicePane.setOpacity(0);
        newInvoicePane.setVisible(false);
    }

    private void addNewInvoice() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("New_Invoice.fxml"));
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene(root));
        stage.setTitle("Factura noua");
        stage.getIcons().add(iconIMG);
        stage.show();
    }

    public void changeInvoiceButtonOnAction() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("Change_Invoice.fxml"));
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene(root));
        stage.setTitle("Modificarea facturii");
        stage.getIcons().add(iconIMG);
        stage.show();
    }

    public void deleteInvoiceButtonOnAction() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("Delete_Invoice.fxml"));
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene(root));
        stage.setTitle("Stergerea Facturilor");
        stage.getIcons().add(iconIMG);
        stage.show();
    }

    public void staffButtonsOnAction(ActionEvent e) throws IOException{
        if(e.getSource() == addStaffButton){
            loadStaffStages("New_Staff.fxml", "Angajat nou");
        }else if(e.getSource() == changeStaffButton){
            loadStaffStages("Change_Employee.fxml", "Modifica datele despre angajat");
        }else if(e.getSource() == deleteStaffButton){
            loadStaffStages("Delete_Employee.fxml", "Stergerea angajatului");
        } else if(e.getSource() == allowAccesButton){
            loadStaffStages("Allow_Access.fxml", "Permite Acces");
        } else if(e.getSource() == changePasswordButton){
            loadStaffStages("Change_Password.fxml", "Modifica Parola / Restrictioneaza accesul");
        }
    }



    public void loadStaffStages(String fileName, String stageTitle) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource(fileName));
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(new Scene(root));
        stage.setTitle(stageTitle);
        stage.getIcons().add(iconIMG);
        stage.show();
    }



    //MENU BUTTONS
    public void menuButtonsOnAction(ActionEvent e){
        Stream.of(dashboardButton,transactionButton,incomesButton,costsButton,productsButton,assetsButton,
                claimsButton, staffButton).forEach(Button -> Button.setStyle("menuButton"));
        if(e.getSource() == dashboardButton){
            Runnable getCostsRunnable = () -> Platform.runLater(this::pieChartCosts);
            new Thread(getCostsRunnable).start();
            getStatisticsFromDb();
            ProductsBarChart();
            costsLineChartFunction();
            dashboardAnchor.toFront();
            dashboardButton.setStyle("-fx-text-fill: #cfcb5b; -fx-background-color: rgba(89,50,15,0.2)");
        }
        else if(e.getSource() == transactionButton){
            transactionAnchor.toFront();
            transactionButton.setStyle("-fx-text-fill: #cfcb5b; -fx-background-color: rgba(89,50,15,0.2)");
            transactionOnAction();
        }
        else if(e.getSource() == incomesButton){

            incomesAnchor.toFront();
            incomesButton.setStyle("-fx-text-fill: #cfcb5b; -fx-background-color: rgba(89,50,15,0.2)");
            getIncomesFromDb();
        }
        else if(e.getSource() == costsButton){
            costsAnchor.toFront();
            costsButton.setStyle("-fx-text-fill: #cfcb5b; -fx-background-color: rgba(89,50,15,0.2)");
            getCostsFromDb();
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
        else if(e.getSource() == staffButton){
            staffAnchorPane.toFront();
            staffButton.setStyle("-fx-text-fill: #cfcb5b; -fx-background-color: rgba(89,50,15,0.2)");
            getStaffFromDb();
        }
    }

}
