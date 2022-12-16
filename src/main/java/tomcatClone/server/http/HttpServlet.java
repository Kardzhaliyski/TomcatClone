package server.http;

import java.io.IOException;

public class HttpServlet {

    public HttpServlet() {
        init();
    }

    protected void init() {}
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //todo add base implementation
    }
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //todo sent back method not supported code
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //todo sent back method not supported code
    }
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //todo sent back method not supported code
    }

    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        switch (req.getMethod().toUpperCase()) {
            case "GET" -> doGet(req, resp);
            case "POST" -> doPost(req,resp);
            case "PUT" -> doPut(req,resp);
            case "DELETE" -> doDelete(req, resp);
            default -> {
                return;//todo sent back method not supported code
            }
        }
    }


}
