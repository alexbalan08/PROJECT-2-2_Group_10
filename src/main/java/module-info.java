module com.dacs.digitalassistent {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.datatransfer;
    requires java.desktop;

    opens com.dacs.digitalassistent to javafx.fxml;
    exports com.dacs.digitalassistent;
}