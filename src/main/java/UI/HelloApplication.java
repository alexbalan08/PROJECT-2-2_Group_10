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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private int textHeight = 16;
    private String font = "Courier New";
    private BuildImages images = new BuildImages();
    private VBox conversation = new VBox(10);
    TextField textField;
    DA assistant = new DA();

    @Override
    public void start(Stage stage) throws IOException {
        HBox root = new HBox();
        StackPane paneCenter = new StackPane();
        stage.setFullScreen(false);
        Scene scene = new Scene(root, 650, 650);

        stage.getIcons().add(images.eyeIcon);
        stage.setTitle("DAC°Search");

        StackPane eyeIcon = new StackPane(images.eyeIconView(45));
        eyeIcon.setAlignment(Pos.TOP_CENTER);

        VBox eyeAndConversation = new VBox(eyeIcon, conversation);
        eyeAndConversation.setMargin(eyeIcon, new Insets(25, 0, 15, 0));
        eyeAndConversation.setMargin(conversation, new Insets(10, 30, 10, 30));

        HBox textFieldSend = createTextFieldSend();
        textFieldSend.setAlignment(Pos.BOTTOM_CENTER);

        paneCenter.setMinWidth(scene.getWidth());
        paneCenter.setMaxWidth(scene.getWidth());
        paneCenter.setMargin(textFieldSend, new Insets(20, 20, 20, 20));

        outputBotMessage("Hello, how can I help you?");

        StackPane backgroundPane = new StackPane();
        backgroundPane.setBackground(new Background(images.background()));
        backgroundPane.setMinWidth(3000 * 0.4);
        StackPane stackPane = new StackPane(backgroundPane, paneCenter);

        root.getChildren().add(stackPane);
        root.setAlignment(Pos.CENTER);
        paneCenter.getChildren().addAll(eyeAndConversation, textFieldSend);
        stage.setFullScreenExitHint("");
        stage.setScene(scene);

        textField.setPrefWidth(paneCenter.getMinWidth() - images.sendIcon.getWidth());

        stage.show();
    }

    public void outputBotMessage(String message) {
        if(message != "") createMessage(message, false);
    }

    public void outputUserMessage(String message) {
        createMessage(message, true);
    }

    public void createMessage(String message, boolean userMessage) {
        Text text = new Text();
        text.setWrappingWidth(250);
        text.setText(message);
        text.setFill(Color.WHITE);
        text.setFont(Font.font("Courier New", textHeight));
        HBox iconText = new HBox(textHeight);



        if (userMessage) {
            text.setTextAlignment(TextAlignment.RIGHT);
            iconText.setAlignment(Pos.CENTER_RIGHT);
            iconText.getChildren().addAll(text, images.accountIconView(textHeight));
            iconText.setMargin(text, new Insets(0, 5, 0 ,0));
        } else {
            iconText.getChildren().addAll(images.eyeIconView(textHeight), text);
        }

        conversation.getChildren().add(iconText);
    }

    public void sendMessageEventHandler() {
        if (textField.getText() != "") {
            outputUserMessage(textField.getText());
            outputBotMessage(assistant.startQuery(textField.getText()));
            textField.clear();
        }
    }

    public HBox createTextFieldSend() {
        textField = new TextField();
        textField.setStyle("-fx-background-color: rgba(115, 188, 224, 0.2); -fx-text-fill: white; -fx-font: " + font);
        textField.setFont(new Font(font, textHeight));
        textField.setMinHeight(textHeight + (textHeight * 1.5));
        textField.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) sendMessageEventHandler();
        });

        ImageView sendIconView = images.sendIconView();
        sendIconView.setOnMouseClicked(me -> sendMessageEventHandler());

        HBox textFieldSend = new HBox(10);
        textFieldSend.getChildren().addAll(textField, sendIconView);
        return textFieldSend;
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