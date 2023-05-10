package UI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledExecutorService;

import java.io.IOException;

public class LogInPage extends Application {
    private String font = "Courier New";
    private Stage primaryStage;
    private HelloApplication main;
    private ImageView imageView;
    private VideoCapture capture;
    private ScheduledExecutorService executor;
    private boolean windowOpened = false;
    private boolean faceRecognition = true;

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        main = new HelloApplication();
        primaryStage.setTitle("EYE°Sistant");

        this.primaryStage = primaryStage;

        Image eyeimage= new Image("file:src\\main\\resources\\login\\login\\img.png");

        ImageView bum=new ImageView(eyeimage);
        bum.setFitWidth(eyeimage.getWidth() * 0.27);
        bum.setFitHeight(eyeimage.getHeight() * 0.27);
        bum.setSmooth(true);

        StackPane eyePane = new StackPane(bum);
        eyePane.setAlignment(Pos.TOP_CENTER);

        StackPane backgroundPane = new StackPane();
        backgroundPane.setBackground(new Background(main.images.background()));
        backgroundPane.setMinWidth(3000 * 0.4);

        imageView = new ImageView();

        StackPane cameraPane = new StackPane(imageView);
        eyePane.setAlignment(Pos.CENTER);

        Label message = new Label();
        message.setText("Approach your face to log in.");
        message.setFont(Font.font(font, 16));
        message.setTextFill(Color.WHITE);

        VBox content = new VBox(20);
        content.getChildren().addAll(eyePane, cameraPane, message);
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(backgroundPane, content);
        content.setMargin(eyePane, new Insets(bum.getFitHeight() * 0.3, 0, 0, 0));
        content.setMargin(message, new Insets(0, 0, 0, bum.getFitHeight() * 0.7));

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

//            System.out.println(String.format("Detected faces: %d", faceDetection.toArray().length));

            for (Rect rect : faceDetection.toArray()) {
                Imgproc.rectangle(frame, rect.tl(), rect.br(), new Scalar(0, 255, 0), 2);
                if (!windowOpened) {
                    windowOpened = true;

                    // Open a new JavaFX window when a face is detected
                    Platform.runLater(() -> {
                        try {
                            openNewWindow();
                        } catch (IOException | NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }

            // Convert the frame to JavaFX Image for display
            Image image = matToJavaFXImage(frame, 0.8);

            Platform.runLater(() -> imageView.setImage(image));
        }
    }

    private Image matToJavaFXImage(Mat frame, double scalar) {
        int width = frame.width();
        int height = frame.height();
        int channels = frame.channels();
        byte[] sourcePixels = new byte[width * height * channels];
        frame.get(0, 0, sourcePixels);

        int targetWidth = (int) (width * scalar);
        int targetHeight = (int) (height * scalar);

        WritableImage image = new WritableImage(targetWidth, targetHeight);
        PixelWriter pixelWriter = image.getPixelWriter();

        if (channels == 1) {
            // Grayscale image
            for (int y = 0; y < targetHeight; y++) {
                for (int x = 0; x < targetWidth; x++) {
                    int sourceX = (int) (x * ((double) width / targetWidth));
                    int sourceY = (int) (y * ((double) height / targetHeight));
                    int grayValue = sourcePixels[sourceY * width + sourceX] & 0xFF;
                    pixelWriter.setColor(x, y, javafx.scene.paint.Color.rgb(grayValue, grayValue, grayValue));
                }
            }
        } else if (channels == 3) {
            // BGR image
            for (int y = 0; y < targetHeight; y++) {
                for (int x = 0; x < targetWidth; x++) {
                    int sourceX = (int) (x * ((double) width / targetWidth));
                    int sourceY = (int) (y * ((double) height / targetHeight));
                    int index = (sourceY * width + sourceX) * channels;
                    int blue = sourcePixels[index] & 0xFF;
                    int green = sourcePixels[index + 1] & 0xFF;
                    int red = sourcePixels[index + 2] & 0xFF;
                    pixelWriter.setColor(x, y, javafx.scene.paint.Color.rgb(red, green, blue));
                }
            }
        }

        return image;
    }

    private void openNewWindow() throws IOException, NoSuchMethodException {
        // Create a new instance of HelloApplication and show the window
        Stage stage = new Stage();
        main.start(stage);

        // Close the initial window
        primaryStage.close();
    }
}
