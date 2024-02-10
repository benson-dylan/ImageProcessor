module parallel.group.imageprocessor {
    requires javafx.controls;
    requires javafx.fxml;


    opens parallel.group.imageprocessor to javafx.fxml;
    exports parallel.group.imageprocessor;
}