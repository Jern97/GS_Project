package applicationserver;

import interfaces.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;

import static constants.ServiceConstants.LOGIN_SERVICE;
import static constants.ServiceConstants.SERVER_DISPATCHER_SERVICE;

public class ApplicationServer extends UnicastRemoteObject implements ApplicationServerInterface {

    //Dit is het aantal slots dat een server kan leveren.
    //Wanneer een game wordt opgestart met 4 players zullen 4 van die tickets worden afgetrokken
    private static final int SERVER_CAPACITY = 20;
    private static ApplicationServer instance;

    static {
        try {
            instance = new ApplicationServer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private int freeSlots = SERVER_CAPACITY;
    private String name;
    private String ip;
    private int port;
    private int restPort;
    private int dispatcherPort;
    private String dispatcherIp;
    private DatabaseInterface db;
    private AppLoginInterface appLogin;
    private LobbyInterface lobby;
    private ApplicationServerInterface backupServer;
    private ServerDispatcherInterface dispatcher;

    private ApplicationServer() throws RemoteException {
    }

    public static ApplicationServer getInstance() {
        return instance;
    }

    public void init(String name, String ip, int port, int restPort, String dispatcherIp, int dispatcherPort) {
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.restPort = restPort;
        this.dispatcherPort = dispatcherPort;
        this.dispatcherIp = dispatcherIp;

        lobby = Lobby.getInstance();

        startLogin();
        System.out.println("INFO: up and running on port: " + port);
        registerWithDispatcher();
        System.out.println("INFO: connection made to database and dispatch");
    }

    @Override
    public void startLogin() {
        try {
            appLogin = AppLogin.getInstance();
            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind(LOGIN_SERVICE, appLogin);
        } catch (RemoteException re) {
            re.printStackTrace();
        }
    }

    @Override
    public void registerWithDispatcher() {
        try {
            Registry registry = LocateRegistry.getRegistry(dispatcherIp, dispatcherPort);
            dispatcher = (ServerDispatcherInterface) registry.lookup(SERVER_DISPATCHER_SERVICE);
            //We krijgen een link naar de databank terug die deze server zal gebruiken
            db = dispatcher.registerApplicationServer((ApplicationServerInterface) this);

            //Maak de lobby klaar voor gebruik
            ((Lobby)lobby).init(this, db, dispatcher);

            //Maak login klaar voor gebruik
            appLogin.init(dispatcher, this, db, lobby);

        } catch (NotBoundException | RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean canFit(int slots) {
        return freeSlots - slots >= 0;
    }

    /*
     * Getters & Setters
     * */

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getIp() {
        return ip;
    }


    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public int getRestPort() {
        return restPort;
    }

    public void setRestPort(int restPort) {
        this.restPort = restPort;
    }

    @Override
    public int getDispatcherPort() {
        return dispatcherPort;
    }

    public void setDispatcherPort(int dispatcherPort) {
        this.dispatcherPort = dispatcherPort;
    }

    @Override
    public String getDispatcherIp() {
        return dispatcherIp;
    }

    public void setDispatcherIp(String dispatcherIp) {
        this.dispatcherIp = dispatcherIp;
    }

    @Override
    public DatabaseInterface getDb() {
        return db;
    }

    @Override
    public void setDb(DatabaseInterface db) {
        this.db = db;
    }

    @Override
    public AppLoginInterface getAppLogin() {
        return appLogin;
    }

    @Override
    public void setAppLogin(AppLoginInterface appLogin) {
        this.appLogin = appLogin;
    }

    @Override
    public LobbyInterface getLobby() {
        return lobby;
    }

    @Override
    public void setLobby(LobbyInterface lobby) {
        this.lobby = lobby;
    }

    @Override
    public ApplicationServerInterface getBackupServer() {
        return backupServer;
    }

    @Override
    public void setBackupServer(ApplicationServerInterface backupServer) {
        this.backupServer = backupServer;
    }

    public int getFreeSlots() {
        return freeSlots;
    }

    public void setFreeSlots(int slots) {
        freeSlots = slots;
    }

    public void reduceFreeSlots(int slots) {
        freeSlots -= slots;
    }

    public void addFreeSlots(int slots) {
        freeSlots += slots;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApplicationServer)) return false;
        if (!super.equals(o)) return false;
        ApplicationServer that = (ApplicationServer) o;
        return port == that.port &&
                restPort == that.restPort &&
                Objects.equals(name, that.name) &&
                Objects.equals(ip, that.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, ip, port, restPort);
    }
}