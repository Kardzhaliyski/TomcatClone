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

    public ServletContext(String contextPath, File war) throws IOException, SAXException, ParserConfigurationException {
        root = war.toPath();//todo not sure
        Path webInfDir = root.resolve("WEB-INF");
        URL classesDir = webInfDir.resolve("classes").toFile().toURI().toURL();
        URL libDir = webInfDir.resolve("lib").toFile().toURI().toURL(); // add maybe
        URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[]{classesDir}, this.getClass().getClassLoader());
        ClassLoader pre = Thread.currentThread().getContextClassLoader();
//        InputStream webXmlIS = urlClassLoader.getResourceAsStream("WEB-INF/web.xml");
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document webXml = documentBuilder.parse(webInfDir.resolve("web.xml").toFile());
        try {
            Thread.currentThread().setContextClassLoader(urlClassLoader);
            this.dispatcher = new ServletDispatcher(urlClassLoader, webXml);
        } finally {
            Thread.currentThread().setContextClassLoader(pre);
        }
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
