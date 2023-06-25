
module UI {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.datatransfer;
    requires java.desktop;
    requires se.michaelthelin.spotify;
    requires org.apache.httpcomponents.core5.httpcore5;
    requires com.google.gson;
    requires org.slf4j;
    requires com.fasterxml.jackson.databind;
    requires org.apache.pdfbox;
    requires org.json;
    requires org.bytedeco.opencv;
    requires jep;
    requires org.bytedeco.ffmpeg;
    //requires opencv;


    exports backend.Skills.WeatherData;

    opens UI to javafx.fxml;
    exports UI;
    exports backend.Skills;
}