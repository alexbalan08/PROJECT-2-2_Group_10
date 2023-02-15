package com.dacs.digitalassistent;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.awt.*;
//import com.sun.javafx.tk.Toolkit;

import java.io.IOException;

public class HelloApplication extends Application {

    private Image icon = new Image("C:\\Users\\vales\\Documents\\Java Projects\\DigitalAssistent\\src\\main\\java\\com\\dacs\\digitalassistent\\blue-eye-original-icon.png");
    private VBox conversation = new VBox();

    @Override
    public void start(Stage stage) throws IOException {
        HBox root = new HBox();
        VBox paneLeft = new VBox(10);
        VBox paneCenter = new VBox();
        VBox paneRight = new VBox();
        root.getChildren().addAll(paneLeft, paneCenter, paneRight);
        stage.setFullScreen(false);
        /*Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
        Scale scale = new Scale(resolution.getWidth()/1280, resolution.getHeight()/720);
        root.getTransforms().add(scale);*/
        Scene scene = new Scene(root, 650, 650, Color.rgb(5, 5, 15));
        // FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        // Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("DAC°Search");

        ImageView iconView = new ImageView(icon);
        iconView.setFitWidth(icon.getWidth() * 0.2);
        iconView.setFitHeight(icon.getHeight() * 0.2);

        Image restartIcon = new Image("C:\\Users\\vales\\Documents\\Java Projects\\DigitalAssistent\\src\\main\\java\\com\\dacs\\digitalassistent\\restart-icon.png");
        ImageView restartIconView = new ImageView(restartIcon);
        restartIconView.setFitWidth(restartIcon.getWidth() * 0.1);
        restartIconView.setFitHeight(restartIcon.getHeight() * 0.1);

        Image backgroundImage = new Image("C:\\Users\\vales\\Documents\\Java Projects\\DigitalAssistent\\src\\main\\java\\com\\dacs\\digitalassistent\\fog-dark.png");
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1, 1, true, true, false, false));

        VBox iconTop = new VBox(iconView);
        iconTop.setAlignment(Pos.CENTER);
        VBox iconTopTextBottom = new VBox(iconTop, conversation);
        iconTopTextBottom.setMargin(conversation, new Insets(15, 10, 10, 10));
        iconTopTextBottom.setMargin(iconTop, new Insets(5, 0, 0, 0));
        paneLeft.setMargin(restartIconView, new Insets(15, 0, 0, 0));

        outputMessage("Hello, how can I help you?");

        stage.getIcons().add(icon);
        paneLeft.setMinWidth(scene.getWidth() * 0.1);
        paneLeft.setAlignment(Pos.TOP_CENTER);
        paneCenter.setMinWidth(scene.getWidth() * 0.8);
        paneCenter.setBackground(new Background(background));
        paneCenter.getChildren().add(iconTopTextBottom);
        paneLeft.getChildren().add(restartIconView);
        stage.setFullScreenExitHint("");
        stage.setScene(scene);

        stage.show();
    }

    public void outputMessage(String message) {
        Text text = new Text();
        text.setText(message);
        text.setFill(Color.WHITE);
        text.setFont(Font.font("Courier New", 15));
        ImageView iconView = new ImageView(icon);
        iconView.setFitWidth(icon.getWidth() * 0.05);
        iconView.setFitHeight(icon.getHeight() * 0.05);
        HBox iconText = new HBox(iconView, text);
        iconText.setMargin(text, new Insets(0, 0, 0, 5));
        conversation.getChildren().add(iconText);
        conversation.setMargin(iconText, new Insets(5, 0, 5, 0));
    }

    public static void main(String[] args) {
        launch();
    }
}