package com.github.kardzhaliyski.tomcatclone.http;

public interface RequestDispatcher {
    void forward(HttpServletRequest req, HttpServletResponse resp);
}
