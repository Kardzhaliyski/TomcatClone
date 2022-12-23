package com.github.kardzhaliyski.tomcatclone.http;

import com.github.kardzhaliyski.tomcatclone.dispatcher.PathData;
import com.github.kardzhaliyski.tomcatclone.utils.PathParser;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    public String method;
    public String path;
    public String protocol;
    public Map<String, String> headers = new HashMap<>();
    public Reader reader;
    public Map<String, String> params = new HashMap<>();

    public HttpRequest(InputStream inputStream) {
        InputStreamReader in = new InputStreamReader(inputStream);
        String line = readLine(in);
        String[] info = line.split(" ");
        method = info[0];
        setPath(info[1]);
        protocol = info[2];

        setHeaders(in);

        reader = in;
    }

    private void setHeaders(InputStreamReader in) {
        String line;
        while ((line = readLine(in)) != null) {
            if (line.isBlank()) {
                break;
            }

            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) != ':') {
                    continue;
                }

                String key = line.substring(0, i).trim();
                String value = line.substring(i + 1).trim();
                headers.put(key, value);
                break;
            }
        }
    }

    void setPath(String path) {
        PathData parse = PathParser.parse(path);

        this.path = parse.path;
        this.params = parse.params;
    }

    private StringBuilder rlSb;

    private String readLine(InputStreamReader in) {
        if (rlSb == null) {
            rlSb = new StringBuilder();
        } else {
            rlSb.setLength(0);
        }

        try {
            char c;
            while ((c = (char) in.read()) != (char) -1 &&
                    c != System.lineSeparator().charAt(0)) {
                rlSb.append(c);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return rlSb.length() == 0 ? null : rlSb.toString();
    }
}