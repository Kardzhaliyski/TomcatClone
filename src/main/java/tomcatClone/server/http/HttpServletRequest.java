package server.http;

import server.Server;

import java.io.Reader;

public class HttpServletRequest {
    private final HttpRequest request;
    private String servletPath;
    private String pathInfo;

    public HttpServletRequest(HttpRequest request, String servletPath, String pathInfo) {
        this.request = request;
        this.servletPath = servletPath;
        this.pathInfo = pathInfo;
    }

    public HttpServletRequest(HttpServletRequest request, String servletPath, String pathInfo) {
        this.request = request.request;
        this.servletPath = servletPath;
        this.pathInfo = pathInfo;
    }

    public String setPath(String path) {
        return request.setPath(path);
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
    public String getProtocol() {
        return request.protocol;
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        return Server.getInstance().servletDispatcher.getRequestDispatcher(path);
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