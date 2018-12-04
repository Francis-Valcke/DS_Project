package logic;


import classes.GameInfo;
import exceptions.AlreadyPresentException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class LobbyController implements Initializable {
    @FXML
    Button create;
    @FXML
    TextField name;
    @FXML
    TextField height;
    @FXML
    TextField width;
    @FXML
    TextField playercount;
    @FXML
    ListView<Label> gameslist;
    @FXML
    Button joinbutton;
    @FXML
    Button spectatebutton;

    HashMap<Label, GameInfo> labelMap = new HashMap<>();

    Client client = Client.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        joinbutton.setOnAction(e -> {
            try {
                joinGame();
            } catch (AlreadyPresentException e1) {
                System.out.println(e1.getMessage());
            }
        });
        spectatebutton.setOnAction(e ->{spectateGame();});
    }

    public void makeGame() throws AlreadyPresentException {
        client.makeGame(name.getText(), Integer.parseInt(width.getText()), Integer.parseInt(height.getText()), Integer.parseInt(playercount.getText()));
    }

    public void joinGame() throws AlreadyPresentException {
        GameInfo selected = labelMap.get(gameslist.getSelectionModel().getSelectedItem());
        client.joinGame(selected);

    }
    public void spectateGame(){
        GameInfo selected = labelMap.get(gameslist.getSelectionModel().getSelectedItem());
        client.spectateGame(selected.getId());
        //TODO: implementeren
    }

    public void refreshList() {
        try {
            gameslist.getItems().clear();
            ArrayList<GameInfo> games = client.getLobby().getLiveGames();
            for (GameInfo gi : games) {
                Label label = new Label(gi.getName() + "\t" + "(" + gi.getNumberOfPlayersJoined() + "/" + gi.getMaxPlayers() + ") "+(gi.isStarted() ? "(started)" : ""));
                labelMap.put(label, gi);
                gameslist.getItems().add(label);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
