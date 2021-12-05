package sample.fxmlAndControllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.DatabaseConnection;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class Change_Employee_Controller implements Initializable {

    @FXML
    private ComboBox<Integer> idPersoanaComboBox;
    @FXML
    private Text invalidIdPersoana;
    @FXML
    private TextField numeTextField, prenumeTextField, idnpTextField, telefonTextField, emailTextField, salariuTextField, functieTextField;
    @FXML
    private DatePicker dataNasteriiDatePicker, dataAngajariiDatePicker;
    @FXML
    private Text invalidNume, invalidPrenume, invalidIDNP, invalidTelefon, invalidDataNastere, invalidEmail;
    @FXML
    private Text invalidDataAngajare, invalidSalariu, invalidFunctie;
    @FXML
    private Pane succesPane;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getIdPersoane();
        idPersoanaComboBox.getSelectionModel().selectedItemProperty().addListener(((observableValue, integer, t1) -> getDataAboutPerson()));
    }

    private void getIdPersoane() {
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getIdQuery = "SELECT _id_personal from personal order by _id_personal";
        try {
            ResultSet getIdResultSet = conn.createStatement().executeQuery(getIdQuery);
            while (getIdResultSet.next()) {
                idPersoanaComboBox.getItems().add(getIdResultSet.getInt("_id_personal"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getDataAboutPerson(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getDataAboutPersonQuery = "SELECT nume, prenume, functie, IDNP, telefon, salariu, email," +
                " DATE_FORMAT(data_nastere, '%d.%m.%Y') as datanastere, DATE_FORMAT(data_angajare, '%d.%m.%Y') " +
                "as dataangajare FROM personal where _id_personal = "+ idPersoanaComboBox.getValue();
        try{
            ResultSet getDataAboutResultSet = conn.createStatement().executeQuery(getDataAboutPersonQuery);
            if(getDataAboutResultSet.next()){
                numeTextField.setText(getDataAboutResultSet.getString("nume"));
                prenumeTextField.setText(getDataAboutResultSet.getString("prenume"));
                idnpTextField.setText(getDataAboutResultSet.getString("IDNP"));
                telefonTextField.setText(getDataAboutResultSet.getString("telefon"));
                emailTextField.setText(getDataAboutResultSet.getString("email"));
                salariuTextField.setText(getDataAboutResultSet.getString("salariu"));
                functieTextField.setText(getDataAboutResultSet.getString("functie"));
                dataAngajariiDatePicker.setValue(LocalDate.parse(getDataAboutResultSet.getString("dataangajare"), DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                dataNasteriiDatePicker.setValue(LocalDate.parse(getDataAboutResultSet.getString("datanastere"), DateTimeFormatter.ofPattern("dd.MM.yyyy")));

            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void updateDataInDb(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String updateQuery = "UPDATE personal set nume = ?, prenume = ?, functie = ?," +
                "IDNP = ?, telefon = ?, salariu = ?, data_nastere = ?, email = ?, data_angajare = ? " +
                "where _id_personal = " + idPersoanaComboBox.getValue();
        try{
            PreparedStatement preparedStatement = conn.prepareStatement(updateQuery);
            preparedStatement.setString(1,numeTextField.getText());
            preparedStatement.setString(2,prenumeTextField.getText());
            preparedStatement.setString(3, functieTextField.getText());
            preparedStatement.setString(4,idnpTextField.getText());
            preparedStatement.setString(5, telefonTextField.getText());
            preparedStatement.setFloat(6, Float.parseFloat(salariuTextField.getText()));
            preparedStatement.setDate(7, Date.valueOf(dataNasteriiDatePicker.getValue()));
            preparedStatement.setString(8,emailTextField.getText());
            preparedStatement.setDate(9, Date.valueOf(dataAngajariiDatePicker.getValue()));
            preparedStatement.execute();
            successInsertion();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private boolean validation(){
        String emailPattern = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`" +
                "{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|" +
                "\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])" +
                "?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                "){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\" +
                "x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+))";
        if(idPersoanaComboBox.getSelectionModel().isEmpty()){
            invalidIdPersoana.setText("Alegeti id-ul la persoana");
            return false;
        } else if(!numeTextField.getText().matches("[a-zA-Z.]{3,}")){
            invalidNume.setText("Nume invalid");
            return false;
        } else if(!prenumeTextField.getText().matches("[a-zA-Z.]{3,}")){
            invalidPrenume.setText("Prenume invalid");
            return false;
        } else if(!idnpTextField.getText().matches("[0-9]{13}")){
            invalidIDNP.setText("IDNP incorect");
            return false;
        } else if(!telefonTextField.getText().matches("0[0-9]{8}|[0-9]{8}")){
            invalidTelefon.setText("Numar de telefon invalid");
            return false;
        } else if(!emailTextField.getText().matches(emailPattern)){
            invalidEmail.setText("Email invalid");
            return false;
        } else if (dataNasteriiDatePicker.getEditor().getText().isBlank()){
            invalidDataNastere.setText("Alegeti data nasterii");
            return false;
        } else if(dataAngajariiDatePicker.getEditor().getText().isBlank()){
            invalidDataAngajare.setText("Alegeti data angajarii");
            return false;
        } else if(salariuTextField.getText().isBlank()){
            invalidSalariu.setText("Alegeti salariul");
            return false;
        } else if(!functieTextField.getText().matches("[a-zA-Z.]{4,}")){
            invalidFunctie.setText("Format incorect. Functia este obligatorie");
            return false;
        } else{
            try{
                dataNasteriiDatePicker.getConverter().fromString(dataNasteriiDatePicker.getEditor().getText());
            } catch (DateTimeParseException e){
                invalidDataNastere.setText("Format data invalid");
                return false;
            }
            try {
                dataAngajariiDatePicker.getConverter().fromString(dataAngajariiDatePicker.getEditor().getText());
            } catch (DateTimeParseException e){
                invalidDataAngajare.setText("Format data invalid");
                return false;
            }
            try{
                Float.parseFloat(salariuTextField.getText());
            } catch (NumberFormatException e){
                invalidSalariu.setText("Format salariu invalid");
                return false;
            }
            if(getIDNP()){
                invalidIDNP.setText("Persoana deja exista!");
                return false;
            }
        }
        return true;
    }

    private boolean getIDNP(){
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.getConnection();
        String getIDNP = "SELECT IDNP from personal where _id_personal <> "+idPersoanaComboBox.getValue();
        try{
            ResultSet idnpResultSet = conn.createStatement().executeQuery(getIDNP);
            while(idnpResultSet.next()){
                if(idnpTextField.getText().equals(idnpResultSet.getString("IDNP"))){
                    return true;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    private void successInsertion(){
        succesPane.setVisible(true);
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
        visiblePause.setOnFinished(event -> succesPane.setVisible(false));
        visiblePause.play();
    }

    public void saveButtonOnAction(){
        Stream.of(invalidNume, invalidPrenume, invalidIDNP, invalidTelefon, invalidDataNastere, invalidEmail,
                invalidDataAngajare, invalidSalariu, invalidFunctie, invalidIdPersoana).forEach(text -> text.setText(""));
        if(validation()){
            updateDataInDb();
        }
    }

    public void cancelButtonOnAction(){
        Stage stage = (Stage) idnpTextField.getScene().getWindow();
        stage.close();
    }
}
