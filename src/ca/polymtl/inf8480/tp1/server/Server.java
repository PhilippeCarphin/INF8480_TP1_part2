package ca.polymtl.inf8480.tp1.server;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import ca.polymtl.inf8480.tp1.shared.ServerInterface;
import ca.polymtl.inf8480.tp1.shared.SyncedFile;

public class Server implements ServerInterface {

	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

	public Server() {
		super();
	}

	private void run() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
			ServerInterface stub = (ServerInterface) UnicastRemoteObject
					.exportObject(this, 0);

			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("server", stub);
			System.out.println("Server ready.");
		} catch (ConnectException e) {
			System.err
					.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancer");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
	}

	@Override
	public void createClientID() throws RemoteException {}
	
	@Override
	public void create(String nom) throws RemoteException {}

	@Override
	public String[] list() throws RemoteException {return new String [0];}

	@Override
	public SyncedFile[] syncLocalDirectory() throws RemoteException  {return new SyncedFile[0];}

	@Override
	public SyncedFile get(String nom, long checksum) throws RemoteException {return new SyncedFile("");}

	@Override
	public SyncedFile lock(String nom, int clientID, long checksum) throws RemoteException {return new SyncedFile("");}

	@Override
	public SyncedFile push(String nom, byte[] contenu, int clientID) throws RemoteException {return new SyncedFile("");}
}
