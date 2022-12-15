package server;

import org.apache.commons.cli.*;
import server.dispatcher.ServletDispatcher;
import server.utils.CliOptions;


public class Main {
    public static HelpFormatter helpFormatter = new HelpFormatter();

    public static void main(String[] args) {
        CommandLine cli;
        try {
            cli = new DefaultParser().parse(CliOptions.getOptions(), args);
        } catch (ParseException e) {
            System.out.println("Invalid Input!");
            return;
        }

        if (cli.getOptionValue(CliOptions.HELP) != null) {
            printUsage();
            return;
        }

        ServerBuilder builder = new ServerBuilder(cli);
        try {
            ServletDispatcher servletDispatcher = new ServletDispatcher("src/main/java/webapps/blog/web.xml"); //todo
            builder.setServletDispatcher(servletDispatcher);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(5);
        }

        Server server = builder.build();

        server.start();
    }

    public static void printUsage() {
        helpFormatter.printHelp("server.http-server [PATH] [OPTIONS]", CliOptions.getOptions());
    }
}