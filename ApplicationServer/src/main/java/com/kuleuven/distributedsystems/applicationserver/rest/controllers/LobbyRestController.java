package com.kuleuven.distributedsystems.applicationserver.rest.controllers;

import classes.GameInfo;
import classes.ResponseMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuleuven.distributedsystems.applicationserver.Lobby;
import com.kuleuven.distributedsystems.applicationserver.rest.VirtualClient;
import com.kuleuven.distributedsystems.applicationserver.rest.VirtualClientManager;
import exceptions.*;
import interfaces.ClientInterface;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.rmi.RemoteException;

import static classes.ResponseType.NOK;
import static classes.ResponseType.OK;

@RestController
@RequestMapping(value = "memory/lobby")
public class LobbyRestController {
    private static VirtualClientManager clientManager = VirtualClientManager.getInstance();

    @RequestMapping(method = RequestMethod.POST, value = "makeNewGame", consumes = "application/json", produces = "application/json")
    public ResponseMessage makeNewGame(@RequestParam String token, @RequestBody String body) {
        ResponseMessage responseMessage = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode actualObj = mapper.readTree(body);
            String name = actualObj.get("name").asText();
            int x = actualObj.get("x").asInt();
            int y = actualObj.get("y").asInt();
            int maxPlayers = actualObj.get("maxPlayers").asInt();

            VirtualClient client = clientManager.getClient(token);
            GameInfo game = client.makeGame(name, x, y, maxPlayers);
            responseMessage = new ResponseMessage(OK, "New game created.", game);
        } catch (IOException e) {
            responseMessage = new ResponseMessage(NOK, "A fatal error occurred.");
            e.printStackTrace();
        } catch (UserNotLoggedInException | InvalidSizeException | InvalidCredentialsException | AlreadyPresentException e) {
            responseMessage = new ResponseMessage(NOK, e.getMessage());
        }
        return responseMessage;
    }

    @RequestMapping(value = "joinGame", produces = "application/json")
    public ResponseMessage joinGame(@RequestParam String token, @RequestParam int gameId) {
        ResponseMessage responseMessage = null;
        try {
            VirtualClient client = clientManager.getClient(token);
            GameInfo game = client.joinGame(gameId);
            responseMessage = new ResponseMessage(OK, "Game joined successfully", game);
        } catch (RemoteException e) {
            responseMessage = new ResponseMessage(NOK, "A fatal error occurred.");
            e.printStackTrace();
        } catch (InvalidCredentialsException | GameNotFoundException | GameFullException | GameStartedException | UserNotLoggedInException | NoSuchGameExistsException | AlreadyPresentException e) {
            responseMessage = new ResponseMessage(NOK, e.getMessage());
        }
        return responseMessage;
    }

    @RequestMapping(value = "spectateGame", produces = "application/json")
    public ResponseMessage spectateGame(@RequestParam String token, @RequestParam int gameId) {
        ResponseMessage responseMessage = null;

        try {
            VirtualClient client = clientManager.getClient(token);
            client.spectateGame(gameId);
            responseMessage = new ResponseMessage(OK, "Spectating game successfully");
        } catch (InvalidCredentialsException | GameNotFoundException | UserNotLoggedInException | NoSuchGameExistsException e) {
            responseMessage = new ResponseMessage(NOK, e.getMessage());
        } catch (RemoteException e) {
            responseMessage = new ResponseMessage(NOK, "A fatal error occurred.");
            e.printStackTrace();
        }

        return responseMessage;
    }

    //This is a polling method
    @RequestMapping(value = "getLiveGames", produces = "application/json")
    public ResponseMessage getLiveGames(@RequestParam String token) throws RemoteException {
        ResponseMessage responseMessage = null;

        ClientInterface client = null;
        try {
            client = clientManager.getClient(token);
            Object o = Lobby.getInstance().getLiveGames();
            responseMessage = new ResponseMessage(OK, "Live games:", o);
        } catch (UserNotLoggedInException e) {
            responseMessage = new ResponseMessage(NOK, e.getMessage());
        }

        return responseMessage;
    }
}
