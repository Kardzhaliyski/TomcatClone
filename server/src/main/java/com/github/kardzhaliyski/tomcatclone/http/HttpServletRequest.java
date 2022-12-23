package com.github.kardzhaliyski.tomcatclone.http;

import com.github.kardzhaliyski.tomcatclone.server.Server;

import java.io.Reader;
import java.util.Map;

public class HttpServletRequest {
    private final HttpRequest request;
    private String servletPath;
    private String pathInfo;

    public HttpServletRequest(HttpRequest request, String servletPath, String pathInfo) {
        this.request = request;
        this.servletPath = servletPath;
        this.pathInfo = pathInfo;
    }

    public HttpServletRequest(HttpServletRequest req) {
        this.request = req.request;
        this.servletPath = getServletPath();
        this.pathInfo = getPathInfo();
    }

    public void setPath(String path) {
        request.setPath(path);
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
        return Server.getInstance().getRequestDispatcher(path);
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


    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    public void setParams(Map<String, String> params) {
        this.request.params = params;
    }
}