module parallel.group.imageprocessor {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens parallel.group.imageprocessor to javafx.fxml;
    exports parallel.group.imageprocessor;
}