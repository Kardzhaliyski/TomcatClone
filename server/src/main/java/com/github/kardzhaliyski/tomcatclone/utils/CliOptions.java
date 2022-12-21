package com.github.kardzhaliyski.tomcatclone.utils;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class CliOptions {
    public static final Option PORT = new Option("p", "port", true, "Port");
    public static final Option THREADS = new Option("t", "threads", true, "Thread count");
    public static final Option PRINT_DIRECTORY = new Option("d", "Print directory content");
    public static final Option HELP = new Option("h", "help", false, "Show commands descriptions.");
    public static final Option CACHED_COMPRESSION = new Option("g", "Sent compressed txt if already generated.");
    public static final Option REAL_TIME_COMPRESSION = new Option("c", "Send compressed text files and copress at the moment if not compressed already.");

    public static Options getOptions() {
        return new Options()
                .addOption(PORT)
                .addOption(THREADS)
                .addOption(PRINT_DIRECTORY)
                .addOption(HELP)
                .addOption(CACHED_COMPRESSION)
                .addOption(REAL_TIME_COMPRESSION);
    }
}
