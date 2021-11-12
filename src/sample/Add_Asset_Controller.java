package sample;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Add_Asset_Controller implements Initializable{
    @FXML
    private Button cancelAddAssetButton, saveAddAssetButton;
    @FXML
    private ComboBox<String> facturaFiscalaTip, idProduseComboBox, tipContinutComboBox;
    @FXML
    private Text produsAlegere, addAssetLocalitateText, addAssetNrCadastralText, addAssetSuprafataText;
    @FXML
    private TextField addAssetLocalitateTextField, addAssetNrCadastralTextField, addAssetSuprafataTextField;

    public void saveAddAssetButtonOnAction(ActionEvent e){

    }

    public void cancelAddAssetButtonOnAction(ActionEvent e){
        Stage stage = (Stage) cancelAddAssetButton.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        facturaFiscalaTip.getItems().addAll("Intrare","Iesire");
        tipContinutComboBox.getItems().addAll("Produse", "Marfa", "Materiale", "OMVSD", "Mijloc Fix","Depozit","Teren");
    }

    public void produsAlegereVisibility(ActionEvent e){
        if(facturaFiscalaTip.getValue().equals("Iesire")){
            tipContinutComboBox.setValue("Produse");
        }
    }

    public void addAssetChooseVisability(ActionEvent e){
        Stage stage = (Stage)((Node) e.getSource()).getScene().getWindow();
        if(tipContinutComboBox.getValue().equals("Depozit") || tipContinutComboBox.getValue().equals("Teren")){
            addAssetLocalitateText.setVisible(true);
            addAssetLocalitateTextField.setVisible(true);
        }
        else{
            addAssetLocalitateText.setVisible(false);
            addAssetLocalitateTextField.setVisible(false);
        }
        if(tipContinutComboBox.getValue().equals("Teren")){
            stage.setHeight(704);
            addAssetNrCadastralTextField.setVisible(true);
            addAssetNrCadastralText.setVisible(true);
            addAssetSuprafataText.setVisible(true);
            addAssetSuprafataTextField.setVisible(true);

        } else{
            stage.setHeight(604);
            addAssetNrCadastralTextField.setVisible(false);
            addAssetNrCadastralText.setVisible(false);
            addAssetSuprafataText.setVisible(false);
            addAssetSuprafataTextField.setVisible(false);
        }
        if(tipContinutComboBox.getValue().equals("Produse")){
            idProduseComboBox.setVisible(true);
            produsAlegere.setVisible(true);
        }
        else{
            idProduseComboBox.setVisible(false);
            produsAlegere.setVisible(false);
        }
    }

}
