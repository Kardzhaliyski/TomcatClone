package com.github.kardzhaliyski.tomcatclone.server;

import com.github.kardzhaliyski.tomcatclone.dispatcher.ServletDispatcher;
import com.github.kardzhaliyski.tomcatclone.http.*;
import com.github.kardzhaliyski.tomcatclone.http.servlet.StaticContentServlet;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ServletContext {
    public ServletDispatcher dispatcher;
    public Path root;
    public boolean showDirectoryContent;
    private HttpServlet staticContentServlet = null;// /blog/index.html

    public ServletContext(String path, ServletDispatcher dispatcher, String root) {
        this.dispatcher = dispatcher;
        this.dispatcher.setServletContext(this);
        this.root = Path.of(root);
    }

    public ServletContext(String contextPath, File dir) throws IOException, SAXException, ParserConfigurationException {
        root = dir.toPath(); //todo not sure
        Path webInfDir = root.resolve("WEB-INF");
        List<URL> urls = new ArrayList<>();
        urls.add(webInfDir.resolve("classes").toFile().toURI().toURL());
        File lib = webInfDir.resolve("lib").toFile();
        File[] files = lib.listFiles(n -> n.getName().endsWith(".jar"));
        for (File file : files) {
            URL url = file.toURI().toURL();
            urls.add(url);
        }

        URLClassLoader urlClassLoader = URLClassLoader.newInstance(urls.toArray(URL[]::new), this.getClass().getClassLoader());
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document webXml = documentBuilder.parse(webInfDir.resolve("web.xml").toFile());

//        ClassLoader prevCL = Thread.currentThread().getContextClassLoader();
//        try {
//            Thread.currentThread().setContextClassLoader(urlClassLoader);
            this.dispatcher = new ServletDispatcher(urlClassLoader, webXml);
            this.dispatcher.setServletContext(this);
//        } finally {
//            Thread.currentThread().setContextClassLoader(prevCL);
//        }
    }

    public HttpServlet getStaticContentServlet() {
        if(staticContentServlet == null) {
            staticContentServlet = new StaticContentServlet();
            staticContentServlet.setServletContext(this);
        }

        return staticContentServlet;
    }

    public void dispatch(HttpRequest request, Socket socket) throws IOException {
        dispatcher.dispatch(request, socket);
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        return dispatcher.getRequestDispatcher(path);
    }
}
