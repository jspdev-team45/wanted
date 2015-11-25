package com.wanted.ws.remote;

import com.wanted.entities.Pack;

/**
 * Created by xlin2 on 2015/11/24.
 */
public interface HttpClientInterface {
    boolean openConnection();
    Object handleSession(Object request);
    void closeSession();
}
