package com.kuleuven.distributedsystems.applicationserver.rest.controllers;

import classes.ResponseMessage;
import com.kuleuven.distributedsystems.applicationserver.AppLogin;
import com.kuleuven.distributedsystems.applicationserver.rest.VirtualClient;
import com.kuleuven.distributedsystems.applicationserver.rest.VirtualClientManager;
import exceptions.AlreadyPresentException;
import exceptions.InvalidCredentialsException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.rmi.RemoteException;

import static classes.ResponseType.NOK;
import static classes.ResponseType.OK;

@RestController
@RequestMapping(value = "memory/appLogin")
public class AppLoginRestController {

    private static VirtualClientManager clientManager = VirtualClientManager.getInstance();

    @RequestMapping(value = "login", produces = "application/json")
    public ResponseMessage login(@RequestParam String username, @RequestParam String token) {
        ResponseMessage responseMessage = null;

        try {
            AppLogin.getInstance().clientLogin(username, token);
            VirtualClient client = clientManager.newClient(username, token);
            responseMessage = new ResponseMessage(OK, "Logged in.");
        } catch (AlreadyPresentException | InvalidCredentialsException e) {
            responseMessage = new ResponseMessage(NOK, e.getMessage());
        } catch (RemoteException e) {
            responseMessage = new ResponseMessage(NOK, "A fatal error occurred.");
            e.printStackTrace();
        }

        return responseMessage;
    }
}
