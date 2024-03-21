module org.example.socialnetwork {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;

    opens org.example.socialnetwork to javafx.fxml;
    exports org.example.socialnetwork;
    opens org.example.socialnetwork.domain;
    opens org.example.socialnetwork.controller;

}