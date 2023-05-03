package faceRecog;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;


public class HelloCV {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat womanFace = Imgcodecs.imread("src/main/resources/faceImages/womanFace.png");

        CascadeClassifier faceDetector = new CascadeClassifier("C:/opencv/sources/data/haarcascades/haarcascade_frontalface_alt.xml");

        MatOfRect faceDetection = new MatOfRect();
        faceDetector.detectMultiScale(womanFace, faceDetection);

        System.out.println(String.format("Detected faces: %d", faceDetection.toArray().length));

        for (Rect rect : faceDetection.toArray()) {
            Imgproc.rectangle(
                    womanFace,
                    new Point(rect.x, rect.y),
                    new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 0, 255),
                    3
            );
        }

        Imgcodecs.imwrite("src/main/resources/faceImages/womanFaceOutput.png", womanFace);
        System.out.println("Done");
    }
}
