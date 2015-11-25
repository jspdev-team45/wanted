package com.wanted.util;

/**
 * Created by xlin2 on 2015/11/23.
 */
public class FileUtil {
    public String getUniqueImageFilename() {
        return "img_"+ System.currentTimeMillis() + ".jpg";
    }
}
