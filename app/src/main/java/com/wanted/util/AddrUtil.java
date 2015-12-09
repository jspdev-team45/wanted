package com.wanted.util;

import com.wanted.ws.remote.ClientConstants;

import java.io.File;

/**
 * Created by xlin2 on 2015/11/24.
 */
public class AddrUtil {
    public String getAddress(String path) {
        return "http://" + ClientConstants.IP + ":" + ClientConstants.PORT +
                File.separator + "java_jsphdev_Project2s" + File.separator +
                path;
    }

    public String getImageAddress(String path) {
        return "http://" + ClientConstants.IP + ":" + ClientConstants.PORT +
                File.separator + "java_jsphdev_Project2s" + File.separator +
                "images" + File.separator + path;
    }
}
