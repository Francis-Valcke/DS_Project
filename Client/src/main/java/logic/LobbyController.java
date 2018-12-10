package logic;


import classes.GameInfo;
import exceptions.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
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
    @FXML
    Text serverName;

    HashMap<Label, GameInfo> labelMap = new HashMap<>();

    Client client = Client.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        joinbutton.setOnAction(e -> {
            try {
                joinGame();
            } catch (AlreadyPresentException | InvalidCredentialsException | GameFullException | GameStartedException | NoSuchGameExistsException | GameNotFoundException e1) {
                System.out.println(e1.getMessage());
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        });
        spectatebutton.setOnAction(e -> {
            try {
                spectateGame();
            } catch (RemoteException | InvalidCredentialsException | GameNotFoundException e1) {
                e1.printStackTrace();
            } catch (AlreadyPresentException e1) {
                e1.printStackTrace();
            }
        });

        try {
            serverName.setText(client.getLobby().getName());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        client.setLobbyController(this);
    }

    public void makeGame() throws AlreadyPresentException {
        client.makeGame(name.getText(), Integer.parseInt(width.getText()), Integer.parseInt(height.getText()), Integer.parseInt(playercount.getText()), 1);
    }

    public void joinGame() throws AlreadyPresentException, RemoteException, InvalidCredentialsException, GameFullException, GameStartedException, NoSuchGameExistsException, GameNotFoundException {
        GameInfo selected = labelMap.get(gameslist.getSelectionModel().getSelectedItem());
        client.joinGame(selected);
    }

    public void spectateGame() throws RemoteException, InvalidCredentialsException, AlreadyPresentException, GameNotFoundException {
        GameInfo selected = labelMap.get(gameslist.getSelectionModel().getSelectedItem());
        client.spectateGame(selected);
    }

    public void refreshList() {
        try {

            gameslist.getItems().clear();
            List<GameInfo> games = client.getLobby().getAllLiveGames();

            for (GameInfo gi : games) {
                Label label = new Label(gi.getName() + "\t" + "(" + gi.getNumberOfPlayersJoined() + "/" + gi.getMaxPlayers() + ") " + (gi.isStarted() ? "(started)" : "") + " " + gi.getHostName());
                labelMap.put(label, gi);
                gameslist.getItems().add(label);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
