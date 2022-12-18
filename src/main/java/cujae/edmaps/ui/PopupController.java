package cujae.edmaps.ui;

import cujae.edmaps.utils.ViewLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class PopupController {

    @FXML
    Text text;
    @FXML
    Label label;

    private String previousFXML, previousTitle;

    public void setText(String labelText, String mainText) {
        text.setText(mainText);
        label.setText(labelText);
    }

    public void setPrevious(String fxml, String title) {
        this.previousFXML = fxml;
        this.previousTitle = title;
    }

    @FXML
    private void onOkButton(ActionEvent event) throws IOException {
        ViewLoader.newWindow(getClass().getResource(previousFXML), previousTitle, null);
        ViewLoader.closeWindow(event);
    }
}
