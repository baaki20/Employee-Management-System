module Employee.Management.System {
    requires javafx.controls;
    requires javafx.fxml;

    opens main to javafx.fxml;
    opens model to javafx.base;

    exports main;
    exports model;
}