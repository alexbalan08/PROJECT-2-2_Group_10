
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

    exports backend.Skills.WeatherData;

    opens UI to javafx.fxml;
    exports UI;
}