module com.example.productui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.productui to javafx.fxml;

    exports com.example.productui;
    exports com.example.productui.model;
}