package UI;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    private Image eyeIcon = new Image("file:src/main/resources/UI/blue-eye-original-icon.png");
    private VBox conversation = new VBox(10);
    TextField textUser;

    @Override
    public void start(Stage stage) throws IOException {
        HBox root = new HBox();
        StackPane paneCenter = new StackPane();
        //root.getChildren().addAll(paneLeft, paneCenter, paneRight);
        stage.setFullScreen(false);
        /*Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
        Scale scale = new Scale(resolution.getWidth()/1280, resolution.getHeight()/720);
        root.getTransforms().add(scale);*/
        Scene scene = new Scene(root, 650, 650, Color.rgb(5, 5, 15));

        // FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        // Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("DAC°Search");

        ImageView iconView = new ImageView(eyeIcon);
        iconView.setFitWidth(eyeIcon.getWidth() * 0.17);
        iconView.setFitHeight(eyeIcon.getHeight() * 0.17);

        Image backgroundImage = new Image("file:src/main/resources/UI/dark-fog-background.png");
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(3000 * 0.4, 2673 * 0.4, false, false, false, false));

        textUser = new TextField();
        textUser.setStyle("-fx-background-color: rgba(115, 188, 224, 0.2); -fx-text-fill: white; -fx-font: Courier New");
        textUser.setFont(new Font("Courier New", 15));
        textUser.setMinHeight(40);
        textUser.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                if (textUser.getText() != "") {
                    outputUserMessage(textUser.getText());
                    textUser.clear();
                }
            }
        });

        Image sendIcon = new Image("file:src/main/resources/UI/send-icon.png");
        ImageView sendIconView = new ImageView(sendIcon);
        sendIconView.setFitWidth(sendIcon.getWidth() * 0.6);
        sendIconView.setFitHeight(sendIcon.getHeight() * 0.6);

        sendIconView.setOnMouseClicked(me -> {
            if (textUser.getText() != "") {
                outputUserMessage(textUser.getText());
                textUser.clear();
            }
        });

        HBox textUserSend = new HBox(10);
        textUserSend.getChildren().addAll(textUser, sendIconView);
        textUserSend.setAlignment(Pos.BOTTOM_CENTER);

        StackPane eyeIcon = new StackPane(iconView);
        eyeIcon.setAlignment(Pos.TOP_CENTER);
        VBox eyeAndConversation = new VBox(eyeIcon, conversation);
        eyeAndConversation.setMargin(conversation, new Insets(10, 30, 10, 30));
        eyeAndConversation.setMargin(eyeIcon, new Insets(20, 0, 10, 0));

        paneCenter.setMargin(textUserSend, new Insets(20, 20, 20, 20));

        outputMessage("Hello, how can I help you?");

        stage.getIcons().add(this.eyeIcon);
        StackPane backgroundPane = new StackPane();
        backgroundPane.setBackground(new Background(background));
        backgroundPane.setMinWidth(3000 * 0.4);
        StackPane stackPane = new StackPane(backgroundPane, paneCenter);
        paneCenter.setMinWidth(scene.getWidth());
        paneCenter.setMaxWidth(scene.getWidth());

        root.getChildren().add(stackPane);
        root.setAlignment(Pos.CENTER);
        paneCenter.getChildren().addAll(eyeAndConversation, textUserSend);
        stage.setFullScreenExitHint("");
        stage.setScene(scene);

        textUser.setPrefWidth(paneCenter.getMinWidth() - sendIcon.getWidth());

        stage.show();
    }

    public void outputMessage(String message) {
        createMessage(message, false);
    }

    public void outputUserMessage(String message) {
        createMessage(message, true);
    }

    public void createMessage(String message, boolean userMessage) {
        Text text = new Text();
        text.setText(message);
        text.setFill(Color.WHITE);
        int textHeight = 16;
        text.setFont(Font.font("Courier New", textHeight));
        ImageView iconView;
        HBox iconText = new HBox(16);

        if (userMessage) {
            Image accountIcon = new Image("file:src/main/resources/UI/account-icon.png");
            iconView = new ImageView(accountIcon);
            iconText.setAlignment(Pos.CENTER_RIGHT);
            iconText.getChildren().addAll(text, iconView);
        } else {
            iconView = new ImageView(eyeIcon);
            System.out.println("Small icon: " + iconView.getFitHeight());
            iconText.getChildren().addAll(iconView, text);
        }
        double ratio = iconView.getImage().getWidth() / iconView.getImage().getHeight();

        iconView.setFitWidth(ratio*textHeight);
        iconView.setFitHeight(textHeight);

        conversation.getChildren().add(iconText);
    }

    public static void main(String[] args) {
        launch();
    }
}