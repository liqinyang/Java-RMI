/*
 * Created on 01-Mar-2016
 */
package rmi;

import java.net.MalformedURLException;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import common.*;

public class RMIServer extends UnicastRemoteObject implements RMIServerI {

	private int totalMessages = -1;
	private int[] receivedMessages;
	private int miss_count = 0;

	public RMIServer() throws RemoteException {
	}

	public void receiveMessage(MessageInfo msg) throws RemoteException {
		System.err.println("Received: " + msg.toString());
		// On receipt of first message, initialise the receive buffer
		if(totalMessages==-1) {
			totalMessages=msg.totalMessages;
			receivedMessages = new int[totalMessages];
		}
		//  Log receipt of the message
		receivedMessages[msg.messageNum-1]=msg.messageNum;
		// If this is the last expected message, then identify
		// any missing messages
		if(msg.messageNum==totalMessages) {
			for(int i=0;i<totalMessages;i++) {
				if(receivedMessages[i]!=i+1) {
					System.out.println("Missing: " + (i+1));
					miss_count++;
				}
			}
			System.out.println("\n"+"Received "+(totalMessages-miss_count));
			System.out.println("Missed "+(miss_count));
			System.out.println("Miss "+(100*miss_count/totalMessages)+"%");
		}
	}


	public static void main(String[] args) {
		RMIServer rmis = null;
//		if(args.length==1) {
//			System.setProperty("java.rmi.server.hostname",args[0]);
//		}
		System.setProperty("java.rmi.server.hostname","35.246.42.180");

		//  Initialise Security Manager
		if(System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		System.out.println("Security Manager ready");
		// Instantiate the server class
		try {
		rmis= new RMIServer();
		System.out.println("Server class Instantiated");
		// Bind to RMI registry
		String hostIP = InetAddress.getLocalHost().getHostAddress();
		System.out.println("Local IP: "+hostIP);
		rebindServer("rmi://"+hostIP+":1234/RMIServer", rmis);
		}catch(RemoteException e) {
			System.out.println("Remote Exception:" + e.getMessage());
		}
		catch(Exception e) {
			System.out.println("Exception:" + e.getMessage());
		}
	}

	protected static void rebindServer(String serverURL, RMIServer server) {
		try {
		// Start  the registry 
		LocateRegistry.createRegistry(1234);
		System.out.println("Registry created");

		// Now rebind the server to the registry 

		Naming.rebind(serverURL, server);
		System.out.println("Bind successful");
		}catch(MalformedURLException e) {
			System.out.println("Malformed URL Exception:" + e.getMessage());
		}catch(RemoteException e) {
			System.out.println("Remote Exception:" + e.getMessage());
		}
	}
}
