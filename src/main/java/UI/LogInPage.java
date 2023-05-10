package UI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
//import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

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

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledExecutorService;

import java.io.IOException;

public class LogInPage extends Application {
    private String font = "Courier New";
    private HelloApplication main;
    private Stage primaryStage;
    private ImageView imageView;
    private VideoCapture capture;
    private ScheduledExecutorService executor;


    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        main = new HelloApplication();
        primaryStage.setTitle("EYE°Sistant");

        this.primaryStage = primaryStage;

//        GridPane grid = new GridPane();
//        grid.setAlignment(Pos.CENTER);
//
//        grid.setHgap(20);
//        grid.setVgap(20);
//        grid.setPadding(new Insets(25, 25, 25, 25));
//
//
//        Label userName = new Label("User Name:");
//        userName.setFont(Font.font(font, 20));
//        userName.setTextFill(Color.WHITE);
//
//
//        grid.add(userName, 0, 1);
//        TextField userTextField = new TextField();
//        userTextField.setStyle("-fx-background-color: rgba(55, 180, 220, 0.15); -fx-text-fill: white");
//        grid.add(userTextField, 1, 1);
//        Label pw = new Label("Password:");
//        pw.setFont(Font.font(font, 20));
//        pw.setTextFill(Color.WHITE);
//        grid.add(pw, 0, 2);
//        PasswordField pwBox = new PasswordField();
//        pwBox.setStyle("-fx-background-color: rgba(55, 180, 220, 0.15); -fx-text-fill: white");
//        grid.add(pwBox, 1, 2);
//
//        // Add the login button
//        Button btn = new Button("Login");
//        btn.setFont(Font.font(font));
//        btn.setStyle("-fx-background-color: rgba(55, 180, 220, 0.15); -fx-text-fill: white");
//        HBox hbBtn = new HBox(20);
//        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
//        hbBtn.getChildren().add(btn);
//        grid.add(hbBtn, 1, 4);
//
//        // Add a label for displaying login status
//        final Label message = new Label();
//        grid.add(message, 1, 6);
//
//
//        btn.setOnAction(event -> {
//            String userNameValue = userTextField.getText();
//            String passwordValue = pwBox.getText();
//            if (true/*userNameValue.equals("team10") && passwordValue.equals("dacs")*/) {
//                message.setText("Login successful!");
//                primaryStage.hide();
//                try {
//                    main.start(primaryStage);
//                } catch (IOException | NoSuchMethodException e) {
//                    throw new RuntimeException(e);
//                }
//
//            } else {
//                message.setText("Incorrect username or password.");
//            }
//            message.setFont(Font.font(font));
//            message.setTextFill(Color.WHITE);
//        });


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

        imageView = new ImageView();

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(backgroundPane, eyePane, imageView);
        stackPane.setMargin(eyePane, new Insets(bum.getFitHeight() * 0.6, 0, bum.getFitHeight() * 0.4, 0));

        Scene scene = new Scene(stackPane, 600, 600);

        primaryStage.setScene(scene);

        primaryStage.getIcons().add(new Image("file:src\\main\\resources\\login\\login\\img.png"));

        primaryStage.show();

        startCapture();
    }

    private void startCapture() {
        capture = new VideoCapture(0);

        if (!capture.isOpened()) {
            System.out.println("Failed to open the camera.");
            return;
        }

        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::processFrame, 0, 1, TimeUnit.MILLISECONDS);
    }

    private void processFrame() {
        Mat frame = new Mat();

        if (capture.read(frame)) {
            // Convert the frame to grayscale
            Mat grayFrame = new Mat();
            Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

            CascadeClassifier faceDetector = new CascadeClassifier("C:/opencv/sources/data/haarcascades/haarcascade_frontalface_alt.xml");

            MatOfRect faceDetection = new MatOfRect();
            faceDetector.detectMultiScale(grayFrame, faceDetection, 1.1, 3, 0, new Size(30, 30), new Size());

            System.out.println(String.format("Detected faces: %d", faceDetection.toArray().length));

            for (Rect rect : faceDetection.toArray()) {
                Imgproc.rectangle(frame, rect.tl(), rect.br(), new Scalar(0, 255, 0), 2);
            }

//            if (faceDetection.toArray().length > 0) {
////                final Label message = new Label();
////                message.setText("Login successful!");
//                try {
//                    executor.shutdown();
//                    executor.awaitTermination(500, TimeUnit.MILLISECONDS);
//
//                    if (capture.isOpened()) {
//                        capture.release();
//                    }
//                } catch (InterruptedException | IllegalStateException e) {
//                    e.printStackTrace();
//                }
////                primaryStage.hide();
////                try {
////                    main.start(primaryStage);
////                } catch (IOException | NoSuchMethodException e) {
////                    throw new RuntimeException(e);
////                }
//
//            }

//            Imgcodecs.imwrite("src/main/resources/faceImages/womanFaceOutput.png", frame);
//            System.out.println("Done");

            // Convert the frame to JavaFX Image for display
            Image image = matToJavaFXImage(frame);

            Platform.runLater(() -> imageView.setImage(image));
        }
    }

    private Image matToJavaFXImage(Mat frame) {
        int width = frame.width();
        int height = frame.height();
        int channels = frame.channels();
        byte[] sourcePixels = new byte[width * height * channels];
        frame.get(0, 0, sourcePixels);

        WritableImage image = new WritableImage(width, height);
        PixelWriter pixelWriter = image.getPixelWriter();

        if (channels == 1) {
            // Grayscale image
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int grayValue = sourcePixels[y * width + x] & 0xFF;
                    pixelWriter.setColor(x, y, javafx.scene.paint.Color.rgb(grayValue, grayValue, grayValue));
                }
            }
        } else if (channels == 3) {
            // BGR image
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int index = (y * width + x) * channels;
                    int blue = sourcePixels[index] & 0xFF;
                    int green = sourcePixels[index + 1] & 0xFF;
                    int red = sourcePixels[index + 2] & 0xFF;
                    pixelWriter.setColor(x, y, javafx.scene.paint.Color.rgb(red, green, blue));
                }
            }
        }

        return image;
    }
}
