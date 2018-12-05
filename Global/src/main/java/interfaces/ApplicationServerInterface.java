package interfaces;

public interface ApplicationServerInterface {

    String getIp();
    void setIp(String ip);
    int getRmiPort();
    void setRmiPort(int rmiPort);
    AppLoginInterface getApp_login();
    void setApp_login(AppLoginInterface app_login);
    String getName();
    int getRestPort();

    void setBackupServer(ApplicationServerInterface server);
    ApplicationServerInterface getBackupServer();

}
