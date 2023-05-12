package faceRecog;

import UI.LogInPage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;
import org.opencv.core.*;

import org.opencv.face.FaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import java.awt.image.BufferedImage;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

public class HelloCV extends Application {

    private ImageView imageView;
    private CascadeClassifier faceDetector;
    private FaceRecognizer faceRecognizer;

    public static void main(String[] args) {
        Loader.load(opencv_java.class);
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }

    private void processCameraFeed() {
        VideoCapture capture = new VideoCapture(0);

        if (!capture.isOpened()) {
            System.out.println("Failed to open the camera.");
            return;
        }

        Mat frame = new Mat();
        while (true) {
            if (capture.read(frame)) {
                // Convert the frame to grayscale
                Mat grayFrame = new Mat();
                Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

                // Detect faces in the frame
                MatOfRect faceDetection = new MatOfRect();
                faceDetector.detectMultiScale(grayFrame, faceDetection, 1.1, 3, 0, new Size(30, 30), new Size());

                // Perform face recognition on each detected face
                for (Rect rect : faceDetection.toArray()) {
                    Mat face = grayFrame.submat(rect);

                    // Resize the face image to a fixed size
                    Mat resizedFace = new Mat();
                    Imgproc.resize(face, resizedFace, new Size(100, 100));

                    // Perform face recognition on the resized face image
                    IntBuffer labelBuffer = IntBuffer.allocate(1);
                    DoubleBuffer confidenceBuffer = DoubleBuffer.allocate(1);
                    faceRecognizer.predict(resizedFace, labelBuffer.array(), confidenceBuffer.array());

                    int predictedLabel = labelBuffer.get(0);
                    double confidence = confidenceBuffer.get(0);

                    // Draw a bounding box around the detected face
                    Imgproc.rectangle(frame, rect.tl(), rect.br(), new Scalar(0, 255, 0), 2);

                    // Display the recognized label and confidence
                    String label = "Unknown";
                    if (confidence < 70) {
                        label = "Person " + predictedLabel;
                    }
                    Imgproc.putText(frame, label, new Point(rect.x, rect.y - 10), Imgproc.FONT_HERSHEY_SIMPLEX, 0.9, new Scalar(0, 255, 0), 2);
                }

                // Convert the frame to JavaFX Image for display
//                Image image = matToJavaFXImage(frame);
                Image image = LogInPage.matToJavaFXImage(frame, 1);

                // Update the ImageView with the JavaFX Image
                Platform.runLater(() -> imageView.setImage(image));
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

    }

//    private Image matToJavaFXImage(Mat frame) {
//        int width = frame.width();
//        int height = frame.height();
//        int channels = frame.channels();
//        byte[] sourcePixels = new byte[width * height * channels];
//        frame.get(0, 0, sourcePixels);
//
//        WritableImage image = new WritableImage(width, height);
//        javafx.scene.image.PixelWriter pixelWriter = image.getPixelWriter();
//
//        if (channels == 1) {
//            // Grayscale image
//            for (int y = 0; y < height; y++) {
//                for (int x = 0; x < width; x++) {
//                    int grayValue = sourcePixels[y * width + x] & 0xFF;
//                    javafx.scene.paint.Color color = javafx.scene.paint.Color.rgb(grayValue, grayValue, grayValue);
//                    pixelWriter.setColor(x, y, color);
//                }
//            }
//        } else if (channels == 3) {
//            // BGR image
//            for (int y = 0; y < height; y++) {
//                for (int x = 0; x < width; x++) {
//                    int index = (y * width + x) * channels;
//                    int blue = sourcePixels[index] & 0xFF;
//                    int green = sourcePixels[index + 1] & 0xFF;
//                    int red = sourcePixels[index + 2] & 0xFF;
//                    javafx.scene.paint.Color color = javafx.scene.paint.Color.rgb(red, green, blue);
//                    pixelWriter.setColor(x, y, color);
//                }
//            }
//        }
//
//        return image;
//    }
}
