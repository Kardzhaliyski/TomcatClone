package server;

import org.apache.commons.cli.Option;
import server.http.HttpTask;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static Server instance = null;
    private final int port;
    private final ExecutorService executorService;
    public boolean showDirectoryContent;
    public Path root;
    public Set<Option> options;

    public Server(String root, int port, int threadCount, boolean showDirectoryContent, Set<Option> options) {
        this.root = Path.of(root);
        this.port = port;
        this.executorService = Executors.newFixedThreadPool(threadCount);
        this.showDirectoryContent = showDirectoryContent;
        this.options = options;
        instance = this;
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
}
