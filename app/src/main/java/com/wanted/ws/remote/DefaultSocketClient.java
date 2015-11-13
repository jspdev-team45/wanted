/**
 * @author Xi Lin
 */

package com.wanted.ws.remote;

import com.wanted.entities.Pack;
import com.wanted.entities.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

public class DefaultSocketClient implements SocketClientInterface, SocketClientConstants{

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
			response = handleSession(request);
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
	public Pack handleSession(Pack request) {
		Pack response = null;

		try {
			// ask user to enter command
			sendOutput(request);
			response = (Pack) reader.readObject();
			System.out.println("AAAAAAAAAAAAAAAAAAAAAaaaa");

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return response;
	}

	/**
	 * According to user input, do the corresponding jobs
	 */
//	@SuppressWarnings({ "resource", "unchecked" })
//	private void handleInput(String userInput) throws IOException, ClassNotFoundException {
//		CarModelOptionsIO io = new CarModelOptionsIO();
//		Scanner scanner = new Scanner(System.in);
//
//		// user wants to upload a Properties object
//		if (userInput.equalsIgnoreCase("U")) {
//			printString("Please input your file path.");
//			String filename = scanner.nextLine();
//			Properties prop = io.BuildPropertiesFromFile(filename);
//			sendOutput(prop);
//			printString((String) reader.readObject());
//		}
//
//		// user wants to configure a Automobile instance
//		else if (userInput.equalsIgnoreCase("C")) {
//			sendOutput("configuration");
//			ArrayList<String> modelList = (ArrayList<String>) reader.readObject();
//
//			// print list of Automobile names
//			printString("Here is the model list that you can configure:");
//			for (String name : modelList)
//				printString("--" + name);
//			printString("Please select one:");
//			String modelName = scanner.nextLine();
//			sendOutput(modelName);
//
//			// receive Automobile instance and let user to configure it
//			Automobile auto = (Automobile) reader.readObject();
//			if (auto == null) printString("Invalid model name!");
//			else io.selectCarOptions(auto);
//		}
//
//		else {
//			printString("Please enter 'U' for upload or 'C' for configuration");
//		}
//	}

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
			writer = null;
			reader = null;
			socket.close();
		} catch (IOException e) {
			if (DEBUG) System.err.println("Error closing socket to " + strHost);
		}
		
	}

}
