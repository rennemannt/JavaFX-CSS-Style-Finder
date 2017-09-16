package org.rennemann.javafx.stylefinder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

    private final Set<String> uniqueStyles;

    @FXML
    private TextField directoryTextField;

    @FXML
    private Button browseButton;

    @FXML
    private Button findStylesButton;

    @FXML
    private TextArea outputTextArea;

    @FXML
    private TextArea uniqueStylesTextArea;

    /**
     * Construct.
     */
    public FXMLController() {
        uniqueStyles = new HashSet<>();
    }

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
                    findFilesByExt(directoryTextField.getText(), ".fxml");
                } catch (IOException ex) {
                    Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    /**
     * Find all files in directory and sub-directory.
     *
     * @param dir Parent directory to start search for files
     * @param extension File extension to filter by
     * @throws IOException
     */
    private void findFilesByExt(String dir, String extension) throws IOException {
        Path start = Paths.get(dir);
        int maxDepth = 20;
        // clear the styles grouped by file text area
        outputTextArea.clear();
        try (Stream<Path> stream = Files.find(start,
                maxDepth,
                (path, attr) -> String.valueOf(path).endsWith(extension))) {
            stream.forEach(path -> findUniqueStyles(path));
            uniqueStylesTextArea.setText(uniqueStyles.stream()
                    .sorted()
                    .collect(Collectors.joining("\n")));
        } catch (Exception e) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Find the CSS styles within a given file.
     *
     * @param filePath The path to a files
     */
    private void findUniqueStyles(Path filePath) {
        try {
            StyleClassDomReader reader = new StyleClassDomReader(filePath.toFile());
            outputTextArea.appendText(filePath.getFileName() + "\n");
            reader.find().stream().sorted().forEach(style -> {
                uniqueStyles.add(style);
                outputTextArea.appendText("\t" + style + "\n");
            });
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
