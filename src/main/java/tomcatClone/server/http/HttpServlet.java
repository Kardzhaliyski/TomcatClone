package server.http;

import server.ServletContext;
import servlet.Utils;

import java.io.IOException;

public class HttpServlet {
    private ServletContext servletContext = null;


    public HttpServlet() {
        init();
    }

    protected void init() {}
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Utils.writeErrorAsJson(resp,405, "Method not supported");
    }
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Utils.writeErrorAsJson(resp,405, "Method not supported");
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Utils.writeErrorAsJson(resp,405, "Method not supported");
    }
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Utils.writeErrorAsJson(resp,405, "Method not supported");
    }

    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        switch (req.getMethod().toUpperCase()) {
            case "GET" -> doGet(req, resp);
            case "POST" -> doPost(req,resp);
            case "PUT" -> doPut(req,resp);
            case "DELETE" -> doDelete(req, resp);
            default -> {
                Utils.writeErrorAsJson(resp,405, "Method not supported");
            }
        }
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
