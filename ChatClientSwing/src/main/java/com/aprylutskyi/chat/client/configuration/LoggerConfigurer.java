package com.aprylutskyi.chat.client.configuration;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class LoggerConfigurer {

    private static final String pattern = "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n";

    private LoggerConfigurer() {
    }

    public static void configureConsoleAppender() {
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.DEBUG);

        ConsoleAppender consoleAppender = new ConsoleAppender(new PatternLayout(pattern));
        consoleAppender.setTarget("System.out");
        rootLogger.addAppender(consoleAppender);
    }
}
