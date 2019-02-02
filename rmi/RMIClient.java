/*
 * Created on 01-Mar-2016
 */
package rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import common.MessageInfo;

public class RMIClient {

	public static void main(String[] args) {

		RMIServerI iRMIServer = null;

		// Check arguments for Server host and number of messages
		if (args.length < 2){
			System.out.println("Needs 2 arguments: ServerHostName/IPAddress, TotalMessageCount");
			System.exit(-1);
		}

		String urlServer = new String("rmi://" + args[0] + ":1234/RMIServer");
		int numMessages = Integer.parseInt(args[1]);

		// Initialise Security Manager
		if(System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			System.out.println("Security Manager ready");
			// Bind to RMIServer
			iRMIServer=(RMIServerI) Naming.lookup(urlServer);
			System.out.println("Bind successful");
			// Attempt to send messages the specified number of times
			for(int i = 0; i < numMessages; i++) {
				MessageInfo msg= new MessageInfo(numMessages,i+1);
				System.err.println("Try Sending: " + msg.toString());
				iRMIServer.receiveMessage(msg);
				Thread.sleep(100);
				System.err.println("Sent: " + msg.toString());
			}
		}
		catch(RemoteException | MalformedURLException | NotBoundException e){
			System.out.println("Exception:" + e);
		}catch(Exception e) {
			System.out.println("Exception:" + e);
		}
	}
}
