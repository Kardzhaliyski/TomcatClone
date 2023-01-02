package com.github.kardzhaliyski.tomcatclone.http;

import com.github.kardzhaliyski.tomcatclone.dispatcher.PathData;
import com.github.kardzhaliyski.tomcatclone.utils.PathParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    public String method;
    public String path;
    public String protocol;
    public Map<String, String> headers = new HashMap<>();
    public Map<String, String> params = new HashMap<>();

    public HttpRequest(InputStream inputStream) {
        String line = readLine(inputStream);
        String[] info = line.split(" ");
        method = info[0];
        setPath(info[1]);
        protocol = info[2];

        setHeaders(inputStream);
    }

    private void setHeaders(InputStream inputStream) {
        String line;
        while ((line = readLine(inputStream)) != null) {
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

    private String readLine(InputStream in) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int b;
        try {
            while ((b = in.read()) != -1) {
                if ((char) b == '\n') {
                    break;
                }

                buffer.write(b);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String str = buffer.toString(StandardCharsets.UTF_8);
        return str.length() == 0 ? null : str;
    }
}