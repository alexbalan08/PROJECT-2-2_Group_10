
module UI {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.datatransfer;
    requires java.desktop;

    requires org.apache.httpcomponents.core5.httpcore5;
    requires com.google.gson;
    requires se.michaelthelin.spotify;

    opens UI to javafx.fxml;
    exports UI;
}