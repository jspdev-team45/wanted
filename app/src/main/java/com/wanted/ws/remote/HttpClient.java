package com.wanted.ws.remote;

import com.wanted.entities.Pack;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by xlin2 on 2015/11/24.
 */
public class HttpClient implements HttpClientInterface, ClientConstants {
    private ObjectInputStream reader;
    private ObjectOutputStream writer;

    private URL url;
    private URLConnection connection;

    public HttpClient(URL url) {
        this.setUrl(url);
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public Pack sendToServer(Pack request) {
        Pack response = null;
        if (openConnection()){
            response = (Pack) handleSession(request);
            closeSession();
        }
        return response;
    }

    @Override
    public boolean openConnection() {
        try {
            connection = url.openConnection();
            connection.setDoOutput(true);
            //connection.setDoInput(true);
        } catch (IOException e) {
            if (DEBUG) System.err.println("Unable to connect " + url.toString());
            return false;
        } catch (Exception e) {
            if (DEBUG) System.err.println("Unable to connect " + url.toString());
            return false;
        }

        return true;
    }

    @Override
    public Object handleSession(Object request) {
        Object response = null;

        try {
            writer = new ObjectOutputStream(connection.getOutputStream());
            sendOutput(request);
            reader = new ObjectInputStream(connection.getInputStream());
            response = reader.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e){
            if (DEBUG) System.err.println("Unable to obtain stream to/from " + url.toString());
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

    @Override
    public void closeSession() {
        try {
            reader.close();
            writer.close();
            connection = null;
        } catch (IOException e) {
            if (DEBUG) System.err.println("Error closing connection to " + url.toString());
        } catch (Exception e) {
            if (DEBUG) System.err.println("Error closing connection to " + url.toString());
        }
    }
}
