package UI;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    private Image icon = new Image("file:src/main/resources/UI/blue-eye-original-icon.png");
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

        ImageView iconView = new ImageView(icon);
        iconView.setFitWidth(icon.getWidth() * 0.2);
        iconView.setFitHeight(icon.getHeight() * 0.2);

        Image backgroundImage = new Image("file:src/main/resources/UI/dark-fog-background.png");
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(3000 * 0.4, 2673 * 0.4, false, false, false, false));

        textUser = new TextField();
        textUser.setStyle("-fx-background-color: rgba(115, 188, 224, 0.2); -fx-text-fill: white; -fx-font: Courier New");
        textUser.setFont(new Font("Courier New", 15));
        textUser.setMinHeight(40);
        textUser.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                outputUserMessage(textUser.getText());
                textUser.clear();
            }
        });

        Image sendIcon = new Image("file:src/main/resources/UI/send-icon.png");
        ImageView sendIconView = new ImageView(sendIcon);
        sendIconView.setFitWidth(sendIcon.getWidth() * 0.6);
        sendIconView.setFitHeight(sendIcon.getHeight() * 0.6);

        sendIconView.setOnMouseClicked(me -> {
            outputUserMessage(textUser.getText());
            textUser.clear();
        });

        HBox textUserSend = new HBox(5);
        textUserSend.getChildren().addAll(textUser, sendIconView);
        textUserSend.setAlignment(Pos.BOTTOM_CENTER);

        StackPane eyeIcon = new StackPane(iconView);
        eyeIcon.setAlignment(Pos.TOP_CENTER);
        VBox eyeAndConversation = new VBox(eyeIcon, conversation);
        eyeAndConversation.setMargin(conversation, new Insets(10, 0, 10, 0));
        eyeAndConversation.setMargin(eyeIcon, new Insets(0, 0, 0, 0));

        //VBox iconChatTextUser = new VBox(eyeAndConversation, textUserSend);
        //paneCenter.setMargin(iconChatTextUser, new Insets(0, 5, 5, 5));
        //iconChatTextUser.setMargin(textUser, new Insets(5, 5, 5, 5));
        //iconChatTextUser.setMargin(textUserSend, new Insets(2, 2, 4, 2));
        // eyeAndConversation.setMinHeight(scene.getHeight() - 48);

        outputMessage("Hello, how can I help you?");

        stage.getIcons().add(icon);
        StackPane pane = new StackPane();
        pane.setBackground(new Background(background));
        pane.setMinWidth(3000 * 0.4);
        StackPane stackPane = new StackPane(pane, paneCenter);
        paneCenter.setMinWidth(scene.getWidth() * 1);
        paneCenter.setMaxWidth(scene.getWidth() * 1);
        //paneCenter.setMinHeight(scene.getHeight());

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
        text.setFont(Font.font("Courier New", 16));
        ImageView iconView;
        HBox iconText = new HBox(5);
        if (userMessage) {
            Image accountIcon = new Image("file:src/main/resources/UI/account-icon.png");
            iconView = new ImageView(accountIcon);
            iconView.setFitWidth(16);
            iconView.setFitHeight(16);
            iconText.getChildren().addAll(text, iconView);
            iconText.setAlignment(Pos.CENTER_RIGHT);
        } else {
            iconView = new ImageView(icon);
            iconView.setFitWidth(icon.getWidth() * 0.05);
            iconView.setFitHeight(icon.getHeight() * 0.05);
            iconText.getChildren().addAll(iconView, text);
            System.out.println("Small icon: " + iconView.getFitHeight());
        }
        conversation.getChildren().add(iconText);
    }

    public static void main(String[] args) {
        launch();
    }
}