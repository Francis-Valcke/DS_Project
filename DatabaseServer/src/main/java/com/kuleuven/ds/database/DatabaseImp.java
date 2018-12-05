package com.kuleuven.ds.database;

import com.google.common.hash.Hashing;
import exceptions.InvalidCredentialsException;
import exceptions.UserAlreadyExistsException;
import interfaces.DatabaseInterface;

import java.io.File;
import java.net.URISyntaxException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseImp extends UnicastRemoteObject implements DatabaseInterface {
    private Connection conn;

    public DatabaseImp(String dbFilePath) throws RemoteException{

        //Connectie met de DB opzetten
        String url = "jdbc:sqlite:" + dbFilePath;
        try{
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createNewUser(String username, String password) throws RemoteException, UserAlreadyExistsException {
        if(userExists(username)) throw new UserAlreadyExistsException();

        // SQL statement for creating new user
        String sql = "INSERT INTO users(username, password, salt) VALUES(?,?,?)";

        try{
            String salt = Hashing.sha256().hashString((System.currentTimeMillis() + "WillekeurigeString"), StandardCharsets.UTF_8).toString();
            String hashedPassw = hash(password, salt);
            try{
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, username);
                pstmt.setString(2, hashedPassw);
                pstmt.setString(3, salt);
                pstmt.executeUpdate();
            }
            catch(SQLException sqle){
                sqle.printStackTrace();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public String createToken(String username, String password) throws RemoteException, InvalidCredentialsException {
        if(checkCredentials(username, password)){
            String sql = "UPDATE users SET token = ? , token_timestamp = ? WHERE username = ?;";

            String token = hash(password+"MemoryGame"+System.currentTimeMillis());
            try{
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, token);
                pstmt.setLong(2, System.currentTimeMillis());
                pstmt.setString(3, username);
                pstmt.executeUpdate();
            }
            catch(SQLException se){
                se.printStackTrace();
            }
            return token;

        }
        else throw new InvalidCredentialsException();
    }

    public boolean checkCredentials(String username, String password) throws RemoteException{
        if(!userExists(username)){
            return false;
        }
        String sql = "SELECT password, salt FROM users WHERE username = ?";
        try{
            PreparedStatement pstmt  = conn.prepareStatement(sql);
            pstmt.setString(1,username);
            ResultSet rs  = pstmt.executeQuery();
            //binnengekomen paswoord hashen met de salt uit de DB
            password = hash(password, rs.getString("salt"));
            //checken als beide hashen overeenkomen
            if(password.equals(rs.getString("password"))){
                return true;
            }
            else return false;
        }
        catch(SQLException se){
            se.printStackTrace();
            return false;
        }




    }

    public boolean isTokenValid(String username, String token) throws RemoteException{
        //TODO: tokens hashen
        String sql = "SELECT token_timestamp FROM users WHERE username = ? AND token = ?";
        try{
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,username);
            pstmt.setString(2, token);
            ResultSet rs = pstmt.executeQuery();
            long currentTime = System.currentTimeMillis();
            while(rs.next()){
                //Als de token minder dan 24u oud is
                if(currentTime - rs.getLong(1) < 86400000) return true;
            }
            return false;
        }
        catch(SQLException se){
            //Als user niet bestaat of token klopt niet;
            return false;
        }
    }

    private boolean userExists(String username){
        String sql = "SELECT * FROM users WHERE username = ?";
        try{
            PreparedStatement pstmt  = conn.prepareStatement(sql);
            pstmt.setString(1,username);
            ResultSet rs  = pstmt.executeQuery();

            //Kijken als er iets in de resultset zit
            if(rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public List<byte[]> getTheme(int id) throws RemoteException {
        String sql = "SELECT picture FROM pictures WHERE theme_id=?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, Integer.toString(id));
            ResultSet rs = pstmt.executeQuery();

            List<byte[]> toReturn = new ArrayList<>();
            while(rs.next()) {
                Blob pictureBlob = rs.getBlob("picture");
                byte[] themeAsBytes = pictureBlob.getBytes(1, (int) pictureBlob.length());
                pictureBlob.free();
                toReturn.add(themeAsBytes);
            }
            return toReturn;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void insertPhoto(int id) throws RemoteException {
        //System.out.println(System.getProperty("user.dir"));
        byte[] picture = readFile("src/main/resources/Colors/"+id+".jpg");
        String sql = "INSERT INTO pictures(picture, theme_id) VALUES(?,?)";

        try{
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // set parameters
            pstmt.setBytes(1, picture);
            pstmt.setInt(2, 0);

            //execute query
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void newTheme() {
        String sql = "INSERT INTO themes(id, name, size) VALUES(?,?,?)";
    }

    private static String hash(String password, String salt){
        return Hashing.sha256().hashString((password + salt),StandardCharsets.UTF_8).toString();
    }

    private static String hash(String password){
        return Hashing.sha256().hashString((password),StandardCharsets.UTF_8).toString();
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
}
