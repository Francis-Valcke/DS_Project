package com.kuleuven.ds.controllers;

import classes.AbstractClient;
import classes.ResponseMessage;
import com.kuleuven.ds.VirtualClient;
import com.kuleuven.ds.VirtualClientServer;
import exceptions.AlreadyPresentException;
import exceptions.InvalidCredentialsException;
import exceptions.UserNotLoggedInException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.rmi.RemoteException;

import static classes.ResponseType.NOK;
import static classes.ResponseType.OK;

@RestController
@RequestMapping(value = "memory/appLogin")
public class AppLoginRestController {

    private static VirtualClientServer server = VirtualClientServer.getInstance();

    @RequestMapping(value = "login", produces = "application/json")
    public ResponseMessage login(@RequestParam String username, @RequestParam String token) {
        ResponseMessage responseMessage = null;

        try {

            VirtualClient client = new VirtualClient(username, token);
            client.setDispatch(server.getDispatcher());
            client.connect();
            server.getConnectedClients().put(token, client);

            responseMessage = new ResponseMessage(OK, "Logged in.");
        } catch (AlreadyPresentException | InvalidCredentialsException e) {
            responseMessage = new ResponseMessage(NOK, e.getMessage());
        } catch (RemoteException e) {
            responseMessage = new ResponseMessage(NOK, "A fatal error occurred.");
            e.printStackTrace();
        }

        return responseMessage;
    }

    @RequestMapping(value = "logout", produces = "application/json")
    public ResponseMessage logout(@RequestParam String token) {
        ResponseMessage responseMessage = null;

        try {
            VirtualClient client = ((VirtualClient) server.getClient(token));
            client.disconnect(true);
            server.getConnectedClients().remove(token);

            responseMessage = new ResponseMessage(OK, "Logged out.");
        } catch (UserNotLoggedInException e) {
            responseMessage = new ResponseMessage(NOK, e.getMessage());
        } catch (RemoteException e) {
            responseMessage = new ResponseMessage(NOK, "A fatal error occurred.");
            e.printStackTrace();
        }

        return responseMessage;
    }
}
