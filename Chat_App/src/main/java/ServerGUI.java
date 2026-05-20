import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;

public class ServerGUI extends Application {

    private VBox chatBox = new VBox(10);

    private TextField messageField =
            new TextField();

    private ScrollPane scrollPane =
            new ScrollPane(chatBox);

    private ServerBackend server;

    private final String SERVER_ID = "SERVER";

    @Override
    public void start(Stage stage) {

        server =
                new ServerBackend(
                        message ->
                                Platform.runLater(() ->
                                        displayMessage(message)
                                )
                );

        server.startServer();

        BorderPane root =
                new BorderPane();

        root.getStyleClass().add("root");

        // TOP
        Label title =
                new Label("here we goooo!!!!!");

        title.getStyleClass().add("title");

        HBox top =
                new HBox(title);

        top.getStyleClass().add("topBar");

        // CENTER
        chatBox.getStyleClass().add("chatArea");

        scrollPane.setFitToWidth(true);

        // BOTTOM
        Button sendBtn =
                new Button("Send");

        sendBtn.getStyleClass().add("send-button");

        sendBtn.setOnAction(e -> sendMessage());

        Button imageBtn =
                new Button("Image");

        imageBtn.getStyleClass().add("image-button");

        imageBtn.setOnAction(e -> sendImage());

        HBox bottom =
                new HBox(
                        10,
                        messageField,
                        imageBtn,
                        sendBtn
                );

        HBox.setHgrow(
                messageField,
                Priority.ALWAYS
        );

        bottom.getStyleClass().add("bottomBar");

        root.setTop(top);
        root.setCenter(scrollPane);
        root.setBottom(bottom);

        Scene scene =
                new Scene(root, 400, 700);

        scene.getStylesheets().add(
                getClass()
                        .getResource("/style.css")
                        .toExternalForm()
        );

        stage.setScene(scene);

        stage.setTitle("Chat here....");

        stage.show();
    }

    private void sendMessage() {

        String text =
                messageField.getText().trim();

        if (!text.isEmpty()) {

            Message msg =
                    new Message(SERVER_ID, text);

            displayMessage(msg);

            server.broadcast(msg);

            messageField.clear();
        }
    }

    private void sendImage() {

        FileChooser chooser =
                new FileChooser();

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Images",
                        "*.png",
                        "*.jpg",
                        "*.jpeg"
                )
        );

        File file =
                chooser.showOpenDialog(null);

        if (file != null) {

            try {

                byte[] imageData =
                        Files.readAllBytes(
                                file.toPath()
                        );

                Message msg =
                        new Message(
                                SERVER_ID,
                                imageData
                        );

                displayMessage(msg);

                server.broadcast(msg);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void displayMessage(Message message) {

        boolean isMine =
                message.getSender()
                        .equals(SERVER_ID);

        HBox container =
                new HBox();

        container.setAlignment(
                isMine
                        ? Pos.CENTER_RIGHT
                        : Pos.CENTER_LEFT
        );

        if (message.isImage()) {

            Image image =
                    new Image(
                            new ByteArrayInputStream(
                                    message.getImageData()
                            )
                    );

            ImageView imageView =
                    new ImageView(image);

            imageView.setFitWidth(200);

            imageView.setPreserveRatio(true);

            VBox wrapper =
                    new VBox(imageView);

            wrapper.getStyleClass().add(
                    isMine
                            ? "my-message"
                            : "other-message"
            );

            container.getChildren()
                    .add(wrapper);

        } else {

            Label label =
                    new Label(
                            message.getSender()
                                    + ": "
                                    + message.getText()
                    );

            label.setWrapText(true);

            label.setMaxWidth(250);

            label.getStyleClass().add(
                    isMine
                            ? "my-message"
                            : "other-message"
            );

            container.getChildren()
                    .add(label);
        }

        chatBox.getChildren()
                .add(container);

        scrollPane.setVvalue(1.0);
    }

    public static void main(String[] args) {
        launch();
    }
}