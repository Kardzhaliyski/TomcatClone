package com.github.kardzhaliyski.tomcatclone;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.github.kardzhaliyski.tomcatclone.dispatcher.ServletDispatcher;
import com.github.kardzhaliyski.tomcatclone.utils.CliOptions;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ServerBuilder {
    public static final int DEFAULT_PORT = 80;
    public static final int DEFAULT_THREAD_COUNT = 1;
    public static final String DEFAULT_DIRECTORY = System.getProperty("user.dir");

    private int port = DEFAULT_PORT;
    private int threadCount = DEFAULT_THREAD_COUNT;
    private boolean showDirectoryContent = false;
    private String root = DEFAULT_DIRECTORY;
    private Set<Option> options = null;
    private Map<String, ServletContext> contexts = new HashMap<>();


    public ServerBuilder() {
    }

    public ServerBuilder(String serverXml) {
        Path xmlPath = Path.of(serverXml);
        File xmlFile = xmlPath.toFile();
        if (!xmlFile.exists()) {
            System.err.println("Server.xml not found");
            System.exit(7);
        }

        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        Document doc;
        try {
            doc = documentBuilder.parse(xmlFile);
        } catch (SAXException | IOException e) {
            throw new RuntimeException("Error while parsing Server.xml file", e);
        }

        Node connector = doc.getElementsByTagName("Connector").item(0);
        Node portNode = connector.getAttributes().getNamedItem("port");
        int port = Integer.parseInt(portNode.getNodeValue());

        NodeList contextList = doc.getElementsByTagName("Context");
        for (int i = 0; i < contextList.getLength(); i++) {
            Node contextNode = contextList.item(i);
            NamedNodeMap attributes = contextNode.getAttributes();
            Node pathNode = attributes.getNamedItem("path");
            if (pathNode == null) {
                System.out.println("Invalid context element. Couldn't find path attribute.");
                continue;
            }


            Node docBaseNode = attributes.getNamedItem("docBase");
            if (docBaseNode == null) {
                System.out.println("Invalid context element. Couldn't find docBase attribute.");
            }

            String pathStr = pathNode.getNodeValue();
            String docBase = docBaseNode.getNodeValue();
            ServletDispatcher dispatcher = null;
            try {
                dispatcher = new ServletDispatcher(Path.of(docBase, "web.xml"));
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
                continue;
            }

            ServletContext servletContext = new ServletContext(pathStr, dispatcher, docBase);
            contexts.put(pathStr, servletContext);
        }
    }

    public ServerBuilder(CommandLine cli) {
        String[] args = cli.getArgs();
        if (args.length > 1) {
            System.out.println("Invalid input! Must not contain more then 1 path");
            Main.printUsage();
            System.exit(1);
        }

        if (args.length == 1) {
            root = args[0];
        }

        options = Set.of(cli.getOptions());
        for (Option option : options) {
            if (option.equals(CliOptions.PRINT_DIRECTORY)) {
                showDirectoryContent = true;
            } else if (option == CliOptions.PORT) {
                port = Integer.parseInt(option.getValue());
            } else if (option == CliOptions.THREADS) {
                threadCount = Integer.parseInt(option.getValue());
            }
        }
    }

    public Server build() {
        return new Server(root, port, threadCount, showDirectoryContent, options, contexts);
    }

    public ServerBuilder setPort(int port) {
        this.port = port;
        return this;
    }

    public ServerBuilder setThreadCount(int threadCount) {
        this.threadCount = threadCount;
        return this;
    }

    public ServerBuilder showDirectoryContent(boolean show) {
        this.showDirectoryContent = show;
        return this;
    }

    public ServerBuilder setRoot(String root) {
        this.root = root;
        return this;
    }

    public ServerBuilder addContext(ServletContext context) {
        contexts.put(context.path, context);
        return this;
    }
}
