module com.uniproject.oop {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.uniproject.oop to javafx.fxml;
    exports com.uniproject.oop;
}
