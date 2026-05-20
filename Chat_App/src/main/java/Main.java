import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource("/chat.fxml")
                );

        Scene scene = new Scene(loader.load());

        scene.getStylesheets().add(
                getClass().getResource("/style.css")
                        .toExternalForm()
        );

        stage.setTitle("chat here....");

        stage.setScene(scene);

        stage.setWidth(400);
        stage.setHeight(700);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}