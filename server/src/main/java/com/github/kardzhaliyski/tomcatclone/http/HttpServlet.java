package com.github.kardzhaliyski.tomcatclone.http;

import com.github.kardzhaliyski.tomcatclone.server.ServletContext;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.PrintWriter;

public class HttpServlet {
    private ServletContext servletContext = null;

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public HttpServlet() {
        init();
    }

    protected void init() {
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        writeErrorAsJson(resp, 405, "Method not supported");
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        writeErrorAsJson(resp, 405, "Method not supported");
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        writeErrorAsJson(resp, 405, "Method not supported");
    }

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        writeErrorAsJson(resp, 405, "Method not supported");
    }

    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        switch (req.getMethod().toUpperCase()) {
            case "GET" -> doGet(req, resp);
            case "POST" -> doPost(req, resp);
            case "PUT" -> doPut(req, resp);
            case "DELETE" -> doDelete(req, resp);
            default -> {
                writeErrorAsJson(resp, 405, "Method not supported");
            }
        }
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public static void writeErrorAsJson(HttpServletResponse resp, int statusCode, String msg) throws IOException {
        String json = gson.toJson(msg);
        resp.setContentType("application/json");
        resp.setStatus(statusCode);
        PrintWriter writer = resp.getWriter();
        writer.println(json);
        writer.flush();
    }
}
