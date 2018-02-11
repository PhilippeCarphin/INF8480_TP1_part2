package ca.polymtl.inf8480.tp1.client;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import ca.polymtl.inf8480.tp1.shared.ServerInterface;
import ca.polymtl.inf8480.tp1.shared.Response;

public class Client {

	public static void main(String[] args) {
		String distantHostname = null;

		Client client = new Client(distantHostname);
		if( args.length == 0 )
		{
			client.runTests();
		}
		else
		{
			client.parseArgs(args);
			client.runCmd();
		}
	}
	private enum Command {CREATE, LIST, GET, SYNC, LOCK, PUSH}
	private ServerInterface distantServerStub = null;
	private ServerInterface localServerStub = null;
	private ServerInterface serverStub = null;
	private static final boolean USE_DISTANT_SERVER = false;
	private Command command = null;
	private String commandStr = null;
	private String argument = null;
	private static final String ID_FILENAME = "clientid.txt";
	private int clientID = -1;

	public Client(String distantServerHostname) {
		super();
		if( USE_DISTANT_SERVER ){
			if (distantServerHostname != null) {
				distantServerStub = loadServerStub(distantServerHostname);
				System.out.println("called loadServerStub with hostname " + distantServerHostname);
			}
			serverStub = distantServerStub;
		} else {
			localServerStub = loadServerStub("127.0.0.1");
			serverStub = localServerStub;
		}
	}

	public void runTests()
	{
		// testCreate(localServerStub);
		testCreateClientID();
		testMethod(localServerStub);
	}

	public void testMethod(ServerInterface si)
	{
		try
		{
			String[] files = serverStub.list();
			for (String s : files) {
				System.out.println(s);
			}
		}
		catch (RemoteException e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void testCreate(ServerInterface si)
	{
		try {
			si.create("fichier_test");
		} catch (RemoteException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	private void testCreateClientID()
	{
		getClientID();
	}

	public void runCmd()
	{
		switch(command){
			case CREATE:
				runCreate();
				break;
			default:
				System.out.println("Command " + commandStr + " not yet implemented");
				System.exit(1);
				break;
		}
	}

	private void runCreate()
	{
		Response resp = null;
		try {
			resp = serverStub.create(argument);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		System.out.println(resp.message);
	}

	private ServerInterface loadServerStub(String hostname) {
		ServerInterface stub = null;

		try {
			System.out.println("Calling LocateRegistry.getRegistry("
				+ hostname + ")");
			Registry registry = LocateRegistry.getRegistry(hostname);
			stub = (ServerInterface) registry.lookup("server");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage()
					+ "' n'est pas defini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}

		return stub;
	}

	public void parseArgs(String[] args){
		commandStr = args[0];

		if( commandStr.equals("create") ){
			command = Command.CREATE;
		} else if( commandStr.equals("list") ){
			command = Command.LIST;
		} else if (commandStr.equals("get") ){
			command = Command.GET;
		} else if (commandStr.equals("syndLocalDirectory") ){
			command = Command.LIST;
		} else if (commandStr.equals("lock") ){
			command = Command.LIST;
		} else if (commandStr.equals("push") ){
			command = Command.LIST;
		} else {
			System.out.print("You need to use one of the following parameters :"
					+ "\n-\tlist\n-\tcreate\n-\tlock\n-\tget\n-\tsyncLocalDirectory\n");
			System.exit(1);
		}

		if(args.length > 1)
			argument = args[1];
		validateArgs();
	}

	private void validateArgs(){
		// Check if command takes arguments
		switch(command){
			case CREATE:
			case GET:
			case PUSH:
			case LOCK:
				if( argument == null ){
					System.out.println("Command " + commandStr + " requires an argument");
					System.exit(1);
				}
				break;
			case LIST:
			case SYNC:
				if( argument != null ){
					System.out.println("Command " + commandStr + " does not take any arguments");
					System.exit(1);
				}
				break;
		}
	}

	private void saveClientID(int clientID)
	{

	}

	private void getClientID(){
		File idFile = new File(ID_FILENAME);

		if(idFile.exists()){
			readIdFromFile();
			System.out.println("Read ID " + String.valueOf(clientID) + " from file");
		} else {
			try {
				clientID = serverStub.createClientID();
			} catch ( IOException e){
				e.printStackTrace();
			}
			System.out.println("Got ID " + String.valueOf(clientID) + "  from server");
			// write ID to file
			writeIdToFile();
		}
	}

	private void writeIdToFile()
	{
		try {
			FileWriter fw = new FileWriter(ID_FILENAME);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(String.valueOf(clientID));
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void readIdFromFile()
	{
		String idStr = null;
		try {
			FileReader fr = new FileReader(ID_FILENAME);
			BufferedReader br = new BufferedReader(fr);
			idStr = br.readLine();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			clientID = Integer.parseInt(idStr);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
}
