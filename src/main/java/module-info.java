module cujae.edmaps {
    requires javafx.controls;
    requires javafx.fxml;


    opens cujae.edmaps to javafx.fxml;
    exports cujae.edmaps;
}