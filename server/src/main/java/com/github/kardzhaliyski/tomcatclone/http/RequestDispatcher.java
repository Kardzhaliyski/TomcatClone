package com.github.kardzhaliyski.tomcatclone.http;

import java.io.IOException;

public interface RequestDispatcher {
    void forward(HttpServletRequest req, HttpServletResponse resp) throws IOException;
}
