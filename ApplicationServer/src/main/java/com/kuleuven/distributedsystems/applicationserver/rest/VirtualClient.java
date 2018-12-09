package com.kuleuven.distributedsystems.applicationserver.rest;

import classes.AbstractClient;
import classes.Coordinate;
import com.kuleuven.distributedsystems.applicationserver.AppLogin;
import com.kuleuven.distributedsystems.applicationserver.Game;
import com.kuleuven.distributedsystems.applicationserver.Lobby;
import exceptions.*;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class VirtualClient extends AbstractClient {

    private List<Coordinate> moves = new LinkedList<>();

    public VirtualClient(String username, String token) throws RemoteException {
        this.username = username;
        this.token = token;
        lobby = Lobby.getInstance();
        gameController = new VirtualGameController();
    }

    @Override
    public synchronized Coordinate requestMove() throws RemoteException, LeftGameException {
        /*
         * We moeten 2 moves binnen krijgen van de mobile client van wie het de beurt is.
         * */
        ((VirtualGameController) gameController).yourTurn(true);
        nextMove = super.requestMove();
        ((VirtualGameController) gameController).yourTurn(false);

        if (moves.size() % 2 == 0)
            moves.clear();

        return nextMove;
    }

    @Override
    public void makeGame(String name, int width, int height, int maxPlayers, int themeId) throws InvalidSizeException, RemoteException, InvalidCredentialsException, AlreadyPresentException {
        super.makeGame(name, width, height, maxPlayers, themeId);
        gameController = new VirtualGameController(this, height, width, false);

    }

    @Override
    public void joinGame(String gameId) throws GameFullException, RemoteException, GameStartedException, InvalidCredentialsException, GameNotFoundException, AlreadyPresentException {
        super.joinGame(gameId);
        gameController = new VirtualGameController(this, game.getHeight(), game.getWidth(), false);
    }

    @Override
    public void spectateGame(String gameId) throws GameNotFoundException, RemoteException, InvalidCredentialsException {
        super.spectateGame(gameId);
        gameController = new VirtualGameController(this, game.getHeight(), game.getWidth(), true);
        lobby.spectateGame(gameId, this);
        inGame = true;
        HashMap<Coordinate, Integer> flippedFields = game.getFlippedFields();
        for (Coordinate c : flippedFields.keySet()) {
            gameController.showTile(c, flippedFields.get(c));
        }
    }

    public synchronized Object recordMove(Coordinate c) throws RemoteException, InvalidMoveException, NotYourTurnException {
        if (!((VirtualGameController)gameController).isYourTurn()) throw new NotYourTurnException();
        if (!isMoveValid(c)) throw new InvalidMoveException();
        nextMove = c;
        moves.add(c);
        notifyAll();
        return ((Game) game).getBoard().get(c).getValue();
    }

    private boolean isMoveValid(Coordinate c) throws RemoteException {
        int x = game.getWidth();
        int y = game.getHeight();
        if (c.getX() < 0 || c.getX() >= x) return false;
        if (c.getY() < 0 || c.getY() >= y) return false;
        if (moves.contains(c)) return false;
        return true;
    }

}
