package server.http;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    public String method;
    public String path;
    public String protocol;
    public Map<String, String> headers;
    public byte[] body;

    public HttpRequest(InputStream inputStream) {
        InputStreamReader in = new InputStreamReader(inputStream);
        String line = readLine(in);
        String[] info = line.split(" ");
        method = info[0];
        path = info[1];
        if(path.startsWith("/")) {
            path = path.substring(1);
        }

        protocol = info[2];
        headers = new HashMap<>();
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

        if(line == null) {
            body = new byte[0];
            return;
        }

//        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
//        int r;
//        while ((r = inputStream.read()) != -1) {
//            System.out.println();
//            byteBuffer.write(r);
//        }
//
//        body = byteBuffer.toByteArray();
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