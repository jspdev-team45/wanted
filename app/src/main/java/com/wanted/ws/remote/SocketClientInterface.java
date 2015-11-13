/**
 * @author Xi Lin
 */

package com.wanted.ws.remote;

import com.wanted.entities.Pack;

public interface SocketClientInterface {
	boolean openConnection();
    Pack handleSession(Pack request);
    void closeSession();
}
