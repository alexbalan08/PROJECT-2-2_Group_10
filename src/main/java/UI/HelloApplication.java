package UI;
import backend.DA;
import backend.SkillEditor;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TextArea;
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
import java.lang.reflect.InvocationTargetException;

public class HelloApplication extends Application {
    private int sceneSize = 650;
    private int textHeight = 16;
    private String font = "Courier New";
    protected BuildImages images = new BuildImages();
    private VBox conversation = new VBox(10);
    public TextArea textArea;
    DA assistant = new DA();

    public HelloApplication() throws IOException, NoSuchMethodException {
        textArea = new TextArea();
        assistant.instantiateSkillEditor(textArea);
    }

    @Override
    public void start(Stage stage) throws IOException, NoSuchMethodException {
        HBox root = new HBox();
        Scene scene = new Scene(root, sceneSize, sceneSize);
        scene.getStylesheets().add("file:src/main/resources/hello.css");

        stage.getIcons().add(images.eyeIcon);
        stage.setTitle("EYE°Sistant");

        VBox eyeAndConversation = createEyeAndConversation();

        HBox textFieldSend = createTextAreaSend();

        StackPane paneCenter = createPaneCenter(eyeAndConversation, textFieldSend);

        outputBotMessage("Hello DACStudent, how can I help you?");

        StackPane backgroundPane = new StackPane();
        backgroundPane.setBackground(new Background(images.background()));
        backgroundPane.setMinWidth(3000 * 0.4);

        root.getChildren().add(new StackPane(backgroundPane, paneCenter));
        root.setAlignment(Pos.CENTER);
        stage.setScene(scene);

        textArea.setPrefWidth(paneCenter.getMinWidth() - images.sendIcon.getWidth());

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
        text.setWrappingWidth(sceneSize * 0.55);
        text.setText(message);
        text.setFill(Color.WHITE);
        text.setFont(Font.font("Courier New", textHeight));
        HBox iconText = new HBox(textHeight);
        StackPane iconPane;

        if (userMessage) {
            text.setTextAlignment(TextAlignment.RIGHT);
            iconText.setAlignment(Pos.CENTER_RIGHT);
            iconPane = new StackPane(images.accountIconView(textHeight));
            iconText.getChildren().addAll(text, iconPane);
            iconText.setMargin(text, new Insets(0, 5, 0 ,0));
        } else {
            iconPane = new StackPane(images.eyeIconView(textHeight));
            iconText.getChildren().addAll(iconPane, text);
        }

        iconPane.setAlignment(Pos.TOP_CENTER);
        conversation.getChildren().add(iconText);
        conversation.setMargin(iconText, new Insets(5, 0, 5 ,0));
    }

    public void sendMessageEventHandler() throws IOException, InvocationTargetException, IllegalAccessException {
        if (textArea.getText().strip() != "") {
            String text = textArea.getText().strip();
            outputUserMessage(text);
            textArea.clear();
            outputBotMessage(assistant.startQuery(text));
        }
    }

    public VBox createEyeAndConversation() {
        ImageView eyeIconView = images.eyeIconView(45);
        StackPane eyeIcon = new StackPane(eyeIconView);
        eyeIcon.setAlignment(Pos.TOP_CENTER);
        eyeIcon.setOnMouseClicked(me -> {
            System.out.println("EYE PRESSED!");
            outputBotMessage("THE EYE ICON IS AT POSITION X: " + sceneSize / 2 + " AND Y: " + eyeIconView.getY());
        });

        VBox eyeAndConversation = new VBox(eyeIcon, conversation);
        eyeAndConversation.setMargin(eyeIcon, new Insets(25, 0, 15, 0));
        eyeAndConversation.setMargin(conversation, new Insets(10, 30, 10, 30));

        return eyeAndConversation;
    }

    public HBox createTextAreaSend() throws NoSuchMethodException {
        textArea.setStyle("-fx-text-fill: white; -fx-font: " + font);
        // Design for scroll bar in text area
        textArea.skinProperty().addListener(new ChangeListener<Skin<?>>() {
            @Override
            public void changed(
                    ObservableValue<? extends Skin<?>> ov, Skin<?> t, Skin<?> t1) {
                if (t1 != null && t1.getNode() instanceof Region) {
                    Region r = (Region) t1.getNode();
                    r.getChildrenUnmodifiable().stream().filter(n -> n instanceof Region).map(n -> (Region) n).forEach(n -> n.setBackground(Background.EMPTY));
                    r.getChildrenUnmodifiable().stream().filter(n -> n instanceof Control).map(n -> (Control) n).forEach(c -> c.skinProperty().addListener(this));
                }
            }
        });

        textArea.setMaxHeight((textHeight + (textHeight * 1.5)));
        textArea.setFont(new Font(font, textHeight));
        textArea.setMinHeight(textHeight + (textHeight * 1.5));
        textArea.setOnKeyPressed(ke -> {
            int maxNewLines = 4;
            String text = textArea.getText().strip();
            if (ke.getCode().equals(KeyCode.ENTER)) {
                SkillEditor skillEditor;
                try { skillEditor = new SkillEditor(textArea);
                } catch (NoSuchMethodException | IOException e) { throw new RuntimeException(e); }
                skillEditor.setQuery(text);
                if (skillEditor.isQueryToEditSkill() && skillEditor.entry.getValue().getName().equals("addSkill")) {
                    if (textArea.getMaxHeight() <= (textHeight + (textHeight * 1.5)) + maxNewLines * (textHeight + 3)) textArea.setMaxHeight(textArea.getMaxHeight() + (textHeight + 3));
                } else if (!text.equals("")) {
                    System.out.println(text + "yey");
                    try {
                        textArea.setMaxHeight((textHeight + (textHeight * 1.5)));
                        sendMessageEventHandler();
                    } catch (IOException | InvocationTargetException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (ke.getCode().equals(KeyCode.BACK_SPACE)) {
                int numNewLines = (int) text.chars().filter(num -> num == '\n').count();
                if (numNewLines <= maxNewLines) textArea.setMaxHeight((textHeight + (textHeight * 1.5)) + numNewLines * (textHeight + 3));
            }
        });

        ImageView sendIconView = images.sendIconView();
        sendIconView.setOnMouseClicked(me -> {
            try {
                textArea.setMaxHeight((textHeight + (textHeight * 1.5)));
                sendMessageEventHandler();
            } catch (IOException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        HBox textFieldSend = new HBox(10);
        textFieldSend.getChildren().addAll(textArea, sendIconView);
        textFieldSend.setAlignment(Pos.BOTTOM_CENTER);

        return textFieldSend;
    }

    public StackPane createPaneCenter(VBox eyeAndConversation, HBox textFieldSend) {
        StackPane paneCenter = new StackPane();
        paneCenter.setMinWidth(sceneSize);
        paneCenter.setMaxWidth(sceneSize);
        paneCenter.setMargin(textFieldSend, new Insets(20, 20, 25, 20));
        paneCenter.getChildren().addAll(eyeAndConversation, textFieldSend);

        return paneCenter;
    }

    public static void main(String[] args) throws IOException {
        launch();
    }

    protected static class BuildImages {
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