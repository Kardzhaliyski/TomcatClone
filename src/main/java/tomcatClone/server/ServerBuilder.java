package server;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import server.dispatcher.ServletDispatcher;
import server.utils.CliOptions;

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
    private ServletDispatcher servletDispatcher = null;

    public ServerBuilder() {
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
        return new Server(root, port, threadCount, showDirectoryContent, options, servletDispatcher);
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

    public ServerBuilder setServletDispatcher(ServletDispatcher dispatcher) {
        this.servletDispatcher = dispatcher;
        return this;
    }
}
