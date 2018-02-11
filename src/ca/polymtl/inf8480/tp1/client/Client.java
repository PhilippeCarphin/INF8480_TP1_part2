package ca.polymtl.inf8480.tp1.client;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import ca.polymtl.inf8480.tp1.shared.ServerInterface;

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

	public void runCmd(){
		try {
			switch(command){
				case CREATE:
					serverStub.create(argument);
					break;
				default:
					System.out.println("Command " + commandStr + " not yet implemented");
					System.exit(1);
					break;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private ServerInterface distantServerStub = null;
	private ServerInterface localServerStub = null;
	private ServerInterface serverStub = null;
	private static final boolean USE_DISTANT_SERVER = false;
	private Command command = null;
	private String commandStr = null;
	private String argument = null;

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

	public void runTests() {
		testCreate(localServerStub);
	}

	public void testCreate(ServerInterface si){
		try {
			si.create("fichier_test");
		} catch (RemoteException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
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
}
