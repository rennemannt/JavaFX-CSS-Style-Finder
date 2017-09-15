package org.rennemann.javafx.stylefinder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javax.xml.parsers.ParserConfigurationException;
import org.rennemann.javafx.stylefinder.readers.StyleClassDomReader;
import org.xml.sax.SAXException;

public class FXMLController implements Initializable {

    @FXML
    private TextField directoryTextField;

    @FXML
    private Button browseButton;

    @FXML
    private Button findStylesButton;

    @FXML
    private TextArea outputTextArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        browseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File selectedDirectory = directoryChooser.showDialog(browseButton.getScene().getWindow());

                if (selectedDirectory == null) {
                    directoryTextField.setText("no directory selected");
                } else {
                    directoryTextField.setText(selectedDirectory.getAbsolutePath());
                }
            }
        });

        findStylesButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    findFilesByExt(directoryTextField.getText(), "*.fxml");
                } catch (IOException ex) {
                    Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void findFilesByExt(String dir, String extension) throws IOException {
        final Path p = Paths.get(dir);
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(p, extension)) {
            stream.forEach(path -> findUniqueStyles(path));
        }
    }

    private void findUniqueStyles(Path filePath) {
        try {
            StyleClassDomReader reader = new StyleClassDomReader(filePath.toFile());
            outputTextArea.appendText(filePath.getFileName() + "\n");
            for (String style : reader.find()) {
                outputTextArea.appendText("\t" + style + "\n");
            }
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
