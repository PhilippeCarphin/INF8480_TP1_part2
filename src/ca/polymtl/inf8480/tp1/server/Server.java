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

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Collections;

import java.io.FileReader;
import java.io.BufferedReader;

public class Server implements ServerInterface {

	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

	private static String FS_ROOT = "ajpcfs"; // Yet another new file system made by Alexandre Jouy and Philippe Carphin
	private static String FILE_STORE = FS_ROOT + "/" + "files";
	private static String LOCK_FILES = FS_ROOT + "/" + "lock";
	private static final String ID_FILENAME = FS_ROOT + "/" + "idFile.txt";

	private  ArrayList<Integer> idList = new ArrayList<Integer>(0);
	private File fileStore = null;
	private File lockFiles = null;
	private File idFile = null;

	private void createDirectories(){
		fileStore.getParentFile().mkdir();
		fileStore.mkdir();
		lockFiles.getParentFile().mkdir();
		lockFiles.mkdir();
		try {
			idFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Server() {
		super();

		fileStore = new File(FILE_STORE);
		lockFiles = new File(LOCK_FILES);
		idFile = new File(ID_FILENAME);


		createDirectories();
		try {
			readIDs();
		} catch (IOException e){
			e.printStackTrace();
		}
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
		int newID = getMaxId() + 1;
		addId(newID);
		showIds();
		return newID;
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

	public void addId(int id)
	{
		BufferedWriter idWriter = null;
		FileWriter idFile = null;
		try {
			// The true here says we're in append mode
			idFile = new FileWriter(ID_FILENAME, true);
			idWriter = new BufferedWriter(idFile);
			idWriter.append(String.valueOf(id) + "\n");
			idWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try{
				idWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		idList.add(id);
	}

	public void readIDs() throws IOException
	{
		FileReader fr = new FileReader(ID_FILENAME);
		BufferedReader br = new BufferedReader(fr);
		StringBuffer sb = new StringBuffer();
		String line;
		while( (line = br.readLine()) != null){
			try
			{
				idList.add(Integer.parseInt(line));
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
				return;
			}
		}
	}

	public boolean fileContainsID(Integer id)
	{
		boolean cont = idList.contains(id);
		if(cont)
			System.out.println("The id " + String.valueOf(id) + " is contained");
		else
			System.out.println("The id " + String.valueOf(id) + " is NOT contained");
		return idList.contains(id);
	}


	public void showIds()
	{
		System.out.println("========= Client IDs =============");
		for(Integer i : idList){
			System.out.println("Id : " + String.valueOf(i));
		}
	}

	public Integer getMaxId()
	{
		Integer max = -1;
		if(idList.size() != 0){
			max = Collections.max(idList);
		}
		return max;
	}
}
