package virtualclientserver.controllers;

import classes.GameInfo;
import classes.ResponseMessage;
import classes.ThemeInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.*;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import virtualclientserver.VirtualClient;
import virtualclientserver.VirtualClientServer;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.List;

import static classes.ResponseType.NOK;
import static classes.ResponseType.OK;

@RestController
@RequestMapping(value = "memory/lobby")
public class LobbyRestController {

    private static VirtualClientServer clientServer = VirtualClientServer.getInstance();

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

            VirtualClient client = ((VirtualClient) clientServer.getClient(token));
            client.makeGame(name, x, y, maxPlayers, 0);

            GameInfo game = client.getGame().getGameInfo();

            responseMessage = new ResponseMessage(OK, "New game created.", game);
        } catch (IOException e) {
            responseMessage = new ResponseMessage(NOK, "A fatal error occurred.");
            e.printStackTrace();
        } catch (UserNotLoggedInException | InvalidSizeException | InvalidCredentialsException | AlreadyPresentException | ThemeNotLargeEnoughException e) {
            responseMessage = new ResponseMessage(NOK, e.getMessage());
        }
        return responseMessage;
    }

    @RequestMapping(value = "joinGame", produces = "application/json")
    public ResponseMessage joinGame(@RequestParam String token, @RequestBody GameInfo gameInfo) {
        ResponseMessage responseMessage = null;
        try {

            VirtualClient client = ((VirtualClient) clientServer.getClient(token));
            client.joinGame(gameInfo);
            GameInfo game = client.getGame().getGameInfo();
            responseMessage = new ResponseMessage(OK, "Game joined successfully", game);

        } catch (RemoteException e) {
            responseMessage = new ResponseMessage(NOK, "A fatal error occurred.");
            e.printStackTrace();
        } catch (InvalidCredentialsException | GameNotFoundException | GameFullException | GameStartedException | UserNotLoggedInException | AlreadyPresentException | NoSuchGameExistsException e) {
            responseMessage = new ResponseMessage(NOK, e.getMessage());
        }
        return responseMessage;
    }

    @RequestMapping(value = "spectateGame", produces = "application/json")
    public ResponseMessage spectateGame(@RequestParam String token, @RequestBody GameInfo gameInfo) {
        ResponseMessage responseMessage = null;

        try {
            VirtualClient client = ((VirtualClient) clientServer.getClient(token));

            client.spectateGame(gameInfo);
            GameInfo game = client.getGame().getGameInfo();
            responseMessage = new ResponseMessage(OK, "Spectating game successfully", game);

        } catch (InvalidCredentialsException | GameNotFoundException | UserNotLoggedInException | AlreadyPresentException e) {
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
        VirtualClient client = null;
        try {
            client = ((VirtualClient) clientServer.getClient(token));

            List<GameInfo> liveGames = client.getLobby().getAllLiveGames();
            responseMessage = new ResponseMessage(OK, "Live games:", liveGames);
        } catch (UserNotLoggedInException e) {
            responseMessage = new ResponseMessage(NOK, e.getMessage());
        }

        return responseMessage;
    }

    @RequestMapping(value= "getThemesInfo", produces = "application/json")
    public ResponseMessage getThemesInfo(@RequestParam String token) {
        ResponseMessage responseMessage = null;
        VirtualClient client = null;
        try {
            client = ((VirtualClient) clientServer.getClient(token));
            List<ThemeInfo> themes = client.getLobby().getThemes();
            responseMessage = new ResponseMessage(OK, "List of themes on the server:", themes);
        } catch (UserNotLoggedInException e) {
            responseMessage = new ResponseMessage(NOK, e.getMessage());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return responseMessage;
    }

    @RequestMapping(value= "getPicture")
    public @ResponseBody byte[] getPicture(@RequestParam String token, @RequestParam int themeId, @RequestParam int pictureId) {
        ResponseMessage responseMessage = null;
        VirtualClient client = null;

        try {
            client = ((VirtualClient) clientServer.getClient(token));

            byte[] bytes = client.getLobby().getPicture(themeId, pictureId);
            return bytes;
        } catch (UserNotLoggedInException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }
}
