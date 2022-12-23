package com.github.kardzhaliyski.tomcatclone.utils;

import com.github.kardzhaliyski.tomcatclone.dispatcher.PathData;

public class PathParser {
    public static PathData parse(String path) {
        PathData pathData = new PathData();
        int i = path.indexOf("#");
        if (i != -1) {
            path = path.substring(0, i);
        }

        i = path.indexOf("?");
        if (i == -1) {
            pathData.path = path;
            return pathData;
        }

        String queryString = path.substring(i + 1);
        for (String s : queryString.split("&")) {
            String[] split = s.split("=");
            if(split.length < 2) {
                break;
            }

            String key = split[0];
            String value = split[1];

            if(key == null || value == null || key.isBlank() || value.isBlank()){
                continue;
            }

            pathData.params.put(key, value);
        }

        path = path.substring(0,i);
        if(path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() -1);
        }

        return pathData;
    }
}
