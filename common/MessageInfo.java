/*
 * Created on 01-Mar-2016
 */
package common;

import java.io.Serializable;
/**
 * Utility class that encapsulates the message information to
 * be passed from client to server.  Information can be extracted
 * or constructed as a String for use by the UDP example.
 *
 */
public class MessageInfo implements Serializable {

	public static final long serialVersionUID = 52L;

	public int totalMessages;
	public int messageNum;

	public MessageInfo(int total, int msgNum) {
		totalMessages = total;
		messageNum = msgNum;
	}

	public MessageInfo(String msg) throws Exception {
		String[] fields = msg.split(";");
		if (fields.length!=2)
			throw new Exception("MessageInfo: Invalid string for message construction: " + msg);
		totalMessages = Integer.parseInt(fields[0]);
		messageNum = Integer.parseInt(fields[1]);
	}

	public String toString(){
		return new String(totalMessages+";"+messageNum/*+"\n"*/);
	}


}
