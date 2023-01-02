package com.github.kardzhaliyski.tomcatclone;

import com.github.kardzhaliyski.tomcatclone.server.Server;
import com.github.kardzhaliyski.tomcatclone.server.ServerBuilder;
import org.apache.commons.cli.*;
import com.github.kardzhaliyski.tomcatclone.utils.CliOptions;


public class Main {
    private static final String SERVER_XML_PATH = "server/src/main/resources/server.xml";
    public static HelpFormatter helpFormatter = new HelpFormatter();

    public static void main(String[] args) {
//        CommandLine cli;
//        try {
//            cli = new DefaultParser().parse(CliOptions.getOptions(), args);
//        } catch (ParseException e) {
//            System.out.println("Invalid Input!");
//            return;
//        }
//
//        if (cli.getOptionValue(CliOptions.HELP) != null) {
//            printUsage();
//            return;
//        }

        ServerBuilder builder = new ServerBuilder(SERVER_XML_PATH);
        Server server = builder.build();
        server.start();
    }

    public static void printUsage() {
        helpFormatter.printHelp("server.http-server [PATH] [OPTIONS]", CliOptions.getOptions());
    }
}