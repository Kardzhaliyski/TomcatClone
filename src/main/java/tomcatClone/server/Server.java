package server;

import org.apache.commons.cli.Option;
import server.http.HttpRequest;
import server.http.HttpServletRequest;
import server.http.HttpTask;
import servlet.Utils;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {
    private static final Pattern PATH_MATCHING_PATTERN = Pattern.compile("^\\/(([a-zA-Z]+)(\\/(.*))?)?");
    private static Server instance = null;
    private final int port;
    private final ExecutorService executorService;
    public boolean showDirectoryContent;
    public Path root;
    public Set<Option> options;
    public Map<String,ServletContext> contexts;

    public Server(String root, int port, int threadCount, boolean showDirectoryContent, Set<Option> options, Map<String, ServletContext> contexts) {
        this.root = Path.of(root);
        this.port = port;
        this.executorService = Executors.newFixedThreadPool(threadCount);
        this.showDirectoryContent = showDirectoryContent;
        this.options = options;
        instance = this;
        this.contexts = contexts;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    executorService.submit(new HttpTask(this, socket));
                } catch (IOException e) {
                    System.out.println("Error while accepting socket connection");
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Error while opening server.Server socket.");
            e.printStackTrace();
            System.exit(4);
        }
    }

    public static Server getInstance() {
        return instance;
    }

    public void dispatch(HttpRequest request, Socket socket) throws IOException {
        Matcher matcher = PATH_MATCHING_PATTERN.matcher(request.path);
        if(!matcher.matches()) {
            Utils.writeHttpResponseAsJson(socket.getOutputStream(), 400, "Invalid request path");
            return;
        }

        String servletPath = matcher.group(2);
        ServletContext context = contexts.get(servletPath);
        if(context == null) {
            Utils.writeHttpResponseAsJson(socket.getOutputStream(), 404, "Resource not found");
            return;
        }

        request.path = request.path.substring(servletPath.length() + 1);
        context.dispatcher.dispatch(request, socket);
    }

    public HttpServletRequest getDispatcher(String path) {
        //todo
        return null;
    }
}
