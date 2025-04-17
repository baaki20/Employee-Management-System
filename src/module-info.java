module Employee.Management.System {
    requires javafx.controls;
    requires javafx.fxml;

    opens ui to javafx.fxml;
    opens model to javafx.base;

    exports ui;
    exports model;
}