import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;

public class Main extends Application {

    private File currentFile = null;

    @Override
    public void start(Stage stage) {
        TextArea textArea = new TextArea();
        textArea.setPromptText("Type your notes here...");

        BorderPane border = new BorderPane();
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem newFile = new MenuItem("New");
        MenuItem openFile = new MenuItem("Open");
        MenuItem saveFile = new MenuItem("Save");
        MenuItem exit = new MenuItem("Exit");
        fileMenu.getItems().addAll(newFile, openFile, saveFile, new SeparatorMenuItem(), exit);

        Menu editMenu = new Menu("Edit");
        MenuItem cut = new MenuItem("Cut");
        MenuItem copy = new MenuItem("Copy");
        MenuItem paste = new MenuItem("Paste");
        MenuItem selectAll = new MenuItem("Select All");
        editMenu.getItems().addAll(cut, copy, paste, new SeparatorMenuItem(), selectAll);

        Menu formatMenu = new Menu("Format");
        CheckMenuItem wordWrap = new CheckMenuItem("Word Wrap");
        formatMenu.getItems().addAll(wordWrap);

        menuBar.getMenus().addAll(fileMenu, editMenu, formatMenu);

        HBox statusBar = new HBox();
        Label statusLabel = new Label(" Status: Ready");
        statusBar.getChildren().add(statusLabel);
        statusBar.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 5;");

        border.setTop(menuBar);
        border.setCenter(textArea);
        border.setBottom(statusBar);

        newFile.setOnAction(e -> {
            textArea.clear();
            currentFile = null;
            stage.setTitle("Notepad - New Document");
            statusLabel.setText(" Status: New Document");
        });

        openFile.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                try {
                    textArea.setText(Files.readString(file.toPath()));
                    currentFile = file;
                    stage.setTitle("Notepad - " + file.getName());
                    statusLabel.setText(" Status: Opened " + file.getName());
                } catch (Exception ex) {
                    statusLabel.setText(" Status: Error opening file.");
                }
            }
        });

        saveFile.setOnAction(e -> {
            if (currentFile == null) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
                currentFile = fileChooser.showSaveDialog(stage);
            }

            if (currentFile != null) {
                try (PrintWriter writer = new PrintWriter(currentFile)) {
                    writer.print(textArea.getText());
                    stage.setTitle("Notepad - " + currentFile.getName());
                    statusLabel.setText(" Status: Saved " + currentFile.getName());
                } catch (Exception ex) {
                    statusLabel.setText(" Status: Error saving file.");
                }
            }
        });

        cut.setOnAction(e -> textArea.cut());
        copy.setOnAction(e -> textArea.copy());
        paste.setOnAction(e -> textArea.paste());
        selectAll.setOnAction(e -> textArea.selectAll());
        wordWrap.setOnAction(e -> textArea.setWrapText(wordWrap.isSelected()));
        exit.setOnAction(e -> stage.close());

        Scene scene = new Scene(border, 900, 700);
        stage.setTitle("Notepad - New Document");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}