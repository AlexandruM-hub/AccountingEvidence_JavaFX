package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("fxmlAndControllers/login.fxml"));
        primaryStage.setScene(new Scene(root, 989, 583));
        Image iconIMG = new Image("file:D:\\Lucrari de laborator\\Semestrul 3\\Teza de an\\TezaProiect\\src\\css\\tractor.png");
        primaryStage.getIcons().add(iconIMG);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
