package com.github.kardzhaliyski.tomcatclone;

import com.github.kardzhaliyski.tomcatclone.dispatcher.ServletDispatcher;
import com.github.kardzhaliyski.tomcatclone.http.HttpServlet;
import com.github.kardzhaliyski.tomcatclone.http.servlet.StaticContentServlet;

import java.nio.file.Path;

public class ServletContext {
    public String path;
    public ServletDispatcher dispatcher;
    public Path root;
    public boolean showDirectoryContent;
    private HttpServlet staticContentServlet = null;// /blog/index.html

    public ServletContext(String path, ServletDispatcher dispatcher, String root) {
        this.path = path;
        this.dispatcher = dispatcher;
        this.dispatcher.setServletContext(this);
        this.root = Path.of(root);
    }

    public HttpServlet getStaticContentServlet() {
        if(staticContentServlet == null) {
            staticContentServlet = new StaticContentServlet();
            staticContentServlet.setServletContext(this);
        }

        return staticContentServlet;
    }
}
