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
import javafx.stage.FileChooser;
import javax.xml.parsers.ParserConfigurationException;
import org.rennemann.javafx.stylefinder.matchers.CssStyleMatcher;
import org.rennemann.javafx.stylefinder.readers.StyleClassDomReader;
import org.xml.sax.SAXException;

public class FXMLController implements Initializable {

    private final Set<String> uniqueFxmlStyles;

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

    @FXML
    private TextField cssFileTextField;

    @FXML
    private Button cssBrowseButton;

    @FXML
    private Button analyzeButton;

    @FXML
    private TextArea usedStylesTextArea;

    @FXML
    private TextArea unusedStylesTextArea;

    /**
     * Construct.
     */
    public FXMLController() {
        uniqueFxmlStyles = new HashSet<>();
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

        cssBrowseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select a JavaFX CSS File to Analyze");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JavaFX CSS File", "*.css"));
                File selectedFile = fileChooser.showOpenDialog(cssBrowseButton.getScene().getWindow());

                if (selectedFile == null) {
                    cssFileTextField.setText("no file selected");
                } else {
                    cssFileTextField.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        analyzeButton.setOnAction((ActionEvent event)
                -> analyzeCssStyles(cssFileTextField.getText()));
    }

    /**
     * Analyze the CSS styles and compare against the unique list of styles
     * pulled from FXML files.
     *
     * @param filePath The absolute path to a CSS file
     */
    private void analyzeCssStyles(String filePath) {
        Path path = Paths.get(filePath);
        StringBuilder sb = new StringBuilder();
        try {
            Files.lines(path).forEach(line -> sb.append(line));
        } catch (IOException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
        Set<String> cssStyles = CssStyleMatcher.find(sb.toString());
        setCssStyles(cssStyles);
    }

    /**
     * Set the analyzed text areas with used and unused CSS styles.
     *
     * @param cssStyles The distinct list of styles extracted from the CSS file
     */
    private void setCssStyles(Set<String> cssStyles) {
        Set<String> usedStyles = new HashSet<>();
        Set<String> unusedStyles = new HashSet<>();
        cssStyles.forEach((cssStyle) -> {
            if (uniqueFxmlStyles.contains(cssStyle)) {
                usedStyles.add(cssStyle);
            } else {
                unusedStyles.add(cssStyle);
            }
        });
        usedStylesTextArea.setText(usedStyles.stream().sorted().collect(Collectors.joining("\n")));
        unusedStylesTextArea.setText(unusedStyles.stream().sorted().collect(Collectors.joining("\n")));
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
            uniqueStylesTextArea.setText(uniqueFxmlStyles.stream()
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
                uniqueFxmlStyles.add(style);
                outputTextArea.appendText("\t" + style + "\n");
            });
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
