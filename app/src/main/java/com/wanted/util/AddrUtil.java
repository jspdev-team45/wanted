package com.wanted.util;

import com.wanted.ws.remote.ClientConstants;

import java.io.File;

/**
 * Created by xlin2 on 2015/11/24.
 */
public class AddrUtil {
    public String getAddress(String servletName) {
        return "http://" + ClientConstants.IP + ":" + ClientConstants.PORT +
                File.separator + "java_jsphdev_Project2s" + File.separator +
                servletName;
    }
}
