package server.http;

import server.utils.Gzip;
import server.utils.StatusCode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    public String protocol;
    public StatusCode statusCode;
    private Map<String, String> headers;
    private byte[] body;
    private File bodyFile = null;
    public boolean serverAcceptGzip = false;
    public boolean compressed = false;



    public HttpResponse(String protocol, StatusCode statusCode) {
        this.protocol = protocol;
        this.statusCode = statusCode;
        this.headers = new HashMap<>();
        this.body = new byte[0];
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void addBody(String body) {
        addBody(body.getBytes(StandardCharsets.UTF_8));
    }

    public void addBody(byte[] body) {
        this.body = body;
    }

    public void addBody(File file) {
        bodyFile = file;
    }


    public void send(OutputStream outputStream) throws IOException {
        if (bodyFile != null) {
            String contentType = getContentType();
            headers.put("Content-Type", contentType);
            if (serverAcceptGzip) {
                File compressedFile = Gzip.getCompressedVersion(bodyFile, contentType);
                compressed = compressedFile != bodyFile;
                bodyFile = compressedFile;
            }
        }

        StringBuilder sb = new StringBuilder();
        appendStatusLine(sb);
        addHeaders();
        appendHeaders(sb);
        sb.append(System.lineSeparator());

        OutputStream out = new BufferedOutputStream(outputStream);
        sentInfo(sb, out);
        sendBody(out);
    }

    private void addHeaders() {
        headers.put("Date", LocalDateTime.now().toString());

        Instant lastMod = bodyFile != null ?
                Instant.ofEpochMilli(bodyFile.lastModified()) :
                Instant.now();
        headers.put("Last-Modified", lastMod.toString());

        long bodyLength = bodyFile != null ?
                bodyFile.length() :
                body.length;
        headers.put("Content-Length", String.valueOf(bodyLength));

        if (compressed) {
            headers.put("Content-Encoding", "gzip");
        }
    }

    private void sendBody(OutputStream out) throws IOException {
        if (bodyFile != null) {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(bodyFile));
            byte[] buff = new byte[4096];
            int n;
            while ((n = in.read(buff)) != -1) {
                out.write(buff, 0, n);
                out.flush();
            }
        } else {
            out.write(body);
        }
        out.flush();
    }

    private static void sentInfo(StringBuilder sb, OutputStream out) throws IOException {
        out.write(sb.toString().getBytes());
        out.flush();
    }

    private void appendHeaders(StringBuilder sb) {
        for (Map.Entry<String, String> kvp : headers.entrySet()) {
            sb.append(kvp.getKey())
                    .append(": ")
                    .append(kvp.getValue())
                    .append(System.lineSeparator());
        }
    }

    private void appendStatusLine(StringBuilder sb) {
        sb.append(protocol)
                .append(" ")
                .append(statusCode.getCode())
                .append(" ")
                .append(statusCode.getMessage())
                .append(System.lineSeparator());
    }

    private String getContentType() {
        try {
            return Files.probeContentType(bodyFile.toPath());
        } catch (IOException e) {
            System.out.println("Error while getting Content-Type of: " + bodyFile.toPath());
            return "";
        }
    }
}
