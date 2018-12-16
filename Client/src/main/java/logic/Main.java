package logic;

import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main extends Application {

    public static String username;
    public static String password;

    public static void main(String[] args) {
        username = args[0];
        password = args[1];
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneController.getInstance().showLoginScene();
    }
}
