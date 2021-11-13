package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class Add_Asset_Controller implements Initializable{
    @FXML
    private Button cancelAddAssetButton, saveAddAssetButton;
    @FXML
    private ComboBox<String> facturaFiscalaTipComboBox, idProduseComboBox, tipContinutComboBox;
    @FXML
    private ComboBox<String> idTerenComboBox, idDepozitComboBox, idMijlocFixComboBox, idMaterialComboBox, idOMVSDComboBox;
    @FXML
    private Text produsAlegere, addAssetLocalitateText, addAssetNrCadastralText, addAssetSuprafataText;
    @FXML
    private TextField addAssetLocalitateTextField, addAssetNrCadastralTextField, addAssetSuprafataTextField;
    @FXML
    private TextField addAssetNrFacturiiTextField, addAssetContractantTextField, addAssetDenumireTextField, addAssetCantitateTextField, addAssetPretTextField;
    @FXML
    private DatePicker facturaDatePicker;
    @FXML
    private Label invalidNrFacturiiLabel, invalidContractantLabel, invalidDatePickerLabel,invalidDenumireLabel;
    @FXML
    private Label invalidCantitateLabel, invalidPretLabel, invalidTipContinut, invalidIdProdus, invalidLocalitateLabel;
    @FXML
    private Label invalidNrCadastralLabel, invalidSuprafataLabel;

    // DATA VALIDATION
    public void saveAddAssetButtonOnAction(ActionEvent e){
        Stream.of(invalidNrFacturiiLabel, invalidContractantLabel, invalidDatePickerLabel,invalidDenumireLabel,
                invalidCantitateLabel, invalidPretLabel, invalidTipContinut, invalidIdProdus).forEach(label -> label.setText(""));
        generalValidation();
        if(facturaFiscalaTipComboBox.getValue().equals("Iesire")){
            if(tipContinutComboBox.getValue().equals("Produs")){
                if(generalValidation() && produsCaseValidation()){
                    System.out.println("Validare succes");
                }
            }else if(tipContinutComboBox.getValue().equals("Teren")){
                // ID la teren care este vandut
            }else if(tipContinutComboBox.getValue().equals("Depozit")){
                // ID la depozit
            }

        }else if (facturaFiscalaTipComboBox.getValue().equals("Intrare")){
            if(tipContinutComboBox.getValue().equals("Teren")){
                if(generalValidation() && terenCaseValidation()){
                    System.out.println("Validare succes");
                }
            }else if(tipContinutComboBox.getValue().equals("Depozit")){
                if(generalValidation() && depozitCaseValidation()){
                    System.out.println("Validare succes");
                }
            }else{
                if(generalValidation()){
                    System.out.println("Validare succes");
                }
            }
        }

    }

    public boolean generalValidation(){
        if(addAssetContractantTextField.getText().length()<3){
            invalidContractantLabel.setText("Sunt necesare cel putin 4 caractere");
            return false;
        }
        else if(facturaDatePicker.getEditor().getText().isBlank()){
            invalidDatePickerLabel.setText("Data este obligatorie");
            return false;
        }
        else if(tipContinutComboBox.getValue().isBlank()){
            invalidTipContinut.setText("Alegeti o optiune");
            return false;
        }
        else if(addAssetDenumireTextField.getText().length()<3){
            invalidDenumireLabel.setText("Sunt necesare cel putin 4 caractere");
            return false;
        }
        else if(addAssetCantitateTextField.getText().isBlank()){
            invalidCantitateLabel.setText("Campul nu poate fi gol");
            return false;
        }
        else if(addAssetPretTextField.getText().isBlank()){
            invalidPretLabel.setText("Campul nu poate fi gol");
            return false;
        }
        return true;
    }

    public boolean produsCaseValidation(){
        if(addAssetNrFacturiiTextField.getText().length()<5){
            invalidNrFacturiiLabel.setText("Sunt necesare cel putin 5 caractere");
            return false;
        }
        else if(idProduseComboBox.getValue().isBlank()){
            invalidIdProdus.setText("Alegeti produsul");
            return false;
        }
        return true;
    }

    public boolean terenCaseValidation(){
        if(addAssetLocalitateTextField.getText().length()<3){
            invalidLocalitateLabel.setText("Sunt necesare cel putin 3 caractere");
            return false;
        }else if(addAssetNrCadastralTextField.getText().length()<10){
            invalidNrCadastralLabel.setText("Sunt necesare cel putin 10 caractere");
            return false;
        }else if(addAssetSuprafataTextField.getText().isBlank()){
            invalidSuprafataLabel.setText("Campul nu trebuie sa fie gol");
            return false;
        }
        return true;
    }

    public boolean depozitCaseValidation(){
        if(addAssetLocalitateTextField.getText().length()<3){
            invalidLocalitateLabel.setText("Sunt necesare cel putin 3 caractere");
            return false;
        }else if(addAssetNrFacturiiTextField.getText().length()<5){
            invalidNrFacturiiLabel.setText("Sunt necesare cel putin 5 caractere");
        }
        return true;
    }


    public void cancelAddAssetButtonOnAction(ActionEvent e){
        Stage stage = (Stage) cancelAddAssetButton.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        facturaFiscalaTipComboBox.getItems().addAll("Intrare","Iesire");
        tipContinutComboBox.getItems().addAll("Produse", "Materiale", "OMVSD", "Mijloc Fix","Depozit","Teren");

    }

    public void produsAlegereVisibility(ActionEvent e){
        if(facturaFiscalaTipComboBox.getValue().equals("Iesire")){
            tipContinutComboBox.setValue("Produse");
        }
    }

    public void addAssetChooseVisability(ActionEvent e){
        Stage stage = (Stage)((Node) e.getSource()).getScene().getWindow();
        if((tipContinutComboBox.getValue().equals("Depozit") || tipContinutComboBox.getValue().equals("Teren")) && facturaFiscalaTipComboBox.getValue().equals("Intrare")){
            addAssetLocalitateText.setVisible(true);
            addAssetLocalitateTextField.setVisible(true);
        }
        else{
            addAssetLocalitateText.setVisible(false);
            addAssetLocalitateTextField.setVisible(false);
        }
        if(tipContinutComboBox.getValue().equals("Teren") && facturaFiscalaTipComboBox.getValue().equals("Intrare")){
            stage.setHeight(704);
            addAssetNrCadastralTextField.setVisible(true);
            addAssetNrCadastralText.setVisible(true);
            addAssetSuprafataText.setVisible(true);
            addAssetSuprafataTextField.setVisible(true);

        }else{
            stage.setHeight(604);
            addAssetNrCadastralTextField.setVisible(false);
            addAssetNrCadastralText.setVisible(false);
            addAssetSuprafataText.setVisible(false);
            addAssetSuprafataTextField.setVisible(false);
        }
        if(facturaFiscalaTipComboBox.getValue().equals("Iesire")){
            if(tipContinutComboBox.getValue().equals("Teren")){
                produsAlegere.setText("ID teren");
                produsAlegere.setVisible(true);
                idTerenComboBox.setVisible(true);
                Stream.of(idMaterialComboBox,idDepozitComboBox,idMijlocFixComboBox,idOMVSDComboBox,
                        idProduseComboBox).forEach(stringComboBox -> stringComboBox.setVisible(false));
            }else if(tipContinutComboBox.getValue().equals("Depozit")){
                produsAlegere.setText("ID Depozit");
                produsAlegere.setVisible(true);
                idDepozitComboBox.setVisible(true);
                Stream.of(idMaterialComboBox,idMijlocFixComboBox,idOMVSDComboBox,
                        idProduseComboBox,idTerenComboBox).forEach(stringComboBox -> stringComboBox.setVisible(false));
            }else if(tipContinutComboBox.getValue().equals("Mijloc Fix")){
                produsAlegere.setText("ID Mijloc Fix");
                produsAlegere.setVisible(true);
                idMijlocFixComboBox.setVisible(true);
                Stream.of(idMaterialComboBox,idDepozitComboBox,idOMVSDComboBox,
                        idProduseComboBox,idTerenComboBox).forEach(stringComboBox -> stringComboBox.setVisible(false));
            }else if(tipContinutComboBox.getValue().equals("OMVSD")){
                produsAlegere.setText("ID OMVSD");
                produsAlegere.setVisible(true);
                idOMVSDComboBox.setVisible(true);
                Stream.of(idMaterialComboBox,idDepozitComboBox,idMijlocFixComboBox,
                        idProduseComboBox,idTerenComboBox).forEach(stringComboBox -> stringComboBox.setVisible(false));
            }else if(tipContinutComboBox.getValue().equals("Materiale")){
                produsAlegere.setText("ID Material");
                produsAlegere.setVisible(true);
                idMaterialComboBox.setVisible(true);
                Stream.of(idDepozitComboBox,idMijlocFixComboBox,idOMVSDComboBox,
                        idProduseComboBox,idTerenComboBox).forEach(stringComboBox -> stringComboBox.setVisible(false));
            }else{
                produsAlegere.setText("ID Produs");
                produsAlegere.setVisible(true);
                idProduseComboBox.setVisible(true);
                Stream.of(idMaterialComboBox,idDepozitComboBox,idMijlocFixComboBox,idOMVSDComboBox,
                        idTerenComboBox).forEach(stringComboBox -> stringComboBox.setVisible(false));
            }
        }else {
            produsAlegere.setVisible(false);
            Stream.of(idMaterialComboBox,idDepozitComboBox,idMijlocFixComboBox,idOMVSDComboBox,
                    idProduseComboBox,idTerenComboBox).forEach(stringComboBox -> stringComboBox.setVisible(false));
        }
    }

}
