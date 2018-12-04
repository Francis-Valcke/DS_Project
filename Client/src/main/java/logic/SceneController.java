package logic;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;

public class SceneController {

    private static final SceneController instance = new SceneController();
    private Stage prevStage = null;

    private SceneController(){}

    public Stage getPrevStage() {
        return prevStage;
    }

    public void setPrevStage(Stage prevStage) {
        this.prevStage = prevStage;
    }


    public void showRegisterScene(){
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/register.fxml"));
            Scene scene = new Scene(root, 288, 400);
            stage.setTitle("Register");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();

            prevStage.close();
            prevStage = stage;
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    public void showLoginScene(){
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
            Scene scene = new Scene(root, 288, 400);
            stage.setTitle("Sign in");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();

            if(prevStage != null) prevStage.close();
            prevStage = stage;
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    public void createGameScene(GameController gc) {
        try {
            Client.getInstance().setGameController(gc);

            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gameView.fxml"));
            loader.setController(gc);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("Memory");
            stage.setResizable(true);
            stage.setScene(scene);
            stage.show();
            //Zorgen dat de controller de game verlaat als het scherm gesloten wordt
            stage.setOnCloseRequest(e -> {
                gc.leaveGame();
                SceneController.getInstance().showLobbyScene();
            });
            prevStage.close();
            prevStage = stage;
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }

    }
    public void showLobbyScene(){
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/lobby.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("Lobby");
            stage.setResizable(true);
            stage.setScene(scene);
            stage.show();

            prevStage.close();
            prevStage = stage;
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    public static SceneController getInstance() {
        return instance;
    }
}
