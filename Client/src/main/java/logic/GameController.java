package logic;

import classes.Coordinate;
import classes.PlayerInfo;
import interfaces.GameControllerInterface;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import ui.Tile;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class GameController implements Initializable, GameControllerInterface {
    @FXML
    GridPane tileGrid;
    @FXML
    Label label;
    @FXML
    BorderPane borderPane;
    @FXML
    CheckBox readycheckbox;
    @FXML
    Button leavegamebutton;
    @FXML
    ListView<Label> playerslist;
    @FXML
    Label playerslabel;
    @FXML
    Label infolabel;

    boolean gameStarted = false;
    boolean spectatorMode;

    HashMap<Coordinate, Tile> tiles = new HashMap<>();
    int height, width;

    public GameController(int height, int width, boolean spectator) {
        this.height = height;
        this.width = width;
        spectatorMode = spectator;
        //TODO: spectator gamecontroller anders aanmaken
        try {
            updatePlayerList(Client.getInstance().getGame().getPlayerlist());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void showTile(Coordinate c, int value) {
        Tile tile = tiles.get(c);
        tile.open(Integer.toString(value));
    }

    public void hideTile(Coordinate c, int delay) {
        Tile tile = tiles.get(c);
        tile.close(delay);
    }

    public void ready() {
        Client.getInstance().readyUp();
        readycheckbox.setDisable(true);
    }

    public void leaveGame(){
        Client.getInstance().leaveGame();
    }

    public void updatePlayerList(List<PlayerInfo> playerInfoList){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                playerslist.getItems().clear();
                int readyCount = 0;
                for(PlayerInfo pi: playerInfoList){
                    if(pi.isReady()) readyCount++;
                    Label label = new Label();
                    label.setText(pi.getUsername() +"\t\t"+pi.getScore());
                    playerslist.getItems().add(label);
                }
                if(!gameStarted){
                    infolabel.setText("players ready: "+readyCount+"/"+playerInfoList.size());
                }
                //Todo: persoon aan de beurt int vet zetten ofzo;
            }
        });

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        readycheckbox.setOnAction(event -> {
            if(readycheckbox.isSelected()) {
                ready();
            }
        });

        if(spectatorMode){
            readycheckbox.setDisable(true);
        }

        leavegamebutton.setOnAction(event -> {
            leaveGame();
        });

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Coordinate c = new Coordinate(x, y);
                Tile t = new Tile(c, 200);
                tileGrid.add(t, x, y);
                tiles.put(c, t);
            }
        }
        for (int i = 0; i < height; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setFillHeight(true);
            rc.setVgrow(Priority.ALWAYS);
            tileGrid.getRowConstraints().add(rc);
        }
        for (int i = 0; i < width; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setFillWidth(true);
            cc.setHgrow(Priority.ALWAYS);
            tileGrid.getColumnConstraints().add(cc);
        }
    }

    public void startGame() {
        gameStarted = true;
        updateInfoLabel("Game started");
    }

    public void updateInfoLabel(String s){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                infolabel.setText(s);
            }
        });
    }
}
