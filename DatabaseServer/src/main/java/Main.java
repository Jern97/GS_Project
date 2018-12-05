import interfaces.DatabaseInterface;
import interfaces.DispatcherInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {

    private static final String DISPATCH_IP = "localhost";
    private static final int DISPATCH_PORT = 1000;
    private static String serverName;
    private static int serverPort;

    private static DispatcherInterface dispatcherImp;
    private static DatabaseInterface databaseImp;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 0) {
            serverName = args[0];
            serverPort = Integer.parseInt(args[1]);
        } else {
            serverName = "db_alpha";
            serverPort = 1100;
        }
        databaseImp = startRMI(serverName, serverPort);
        //dispatcherImp = registerDispatcher(serverName, serverPort);

        try {
            databaseImp.insertPhoto(0);
            databaseImp.insertPhoto(1);
            databaseImp.insertPhoto(2);
            databaseImp.insertPhoto(3);
            databaseImp.insertPhoto(4);
            databaseImp.insertPhoto(5);
            databaseImp.insertPhoto(6);
            databaseImp.insertPhoto(7);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public static DispatcherInterface registerDispatcher(String serverName, int port){

        //Registreren bij dispatcher
        try{
            Registry registry = LocateRegistry.getRegistry(DISPATCH_IP, DISPATCH_PORT);
            DispatcherInterface dispatcherImp = (DispatcherInterface) registry.lookup("dispatcher_service");
            dispatcherImp.registerDatabaseServer(serverName, port);
            System.out.println("INFO: "+ serverName+" up and running on port: "+port);
            return dispatcherImp;
        }
        catch(NotBoundException | RemoteException e){
            e.printStackTrace();
        }
        return null;
    }
    public static DatabaseInterface startRMI(String server_name, int port){
        try{
            databaseImp = new DatabaseImp(server_name);
            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind("database_service", databaseImp);
            return databaseImp;
        }
        catch(RemoteException re){
            re.printStackTrace();
        }
        return null;
    }

}
