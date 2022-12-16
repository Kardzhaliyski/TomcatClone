package server.http;

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

    String setPath(String path) {
        path = extractParams(path);
//        if (path.startsWith("/")) {
//            path = path.substring(1);
//        }
        if(path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() -1);
        }

        return this.path = path;
    }

    private String extractParams(String path) {
        int i = path.indexOf("#");
        if (i != -1) {
            path = path.substring(0, i);
        }

        i = path.indexOf("?");
        if (i == -1) {
            return path;
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

            params.put(key, value);
        }

        return path.substring(0, i);
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