package dispatcher;

import classes.ResponseMessage;
import classes.ResponseType;
import com.fasterxml.jackson.core.JsonProcessingException;
import exceptions.InvalidCredentialsException;
import exceptions.UserAlreadyExistsException;
import interfaces.VirtualClientServerInterface;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.rmi.RemoteException;

@RestController
@RequestMapping(value = "memory")
public class DispatcherController {

    private Dispatcher dispatcher = Dispatcher.getInstance();

    @RequestMapping(method = RequestMethod.POST, value = "/registerNewUser")
    public ResponseMessage registerNewUser(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password) {
        ResponseMessage responseMessage = null;
        try {
            dispatcher.registerNewUser(username, password);
            responseMessage = new ResponseMessage(ResponseType.OK, "New user has been created.");
        } catch (RemoteException e) {
            responseMessage = new ResponseMessage(ResponseType.NOK, "Internal server error!");
            e.printStackTrace();
        } catch (UserAlreadyExistsException e) {
            responseMessage = new ResponseMessage(ResponseType.NOK, e.getMessage());
        }
        return responseMessage;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/requestNewToken")
    public ResponseMessage requestNewToken(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password) {
        ResponseMessage responseMessage = null;

        try {
            String token = dispatcher.requestNewToken(username, password);
            responseMessage = new ResponseMessage(ResponseType.OK, "Token created.", token);
        } catch (RemoteException e) {
            responseMessage = new ResponseMessage(ResponseType.NOK, "Internal server error!");
            e.printStackTrace();
        } catch (InvalidCredentialsException e) {
            responseMessage = new ResponseMessage(ResponseType.NOK, e.getMessage());
        }
        return responseMessage;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/isTokenValid")
    public ResponseMessage isTokenValid(@RequestParam(value = "username") String username, @RequestParam(value = "token") String token) {
        ResponseMessage responseMessage = null;
        try {
            boolean result = dispatcher.isTokenValid(username, token);
            responseMessage = new ResponseMessage(ResponseType.OK, "Status of token checked successfully.", result);
        } catch (RemoteException e) {
            responseMessage = new ResponseMessage(ResponseType.NOK, "Internal server error!");
            e.printStackTrace();
        }
        return responseMessage;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getVirtualClientServer")
    public ResponseMessage getApplicationServer() throws RemoteException {
        ResponseMessage responseMessage = null;

        VirtualClientServerInterface server = dispatcher.getVirtualClientServer();

        VirtualClientServer object = new VirtualClientServer(server.getName(), server.getAddress(), server.getPort());

        responseMessage = new ResponseMessage(ResponseType.OK, "Application servers available.", object);

        return responseMessage;
    }


}
