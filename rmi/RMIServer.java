/*
 * Created on 01-Mar-2016
 */
package rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

import common.*;

public class RMIServer extends UnicastRemoteObject implements RMIServerI {

	private int totalMessages = -1;
	private int[] receivedMessages;
	private int count = 0;
	private int miss_count = 0;

	public RMIServer() throws RemoteException {
	}

	public void receiveMessage(MessageInfo msg) throws RemoteException {
		System.err.println("Received: " + msg.toString());
		// TO-DO: On receipt of first message, initialise the receive buffer
		if(count==0) {
			totalMessages=msg.totalMessages;
			receivedMessages = new int[totalMessages];
		}
		// TO-DO: Log receipt of the message
		receivedMessages[count++]=msg.messageNum;
		// TO-DO: If this is the last expected message, then identify
		//        any missing messages
		if(count==totalMessages) {
			for(int i=0;i<totalMessages;i++) {
				if(receivedMessages[i]!=i+1) {
					System.out.println("Missing: " + (i+1));
					miss_count++;
				}
			}
			System.out.println("\n"+"Received "+(count-miss_count));
			System.out.println("Missed "+(miss_count));
			System.out.println("Miss "+(100*miss_count/count)+"%");
			//System.exit(0);
		}
	}


	public static void main(String[] args) {

		RMIServer rmis = null;

		// TO-DO: Initialise Security Manager
		if(System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		// TO-DO: Instantiate the server class
		try {
		rmis= new RMIServer();
		// TO-DO: Bind to RMI registry
		rebindServer("rmi://localhost:1234/RMIServer", rmis);
		}catch(RemoteException e) {
			System.out.println("Remote Exception:" + e.getMessage());
		}
	}

	protected static void rebindServer(String serverURL, RMIServer server) {
		try {
		// TO-DO:
		// Start / find the registry (hint use LocateRegistry.createRegistry(...)
		LocateRegistry.createRegistry(1234);
		// If we *know* the registry is running we could skip this (eg run rmiregistry in the start script)

		// TO-DO:
		// Now rebind the server to the registry (rebind replaces any existing servers bound to the serverURL)
		// Note - Registry.rebind (as returned by createRegistry / getRegistry) does something similar but
		// expects different things from the URL field.
		Naming.rebind(serverURL, server);
		}catch(MalformedURLException e) {
			System.out.println("Malformed URL Exception:" + e.getMessage());
		}catch(RemoteException e) {
			System.out.println("Remote Exception:" + e.getMessage());
		}
	}
}