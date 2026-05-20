import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.UUID;

public class ChatController {

    @FXML
    private VBox chatBox;

    @FXML
    private TextField messageField;

    @FXML
    private ScrollPane scrollPane;

    private Client client;

    private final String myID =
            UUID.randomUUID()
                    .toString()
                    .substring(0, 5);

    public void initialize() {

        System.out.println(
                "[UI] Chat Started with ID: " + myID
        );

        chatBox.heightProperty().addListener(
                (obs, oldVal, newVal) ->
                        scrollPane.setVvalue(1.0)
        );

        client = new Client("localhost", 5000);

        new Thread(() -> {

            while (true) {

                Message message =
                        client.receiveMessage();

                if (message != null) {

                    Platform.runLater(() ->
                            displayMessage(message)
                    );
                }
            }

        }).start();
    }

    private void displayMessage(Message message) {

        boolean isMine =
                message.getSender().equals(myID);

        HBox container = new HBox();

        container.setAlignment(
                isMine
                        ? Pos.CENTER_RIGHT
                        : Pos.CENTER_LEFT
        );

        container.setPadding(
                new javafx.geometry.Insets(5)
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

            container.getChildren().add(wrapper);

        } else {

            Label label = new Label(message.getText());

            label.setWrapText(true);

            label.setMaxWidth(250);

            label.getStyleClass().add(
                    isMine
                            ? "my-message"
                            : "other-message"
            );

            container.getChildren().add(label);
        }

        chatBox.getChildren().add(container);
    }

    @FXML
    public void sendMessage() {

        String text =
                messageField.getText().trim();

        if (!text.isEmpty()) {

            Message message =
                    new Message(myID, text);

            client.sendMessage(message);

            messageField.clear();
        }
    }

    @FXML
    public void sendImage() {

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

                Message message =
                        new Message(myID, imageData);

                client.sendMessage(message);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}