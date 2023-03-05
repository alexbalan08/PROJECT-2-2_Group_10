package UI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class LogInPage extends Application {
    private String font = "Courier New";
    private HelloApplication main;



    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        main = new HelloApplication();
        primaryStage.setTitle("EYE°Sistant");


        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);

        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(25, 25, 25, 25));


        Label userName = new Label("User Name:");
        userName.setFont(Font.font(font, 20));
        userName.setTextFill(Color.WHITE);


        grid.add(userName, 0, 1);
        TextField userTextField = new TextField();
        userTextField.setStyle("-fx-background-color: rgba(115, 188, 224, 0.2); -fx-text-fill: white");
        grid.add(userTextField, 1, 1);
        Label pw = new Label("Password:");
        pw.setFont(Font.font(font, 20));
        pw.setTextFill(Color.WHITE);
        grid.add(pw, 0, 2);
        PasswordField pwBox = new PasswordField();
        pwBox.setStyle("-fx-background-color: rgba(115, 188, 224, 0.2); -fx-text-fill: white");
        grid.add(pwBox, 1, 2);



        // Add the login button
        Button btn = new Button("Login");
        btn.setFont(Font.font(font));
        btn.setStyle("-fx-background-color: rgba(115, 188, 224, 0.2); -fx-text-fill: white");
        HBox hbBtn = new HBox(20);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        // Add a label for displaying login status
        final Label message = new Label();
        grid.add(message, 1, 6);


        btn.setOnAction(event -> {
            String userNameValue = userTextField.getText();
            String passwordValue = pwBox.getText();
            if (userNameValue.equals("team10") && passwordValue.equals("dacs")) {
                message.setText("Login successful!");
                primaryStage.hide();
                try {
                    main.start(primaryStage);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            } else {
                message.setText("Incorrect username or password.");
            }
            message.setFont(Font.font(font));
            message.setTextFill(Color.WHITE);
        });


        Image eyeimage= new Image("file:src\\main\\resources\\login\\login\\img.png");

        ImageView bum=new ImageView(eyeimage);
        bum.setFitWidth(eyeimage.getWidth() * 0.4);
        bum.setFitHeight(eyeimage.getHeight() * 0.4);
        bum.setSmooth(true);

        StackPane eyePane = new StackPane(bum);
        eyePane.setAlignment(Pos.TOP_CENTER);

        StackPane backgroundPane = new StackPane();
        backgroundPane.setBackground(new Background(main.images.background()));
        backgroundPane.setMinWidth(3000 * 0.4);

        StackPane stackPane = new StackPane();

        stackPane.getChildren().addAll(backgroundPane, eyePane, grid);
        stackPane.setMargin(eyePane, new Insets(bum.getFitHeight() * 0.6, 0, bum.getFitHeight() * 0.4, 0));

        Scene scene = new Scene(stackPane, 600, 600);

        primaryStage.setScene(scene);

        primaryStage.getIcons().add(new Image("file:src\\main\\resources\\login\\login\\img.png"));

        primaryStage.show();
    }
}
