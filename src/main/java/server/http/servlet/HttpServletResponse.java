package server.http.servlet;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class HttpServletResponse {
    public static final int SC_CREATED = 201;
    public static final int SC_BAD_REQUEST = 400;
    public static final int SC_UNAUTHORIZED = 401;
    public static final int SC_NOT_FOUND = 404;

    public PrintWriter getWriter() throws IOException {
        return null;
    }

    public void setContentType(String type) {

    }

    public void addHeader(String name, String value){

    }

    public void sendRedirect(String path) {

    }

    public void setStatus(int statusCode) {
    }
}
