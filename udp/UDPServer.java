/*
 * Created on 01-Mar-2016
 */
package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import common.MessageInfo;

public class UDPServer {

	private DatagramSocket recvSoc;
	private int totalMessages = -1;
	private int miss_count = 0;
	private int[] receivedMessages;
	private boolean close;

	private void run() {
		int				pacSize=0;
		byte[]			pacData;
		DatagramPacket 	pac;
		
		//Receive the messages and process them by calling processMessage().
		close=false;
		try {
			//keep receiving when last message not recieved
			while(!close) {
				pacSize=65507;
				pacData= new byte[pacSize];
				pac = new DatagramPacket(pacData,pacSize);
				recvSoc.setSoTimeout(30000);
				recvSoc.receive(pac);
				processMessage(new String(pac.getData(),0,pac.getLength()));
			}
		}catch (SocketTimeoutException e) {
			System.out.println("Timeout: " + e.getMessage());
			//If last message lost, still count total when timeout.
			if(totalMessages!=-1) {
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
		}catch (SocketException e){
			System.out.println("Socket: " + e.getMessage());
		}catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}
		// Use a timeout (30 secs) to ensure the program doesn't block forever

	}

	public void processMessage(String data) {

		MessageInfo msg = null;
		System.err.println("Received: " + data);

		// Use the data to construct a new MessageInfo object
		try {
			msg = new MessageInfo(data);
			// On receipt of first message, initialise the receive buffer
			if(totalMessages==-1) {
				totalMessages=msg.totalMessages;
				receivedMessages = new int[totalMessages];
			}
			// Log receipt of the message
			receivedMessages[msg.messageNum-1]=msg.messageNum;
			// If this is the last expected message, then identify
			// any missing messages
			if(msg.messageNum==totalMessages) {
				//close if last message received
				close=true;
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
		}catch (Exception e){
			System.out.println("Exception: " + e.getMessage());
		}
	}


	public UDPServer(int rp) {
		// Initialise UDP socket for receiving data
		try {
			recvSoc = new DatagramSocket(rp);
		}catch (SocketException e){
			System.out.println("Socket: " + e.getMessage());
			System.exit(-1);
		}
		// Done Initialisation
		System.out.println("UDPServer ready");
	}

	public static void main(String args[]) {
		int	recvPort;
		
		// Get the parameters from command line
		if (args.length < 1) {
			System.err.println("Arguments required: recv port");
			System.exit(-1);
		}
		recvPort = Integer.parseInt(args[0]);

		//Construct Server object and start it by calling run().
		UDPServer s= new UDPServer(recvPort);
		s.run();
		
	}

}
