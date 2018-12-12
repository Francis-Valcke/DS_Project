package database;

import classes.GameInfo;
import classes.PreparedStatementWrapper;
import classes.ThemeInfo;
import com.google.common.hash.Hashing;
import exceptions.InvalidCredentialsException;
import exceptions.UserAlreadyExistsException;
import interfaces.DatabaseInterface;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseImp extends UnicastRemoteObject implements DatabaseInterface {
    private DatabaseInterface master; //==null betekent dat deze de master is
    private Connection conn;
    private List<DatabaseInterface> peers = new ArrayList<>();
    int gamesMade = 0;

    public DatabaseImp(String dbFilePath) throws RemoteException {

        //Connectie met de DB opzetten
        String url = "jdbc:sqlite:" + dbFilePath;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static String hash(String password, String salt) {
        return Hashing.sha256().hashString((password + salt), StandardCharsets.UTF_8).toString();
    }

    private static String hash(String password) {
        return Hashing.sha256().hashString((password), StandardCharsets.UTF_8).toString();
    }

    public void createNewUser(String username, String password) throws RemoteException, UserAlreadyExistsException {
        if (master == null) {
            if (userExists(username)) throw new UserAlreadyExistsException();

            // SQL statement for creating new user
            String sql = "INSERT INTO users(username, password, salt) VALUES(?,?,?)";

            try {
                String salt = Hashing.sha256().hashString((System.currentTimeMillis() + "WillekeurigeString"), StandardCharsets.UTF_8).toString();
                String hashedPassw = hash(password, salt);
                try {
                    PreparedStatementWrapper pstmt = new PreparedStatementWrapper(sql);
                    pstmt.setString(1, username);
                    pstmt.setString(2, hashedPassw);
                    pstmt.setString(3, salt);
                    pstmt.executeUpdate(conn);

                    //Nieuwe users moeten direct gepusht worden naar alle servers
                    pushToPeers(pstmt);

                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            master.createNewUser(username, password);
        }
    }

    public String createToken(String username, String password) throws RemoteException, InvalidCredentialsException {
        if (master == null) {
            if (checkCredentials(username, password)) {
                String sql = "UPDATE users SET token = ? , token_timestamp = ? WHERE username = ?;";

                String token = hash(password + "MemoryGame" + System.currentTimeMillis());
                try {
                    PreparedStatementWrapper pstmt = new PreparedStatementWrapper(sql);
                    pstmt.setString(1, hash(token));
                    pstmt.setLong(2, System.currentTimeMillis());
                    pstmt.setString(3, username);
                    pstmt.executeUpdate(conn);

                    //Tokens moeten direct gepusht worden naar alle servers
                    pushToPeers(pstmt);
                } catch (SQLException se) {
                    se.printStackTrace();
                }
                return token;

            } else throw new InvalidCredentialsException();
        } else {
            return master.createToken(username, password);
        }
    }

    public boolean checkCredentials(String username, String password) throws RemoteException {
        if (!userExists(username)) {
            return false;
        }
        String sql = "SELECT password, salt FROM users WHERE username = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            //binnengekomen paswoord hashen met de salt uit de DB
            password = hash(password, rs.getString("salt"));
            //checken als beide hashen overeenkomen
            if (password.equals(rs.getString("password"))) {
                return true;
            } else return false;
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }


    }

    public void inValidateToken(String username) throws RemoteException {
        //token invalideren door timestamp op 0 te zetten
        if (master == null) {
            try {
                String sql = "UPDATE users SET token_timestamp = ? WHERE username = ?;";
                PreparedStatementWrapper pstmt = new PreparedStatementWrapper(sql);
                pstmt.setLong(1, 0);
                pstmt.setString(2, username);

                pstmt.executeUpdate(conn);

                pushToPeers(pstmt);
            } catch (SQLException sqle) {
                System.out.println(sqle);
            }
        } else {
            master.inValidateToken(username);
        }
    }

    public boolean isTokenValid(String username, String token) throws RemoteException {
        String sql = "SELECT token_timestamp FROM users WHERE username = ? AND token = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, hash(token));
            ResultSet rs = pstmt.executeQuery();
            long currentTime = System.currentTimeMillis();
            while (rs.next()) {
                //Als de token minder dan 24u oud is
                if (currentTime - rs.getLong(1) < 86400000) return true;
            }
            return false;
        } catch (SQLException se) {
            //Als user niet bestaat of token klopt niet;
            return false;
        }
    }

    private boolean userExists(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            //Kijken als er iets in de resultset zit
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public List<byte[]> getPictures(int theme_id) throws RemoteException {
        String sql = "SELECT picture FROM pictures WHERE theme_id=?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, Integer.toString(theme_id));
            ResultSet rs = pstmt.executeQuery();

            List<byte[]> toReturn = new ArrayList<>();
            while (rs.next()) {
                InputStream is = rs.getBinaryStream("picture");
                byte[] themeAsBytes = IOUtils.toByteArray(is);
                toReturn.add(themeAsBytes);
            }
            return toReturn;

        } catch (SQLException | IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public byte[] getPicture(int theme_id, int picture_index) throws RemoteException{
        try{
            String sql1 = "SELECT start_id FROM themes WHERE theme_id = ?";
            PreparedStatement pstmt1 = conn.prepareStatement(sql1);
            pstmt1.setInt(1, theme_id);
            ResultSet rs1 = pstmt1.executeQuery();
            if(rs1.next()) {
                int index = rs1.getInt("start_id");
                String sql2 = "SELECT picture FROM pictures WHERE id=?";
                PreparedStatement pstmt2 = conn.prepareStatement(sql2);
                pstmt2.setInt(1, index+picture_index);
                ResultSet rs2 = pstmt2.executeQuery();

                if(rs2.next()){
                    InputStream is = rs2.getBinaryStream("picture");
                    return IOUtils.toByteArray(is);
                }
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<ThemeInfo> getThemes() throws RemoteException {
        String sql = "SELECT * FROM themes";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            List<ThemeInfo> toReturn = new ArrayList<>();
            while (rs.next()) {
                toReturn.add(new ThemeInfo(rs.getInt(1), rs.getString(2), rs.getInt(3)));
            }
            return toReturn;
        } catch (SQLException sqe) {
            System.out.println(sqe);
        }
        return null;
    }

    public ThemeInfo getTheme(int id) throws RemoteException {
        String sql = "SELECT * FROM themes WHERE id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new ThemeInfo(rs.getInt(1), rs.getString(2), rs.getInt(3));
            }
        } catch (SQLException sqe) {
            System.out.println(sqe);
        }
        return null;
    }

    public List<GameInfo> getAllGames() throws RemoteException {
        try {
            String sql = "SELECT * FROM games";
            PreparedStatementWrapper pstmt = new PreparedStatementWrapper(sql);
            ResultSet rs = pstmt.executeQuery(conn);
            List<GameInfo> toReturn = new ArrayList<>();
            while (rs.next()) {
                toReturn.add(new GameInfo(rs.getString(6), rs.getString(2), rs.getString(1),
                        rs.getInt(8), rs.getInt(9), rs.getInt(4), rs.getInt(3),
                        rs.getBoolean(5), rs.getInt(7)));
            }
            return toReturn;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addGame(GameInfo gi) throws RemoteException {
        try {
            String sql = "INSERT INTO games(id, name, players_joined, max_players, started, hostname, theme_id, width, height) VALUES(?,?,?,?,?,?,?,?,?)";
            PreparedStatementWrapper pstmt = new PreparedStatementWrapper(sql);
            pstmt.setString(1, gi.getId());
            pstmt.setString(2, gi.getName());
            pstmt.setInt(3, gi.getNumberOfPlayersJoined());
            pstmt.setInt(4, gi.getMaxPlayers());
            pstmt.setBoolean(5, gi.isStarted());
            pstmt.setString(6, gi.getHostName());
            pstmt.setInt(7, gi.getTheme_id());
            pstmt.setInt(8, gi.getWidth());
            pstmt.setInt(9, gi.getHeight());

            pstmt.executeUpdate(conn);

            pushToPeers(pstmt);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void updateGameInfo(GameInfo gi) throws RemoteException {
        try {
            String sql = "UPDATE games SET players_joined = ?, started = ?";
            PreparedStatementWrapper pstmt = new PreparedStatementWrapper(sql);
            pstmt.setInt(1, gi.getNumberOfPlayersJoined());
            pstmt.setBoolean(2, gi.isStarted());

            pstmt.executeUpdate(conn);
            pushToPeers(pstmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void removeGame(GameInfo gi) throws RemoteException {
        try {
            String sql = "DELETE FROM games WHERE id = ?";
            PreparedStatementWrapper pstmt = new PreparedStatementWrapper(sql);
            pstmt.setString(1, gi.getId());

            pstmt.executeUpdate(conn);
            pushToPeers(pstmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void clearGames() {
        try {
            String sql = "DELETE FROM games";
            PreparedStatementWrapper pstmt = new PreparedStatementWrapper(sql);
            pstmt.executeUpdate(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void insertPhoto(int id) throws RemoteException {
        if (master == null) {
            //System.out.println(System.getProperty("user.dir"));
            byte[] picture = readFile(ClassLoader.getSystemClassLoader().getResource("sugimori/" + id + ".png").getPath());

            String sql = "INSERT INTO pictures(picture, theme_id) VALUES(?,?)";

            try {
                PreparedStatementWrapper pstmt = new PreparedStatementWrapper(sql);

                // set parameters
                pstmt.setBytes(1, picture);
                pstmt.setInt(2, 1);

                //execute query
                pstmt.executeUpdate(conn);

                pushToPeers(pstmt);

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } else {
            master.insertPhoto(id);
        }
    }


    //TIJDELIJK, om files op te zetten naar byte arrays
    private byte[] readFile(String file) {
        ByteArrayOutputStream bos = null;
        try {
            File f = new File(file);
            FileInputStream fis = new FileInputStream(f);
            byte[] buffer = new byte[1024];
            bos = new ByteArrayOutputStream();
            for (int len; (len = fis.read(buffer)) != -1; ) {
                bos.write(buffer, 0, len);
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e2) {
            System.err.println(e2.getMessage());
        }
        return bos != null ? bos.toByteArray() : null;
    }

    public void executeSQL(PreparedStatementWrapper pstmt) throws RemoteException {
        try {
            pstmt.executeUpdate(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void pushToPeers(PreparedStatementWrapper pstmt) {
        for (DatabaseInterface slave : peers) {
            try {
                slave.executeSQL(pstmt);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public DatabaseInterface getMaster() throws RemoteException {
        return master;
    }

    public void setMaster(DatabaseInterface master) throws RemoteException {
        this.master = master;
    }

    public void addPeer(DatabaseInterface slave) throws RemoteException {
        peers.add(slave);
    }

    public void removePeer(DatabaseInterface slave) throws RemoteException {
        peers.remove(slave);
    }


}
