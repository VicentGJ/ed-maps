module cujae.edmaps {
    requires javafx.controls;
    requires javafx.fxml;
    requires LinkedGraph;
    requires org.jetbrains.annotations;


    opens cujae.edmaps to javafx.fxml;
    exports cujae.edmaps;
    exports cujae.edmaps.ui;
    opens cujae.edmaps.ui to javafx.fxml;
    exports cujae.edmaps.utils;
    opens cujae.edmaps.utils to javafx.fxml;
}