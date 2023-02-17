package UI;
import backend.DA;
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
    private int textHeight = 16;
    private BuildImages images = new BuildImages();
    private VBox conversation = new VBox(10);
    TextField textUser;
    DA assistant = new DA();

    @Override
    public void start(Stage stage) throws IOException {
        HBox root = new HBox();
        StackPane paneCenter = new StackPane();
        stage.setFullScreen(false);
        Scene scene = new Scene(root, 650, 650);

        stage.setTitle("DAC°Search");

        textUser = new TextField();
        textUser.setStyle("-fx-background-color: rgba(115, 188, 224, 0.2); -fx-text-fill: white; -fx-font: Courier New");
        textUser.setFont(new Font("Courier New", textHeight));
        textUser.setMinHeight(40);
        textUser.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                if (textUser.getText() != "") {
                    outputUserMessage(textUser.getText());
                    outputMessage(assistant.startQuery(textUser.getText()));
                    textUser.clear();
                }
            }
        });

        images.sendIconView().setOnMouseClicked(me -> {
            if (textUser.getText() != "") {
                outputUserMessage(textUser.getText());
                outputMessage(assistant.startQuery(textUser.getText()));
                textUser.clear();
            }
        });

        HBox textUserSend = new HBox(10);
        textUserSend.getChildren().addAll(textUser, images.sendIconView());
        textUserSend.setAlignment(Pos.BOTTOM_CENTER);

        StackPane eyeIcon = new StackPane(images.eyeIconView(45));
        eyeIcon.setAlignment(Pos.TOP_CENTER);
        VBox eyeAndConversation = new VBox(eyeIcon, conversation);
        eyeAndConversation.setMargin(conversation, new Insets(10, 30, 10, 30));
        eyeAndConversation.setMargin(eyeIcon, new Insets(20, 0, 10, 0));

        paneCenter.setMargin(textUserSend, new Insets(20, 20, 20, 20));

        outputMessage("Hello, how can I help you?");

        stage.getIcons().add(images.eyeIcon);
        StackPane backgroundPane = new StackPane();
        backgroundPane.setBackground(new Background(images.background()));
        backgroundPane.setMinWidth(3000 * 0.4);
        StackPane stackPane = new StackPane(backgroundPane, paneCenter);
        paneCenter.setMinWidth(scene.getWidth());
        paneCenter.setMaxWidth(scene.getWidth());

        root.getChildren().add(stackPane);
        root.setAlignment(Pos.CENTER);
        paneCenter.getChildren().addAll(eyeAndConversation, textUserSend);
        stage.setFullScreenExitHint("");
        stage.setScene(scene);

        textUser.setPrefWidth(paneCenter.getMinWidth() - images.sendIcon.getWidth());

        stage.show();
    }

    public void outputMessage(String message) {
        if(message!="") createMessage(message, false);
    }

    public void outputUserMessage(String message) {
        createMessage(message, true);
    }

    public void createMessage(String message, boolean userMessage) {
        Text text = new Text();
        text.setText(message);
        text.setFill(Color.WHITE);
        text.setFont(Font.font("Courier New", textHeight));
        HBox iconText = new HBox(textHeight);

        if (userMessage) {
            iconText.setAlignment(Pos.CENTER_RIGHT);
            iconText.getChildren().addAll(text, images.accountIconView(textHeight));
        } else {
            iconText.getChildren().addAll(images.eyeIconView(textHeight), text);
        }

        conversation.getChildren().add(iconText);
    }

    public void eventHandlers() {

    }

    public static void main(String[] args) {
        launch();
    }


    private static class BuildImages {
        private Image eyeIcon;
        private Image backgroundImage;
        private Image sendIcon;
        private Image accountIcon;

        public BuildImages() {
            eyeIcon = new Image("file:src/main/resources/UI/blue-eye-original-icon.png");
            backgroundImage = new Image("file:src/main/resources/UI/dark-fog-background.png");
            sendIcon = new Image("file:src/main/resources/UI/send-icon.png");
            accountIcon = new Image("file:src/main/resources/UI/account-icon.png");
        }

        public ImageView eyeIconView(int eyeIconHeight) {
            double ratio = eyeIcon.getWidth() / eyeIcon.getHeight();
            ImageView eyeIconView = new ImageView(eyeIcon);
            eyeIconView.setFitWidth(ratio*eyeIconHeight);
            eyeIconView.setFitHeight(eyeIconHeight);
            return eyeIconView;
        }

        public BackgroundImage background() {
            return new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(3000 * 0.4, 2673 * 0.4, false, false, false, false));
        }

        public ImageView sendIconView() {
            ImageView sendIconView = new ImageView(sendIcon);
            sendIconView.setFitWidth(sendIcon.getWidth() * 0.6);
            sendIconView.setFitHeight(sendIcon.getHeight() * 0.6);
            return sendIconView;
        }

        public ImageView accountIconView(int accountIconHeight) {
            ImageView accountIconView = new ImageView(accountIcon);
            accountIconView.setFitWidth(accountIconHeight);
            accountIconView.setFitHeight(accountIconHeight);
            return accountIconView;
        }
    }
}