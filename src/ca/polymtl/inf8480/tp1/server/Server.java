package ca.polymtl.inf8480.tp1.server;

import java.io.File;
import java.io.IOException;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import ca.polymtl.inf8480.tp1.shared.ServerInterface;
import ca.polymtl.inf8480.tp1.shared.SyncedFile;
import ca.polymtl.inf8480.tp1.shared.Response;

public class Server implements ServerInterface {

	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

	private static String FS_ROOT = "ajpcfs"; // Yet another new file system made by Alexandre Jouy and Philippe Carphin
	private static String FILE_STORE = FS_ROOT + "/" + "files";
	private static String LOCK_FILES = FS_ROOT + "/" + "lock";

	private File fileStore = null;
	private File lockFiles = null;

	private void createDirectories(){
		fileStore.getParentFile().mkdir();
		fileStore.mkdir();
		lockFiles.getParentFile().mkdir();
		lockFiles.mkdir();
	}

	public Server() {
		super();

		fileStore = new File(FILE_STORE);
		lockFiles = new File(LOCK_FILES);

		createDirectories();
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
	public int createClientID() throws RemoteException {
		return -1;
	}

	@Override
	public Response create(String nom) throws RemoteException {

		Response resp = new Response();
		try {

			File f = new File(FILE_STORE + "/" + nom);

			/*
			 * Cette fonction retourne false si le fichier existe deja.
			 * L'operation est dite atomique du point de vue des systemes de fichiers.
			 */
			boolean fileCreated = f.createNewFile();
			resp.retval = fileCreated;
			if(fileCreated){
				resp.message = "File created successfully\n";
			} else {
				resp.message = "Could not create file\n";
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return resp;
	}

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
