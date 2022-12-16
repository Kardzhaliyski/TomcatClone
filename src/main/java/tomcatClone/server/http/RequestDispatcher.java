package server.http;

public interface RequestDispatcher {
    void forward(HttpServletRequest req, HttpServletResponse resp);
}
