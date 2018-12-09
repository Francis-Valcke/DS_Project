package logic;

import exceptions.AlreadyPresentException;
import exceptions.InvalidCredentialsException;
import interfaces.ApplicationServerInterface;
import interfaces.DispatcherInterface;
import interfaces.LobbyInterface;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ui.AlertBox;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LoginController {

    @FXML
    public TextField input_username;
    @FXML
    public PasswordField input_password;
    @FXML
    public Button button_signin;
    @FXML
    public Button button_register;


    public LoginController() {
    }

    public void login(ActionEvent actionEvent) {
        Registry registry;
        try {
            String username = input_username.getText();
            String password = input_password.getText();

            //Dispatcher opzoeken
            registry = LocateRegistry.getRegistry(Main.DISPATCH_IP, Main.DISPATCH_PORT);
            DispatcherInterface dispatch = (DispatcherInterface) registry.lookup("dispatcher_service");

            //Token opvragen
            String token = "";
            //Nieuwe token opvragen als de oude ouder dan 24u is
            if (!dispatch.isTokenValid(username, token)) {
                token = dispatch.requestNewToken(username, password);
            }

            Client client = Client.getInstance();
            client.setDispatch(dispatch);
            client.setUsername(username);
            client.setToken(token);

            client.connect();


            //Naar lobby scherm gaan
            SceneController.getInstance().showLobbyScene();

            //Tijdelijke code

        } catch (RemoteException | NotBoundException re) {
            AlertBox.display("Connection problems", "Cannot contact dispatcher");
            re.printStackTrace();
        } catch (InvalidCredentialsException ice) {
            AlertBox.display("Login not successful.", "Username and/or password are wrong.");
        } catch (AlreadyPresentException e) {
            AlertBox.display("Login not successful.", e.getMessage());
        }

    }

    public void register(ActionEvent actionEvent) {
        SceneController.getInstance().showRegisterScene();
    }


}
