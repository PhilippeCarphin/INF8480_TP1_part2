package ca.polymtl.inf8480.tp1.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface ServerInterface extends Remote {
	void createClientID() throws RemoteException;
	void create(String nom) throws RemoteException;
	String[] list() throws RemoteException;
	SyncedFile[] syncLocalDirectory() throws RemoteException;
	SyncedFile get(String nom, long checksum) throws RemoteException;
	SyncedFile lock(String nom, int clientID, long checksum) throws RemoteException;
	SyncedFile push(String nom, byte[] contenu, int clientID) throws RemoteException;
}