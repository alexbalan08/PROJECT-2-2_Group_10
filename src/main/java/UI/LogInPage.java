package UI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;
import org.opencv.core.*;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;
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
    private boolean faceRecognition = false;
    private boolean recognizedFace = false;
    private boolean logIn = true;
    private StackPane topPane;
    private Label message;
    private VBox content;
    private Button signUp;
    private StackPane cameraPane;
    private HBox nameFinish;
    private Label name;
    private TextField nameInfo;
    private HBox registerName;
    private Button cancel;
    private Button finish;
    private FaceRecognizer faceRecognizer;

    public static void main(String[] args) {
        Loader.load(opencv_java.class);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        main = new HelloApplication();
        primaryStage.setTitle("EYE°Sistant");

        this.primaryStage = primaryStage;

        Image eyeimage= new Image("file:src\\main\\resources\\login\\login\\img.png");

        ImageView topEye =new ImageView(eyeimage);
        topEye.setFitWidth(eyeimage.getWidth() * 0.27);
        topEye.setFitHeight(eyeimage.getHeight() * 0.27);
        topEye.setSmooth(true);

        // Add the login button
        signUp = new Button("Sign Up");
        signUp.setFont(Font.font(font, 14));
        signUp.setStyle("-fx-background-color: rgba(55, 180, 220, 0.15); -fx-text-fill: white");

        topPane = new StackPane(topEye, signUp);
        topPane.setAlignment(signUp, javafx.geometry.Pos.BOTTOM_RIGHT);
        topPane.setMargin(topEye, new Insets(topEye.getFitHeight() * 0.3, 0, 0, 0));
        topPane.setMargin(signUp, new Insets(0, 44, 0, 0));

        StackPane backgroundPane = new StackPane();
        backgroundPane.setBackground(new Background(main.images.background()));
        backgroundPane.setMinWidth(3000 * 0.4);

        imageView = new ImageView();

        cameraPane = new StackPane(imageView);

        message = new Label();
        message.setText("Approach your face to log in.");
        message.setFont(Font.font(font, 16));
        message.setTextFill(Color.WHITE);

        name = new Label("Name:");
        name.setFont(Font.font(font, 1));
        name.setTextFill(Color.TRANSPARENT);
        nameInfo = new TextField();
        nameInfo.setFont(Font.font(font, 1));
        nameInfo.setStyle("-fx-background-color: rgba(55, 180, 220, 0.0); -fx-text-fill: white");

        registerName = new HBox(10);
        registerName.getChildren().addAll(name, nameInfo);
        registerName.setMargin(name, new Insets(5, 0, 0, 0));

        // Creating cancel and finish buttons
        cancel = new Button("Cancel");
        cancel.setFont(Font.font(font, 1));
        cancel.setStyle("-fx-background-color: rgba(55, 180, 220, 0.0); -fx-text-fill: transparent");

        finish = new Button("Finish");
        finish.setFont(Font.font(font, 1));
        finish.setStyle("-fx-background-color: rgba(55, 180, 220, 0.0); -fx-text-fill: transparent");

        HBox cancelFinish = new HBox(10);
        cancelFinish.getChildren().addAll(cancel, finish);

        nameFinish = new HBox(171);
        nameFinish.getChildren().addAll(registerName, cancelFinish);

        content = new VBox(20);
        content.getChildren().addAll(topPane, nameFinish, cameraPane, message);
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(backgroundPane, content);
        stackPane.setAlignment(signUp, Pos.BOTTOM_RIGHT);
        content.setMargin(message, new Insets(0, 0, 0, topEye.getFitHeight() * 0.7));

        signUp.setOnAction(event -> signUp());

        Scene scene = new Scene(stackPane, 600, 600);

        primaryStage.setScene(scene);

        primaryStage.getIcons().add(new Image("file:src\\main\\resources\\login\\login\\img.png"));

        primaryStage.show();

        if (faceRecognition) {
            // Load the face recognizer model
            faceRecognizer = LBPHFaceRecognizer.create();
            faceRecognizer.read("C:\\opencv\\sources\\samples\\dnn\\models.yml"); // path/to/face_recognizer_model.yml
        }

        startCapture();
    }

    private void signUp() {
        logIn = false;

        signUp.setStyle("-fx-background-color: rgba(55, 180, 220, 0.0); -fx-text-fill: transparent");

        name.setFont(Font.font(font, 16));
        name.setTextFill(Color.WHITE);

        nameInfo.setFont(Font.font(font, 16));
        nameInfo.setStyle("-fx-background-color: rgba(55, 180, 220, 0.15); -fx-text-fill: white");

        cancel.setFont(Font.font(font, 14));
        cancel.setStyle("-fx-background-color: rgba(55, 180, 220, 0.15); -fx-text-fill: white");

        finish.setFont(Font.font(font, 14));
        finish.setStyle("-fx-background-color: rgba(55, 180, 220, 0.15); -fx-text-fill: white");

        message.setText("Slowly move your face in all directions");

        content.setMargin(nameFinish, new Insets(0, 0, 0, 44));

        cancel.setOnAction(event -> {
            logIn = true;

            name.setFont(Font.font(font, 1));
            name.setTextFill(Color.TRANSPARENT);

            nameInfo.setText("");
            nameInfo.setFont(Font.font(font, 1));
            nameInfo.setStyle("-fx-background-color: rgba(55, 180, 220, 0.0); -fx-text-fill: transparent");

            cancel.setFont(Font.font(font, 1));
            cancel.setStyle("-fx-background-color: rgba(55, 180, 220, 0.0); -fx-text-fill: transparent");

            finish.setFont(Font.font(font, 1));
            finish.setStyle("-fx-background-color: rgba(55, 180, 220, 0.0); -fx-text-fill: transparent");

            message.setText("Approach your face to log in.");

            signUp.setStyle("-fx-background-color: rgba(55, 180, 220, 0.15); -fx-text-fill: white");
        });
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
                if (logIn) logIn(rect, grayFrame, frame);
                else signUp();
            }

            // Convert the frame to JavaFX Image for display
            Image image = matToJavaFXImage(frame, 0.8);

            Platform.runLater(() -> imageView.setImage(image));
        }
    }

    public void logIn(Rect rect, Mat grayFrame, Mat frame) {
        if (faceRecognition) faceRecognition(rect, grayFrame, frame);
        else recognizedFace = true;

        if (recognizedFace) {
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
    }

    private void trainFaceRecognizer() {
        List<Mat> faceImages = new ArrayList<>();
        List<Integer> faceLabels = new ArrayList<>();

        // Add face images and labels for each person
        // For example:
        // faceImages.add(person1FaceImage1);
        // faceLabels.add(0); // Assign label 0 to person 1
        // faceImages.add(person1FaceImage2);
        // faceLabels.add(0); // Assign label 0 to person 1
        // faceImages.add(person2FaceImage1);
        // faceLabels.add(1); // Assign label 1 to person 2
        // ...

        FaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();

        MatOfInt labelsMat = new MatOfInt();
        labelsMat.fromList(faceLabels);

        faceRecognizer.train(faceImages, labelsMat);
    }

    private void faceRecognition(Rect rect, Mat grayFrame, Mat frame) {
        Mat face = grayFrame.submat(rect);

        // Resize the face image to a fixed size
        Mat resizedFace = new Mat();
        Imgproc.resize(face, resizedFace, new Size(100, 100));

        // Perform face recognition on the resized face image
        int[] labelBuffer = new int[1];
        double[] confidenceBuffer = new double[1];
        faceRecognizer.predict(resizedFace, labelBuffer, confidenceBuffer);

        int predictedLabel = labelBuffer[0];
        double confidence = confidenceBuffer[0];

        // Display the recognized label and confidence
        String label = "Unknown";
        if (confidence < 70) {
            recognizedFace = true;
            label = "Person " + predictedLabel;
        }

        Imgproc.putText(frame, label, new Point(rect.x, rect.y - 10), Imgproc.FONT_HERSHEY_SIMPLEX, 0.9, new Scalar(0, 255, 0), 2);
    }
    public static Image matToJavaFXImage(Mat frame, double scalar) {
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
