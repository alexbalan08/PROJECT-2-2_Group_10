package com.dacs.digitalassistent;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 304, 428, Color.rgb(5, 5, 15)/*Color.color(0.05, 0.05, 0.06)*/);
        // FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        // Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("DAC°Search");
        Image icon = new Image("C:\\Users\\vales\\Documents\\Java Projects\\DigitalAssistent\\src\\main\\java\\com\\dacs\\digitalassistent\\blue-eye-icon.png");
        Image backgroundImage = new Image("C:\\Users\\vales\\Documents\\Java Projects\\DigitalAssistent\\src\\main\\java\\com\\dacs\\digitalassistent\\fog-dark.png");
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(304, 428, true, true, true, false));
        Text textInit = new Text();
        textInit.setText("Hello, how can I help you?");
        textInit.setX(350);
        textInit.setY(150);
        textInit.setFill(Color.WHITE);
        textInit.setFont(Font.font("Courier New", 20));

        stage.getIcons().add(icon);
        root.setBackground(new Background(background));
        root.getChildren().add(textInit);
        stage.setFullScreen(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}