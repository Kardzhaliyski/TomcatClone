package server.http.servlet;

import server.http.HttpRequest;
import server.utils.StatusCode;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class HttpServletResponse {
    public static final int SC_CREATED = 201;
    public static final int SC_BAD_REQUEST = 400;
    public static final int SC_UNAUTHORIZED = 401;
    public static final int SC_NOT_FOUND = 404;

    public String protocol;
    public StatusCode statusCode;
    private Map<String, String> headers = new HashMap<>();
    private boolean serverAcceptGzip = false;
//    private boolean headersSend = false;
    private PrintWriter writer;

    public HttpServletResponse(HttpRequest request, OutputStream outputStream) {
        this.protocol = request.protocol;
        this.statusCode = StatusCode.OK;

        String ae = request.headers.get("Accept-Encoding");
        if (ae != null && ae.contains("gzip")) {
            this.serverAcceptGzip = true;
        }

        writer = new PrintWriter(new OutputStreamWriter(outputStream));
    }

    public PrintWriter getWriter() throws IOException {
        sendHeaders();
        return writer;
    }

    private void sendHeaders() {
        StringBuilder sb = new StringBuilder();
        appendStatusLine(sb);
        addHeaders();
        appendHeaders(sb);
        sb.append(System.lineSeparator());
        writer.write(sb.toString());
        writer.flush();
    }

    public void setContentType(String type) {
        addHeader("content-type", type);
    }

    public void addHeader(String name, String value) {
        String v = headers.get(name);
        if (v != null) {
            value = v + ", " + value;
        }

        headers.put(name, value);
    }

    public void sendRedirect(String path) {
        //todo
    }

    public void setStatus(int statusCode) {
        this.statusCode = switch (statusCode){
            case 200 -> StatusCode.OK;
            case 201 -> StatusCode.CREATED;
            case 400 -> StatusCode.BAD_REQUEST;
            case 401 -> StatusCode.UNAUTHORIZED;
            case 404 -> StatusCode.NOT_FOUND;
            default -> throw new IllegalStateException("Unexpected value: " + statusCode);
        };
    }

    private void addHeaders() {
        headers.put("Date", LocalDateTime.now().toString());
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
}

