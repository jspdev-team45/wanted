/**
 * @author Xi Lin
 */

package com.wanted.ws.remote;

import com.wanted.entities.Pack;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class DefaultSocketClient implements SocketClientInterface, ClientConstants {

	private ObjectInputStream reader;
    private ObjectOutputStream writer;
    private Socket socket;
    private String strHost;
    private int iPort;

    public DefaultSocketClient(String strHost, int iPort) {
    	this.setstrHost(strHost);
    	this.setiPort(iPort);
    }
    
	public void setstrHost(String strHost) {
		this.strHost = strHost;
	}
	
	public void setiPort(int iPort) {
		this.iPort = iPort;
	}
    
	public Pack sendToServer(Pack request) {
		Pack response = null;
		if (openConnection()){
			response = (Pack) handleSession(request);
			closeSession();
		}
		return response;
	}
   
	/**
	 * Create a socket and initialize reader and writer
	 */
	@Override
	public boolean openConnection() {
		try {
		    socket = new Socket(strHost, iPort);
		} catch(IOException socketError) {
		    if (DEBUG) System.err.println("Unable to connect " + strHost);
		    return false;
		}
		try {
			reader = new ObjectInputStream(socket.getInputStream());
			writer = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (Exception e){
		   if (DEBUG) System.err.println("Unable to obtain stream to/from " + strHost);
		   return false;
		}
		return true;

	}

	/**
	 * Send different objects to server according to user input and receive server messages
	 */
	@Override
	@SuppressWarnings("resource")
	public Object handleSession(Object request) {
		Object response = null;

		try {
			sendOutput(request);
			response = reader.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return response;
	}

	/**
	 * Send object to server 
	 */
	private void sendOutput(Object obj) throws IOException {
		writer.writeObject(obj);
		writer.flush();
	}

	/**
	 * Print a string 
	 */
	private void printString(String string) {
		System.out.println(string);
	}

	/**
	 * Close the connection and set writer and reader to be null 
	 */
	@Override
	public void closeSession() {
		try {
			writer.close();
			reader.close();
			socket.close();
		} catch (IOException e) {
			if (DEBUG) System.err.println("Error closing socket to " + strHost);
		}
		
	}

}
