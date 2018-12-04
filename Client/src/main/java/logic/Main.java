package logic;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application{

    public static final String DISPATCH_IP = "localhost";
    public static final int DISPATCH_PORT = 1000;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneController.getInstance().showLoginScene();

    }
}
