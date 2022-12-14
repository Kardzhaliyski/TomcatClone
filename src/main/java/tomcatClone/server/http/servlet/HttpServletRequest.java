package server.http.servlet;

import server.http.HttpRequest;

import java.io.Reader;

public class HttpServletRequest {
    private final HttpRequest request;
    private String servletPath;
    private String pathInfo;

    public HttpServletRequest(HttpRequest httpRequest, String urlPattern) {
        this.request = httpRequest;
        setPathData(urlPattern);
    }

    private void setPathData(String urlPattern) {
        int i = urlPattern.indexOf('*');
        if (i == -1) {
            servletPath = urlPattern;
            pathInfo = null;
            return;
        }

        if (i > 0 && urlPattern.charAt(i - 1) == '/') {
            servletPath = urlPattern.substring(0, i - 1);
            pathInfo = "/" + urlPattern.substring(i + 1);
        }
    }

    public String getParameter(String name) {
        return request.params.get(name);
    }

    public Reader getReader() {
        return request.reader;
    }

    public String getPathInfo() {
        return pathInfo;
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        return null; //todo
    }

    public String getMethod() {
        return request.method;
    }

    public String getServletPath() {
        return servletPath;
    }

    public String getHeader(String name) {
        return request.headers.get(name);
    }
}
