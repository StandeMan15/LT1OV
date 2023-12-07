module com.example.ovapp {
    requires javafx.controls;
    requires javafx.fxml;

    //requires org.kordamp.bootstrapfx.core;
    requires javafx.base;
    requires java.desktop;

    opens com.example.ovapp to javafx.fxml;
    exports com.example.ovapp;
}