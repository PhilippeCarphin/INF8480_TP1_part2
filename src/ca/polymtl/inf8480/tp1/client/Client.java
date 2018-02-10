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
		/*Le deuxième argument sert à faire des test de l'impact de la longueur sur le temps d'appel*/

		if (args.length > 0)
		{
			distantHostname = args[0];		
		}
		else
		{
			System.out.print("You need to use one of the following parameters :\n-\tlist\n-\tcreate\n-\tlock\n-\tget\n-\tsyncLocalDirectory\n");
			return;
		}

		Client client = new Client(distantHostname);
		client.runTests();
		
	}

	private ServerInterface distantServerStub = null;

	public Client(String distantServerHostname) {
		super();

		if (distantServerHostname != null) {
			distantServerStub = loadServerStub(distantServerHostname);
		}
	}
	
	public void runTests() {
		try {
			distantServerStub.create("fichier_test");
		} catch (RemoteException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	private ServerInterface loadServerStub(String hostname) {
		ServerInterface stub = null;

		try {
			Registry registry = LocateRegistry.getRegistry(hostname);
			stub = (ServerInterface) registry.lookup("server");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage()
					+ "' n'est pas défini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}

		return stub;
	}
}
