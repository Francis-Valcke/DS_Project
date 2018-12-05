package logic;

import exceptions.InvalidCredentialsException;
import exceptions.UserAlreadyExistsException;
import interfaces.ApplicationServerInterface;
import interfaces.DispatcherInterface;
import interfaces.LobbyInterface;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RegisterController {

    public TextField input_username;
    public PasswordField input_password;
    public PasswordField input_confirm;
    public Button button_register;
    public Button button_back;


    public RegisterController() {
    }

    public void register() {
        try {
            String username = input_username.getText();
            String password = input_password.getText();

            Registry registry = LocateRegistry.getRegistry(Main.DISPATCH_IP, Main.DISPATCH_PORT);
            DispatcherInterface dispatch = (DispatcherInterface) registry.lookup("dispatcher_service");

            dispatch.registerNewUser(username, password);

            String token = dispatch.requestNewToken(username, password);

            Client client = Client.getInstance();
            client.setApplicationServer(dispatch.getApplicationServer());

            ApplicationServerInterface appServer = dispatch.getApplicationServer();
            LobbyInterface lobby = appServer.getAppLogin().clientLogin(username, token);
            appServer.addConnectedClient(client);
            client.setApplicationServer(appServer);

            client.setUsername(username);
            client.setToken(token);
            client.setDispatch(dispatch);
            client.setApp_login(appServer.getAppLogin());
            client.setLobby(lobby);
            SceneController.getInstance().showLobbyScene();

        } catch (RemoteException | NotBoundException | UserAlreadyExistsException | InvalidCredentialsException e) {
            e.printStackTrace();
        }
    }

    public void goBack() {
        SceneController.getInstance().showLoginScene();
    }
}