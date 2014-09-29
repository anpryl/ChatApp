package com.aprylutskyi.chat.client.util;

import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;

public class Closer {

    private static final Logger LOGGER = Logger.getLogger(Closer.class);
    
    private Closer() {
    }

    public static void close(Closeable toClose) {
        if (toClose != null) {
            try {
                toClose.close();
            } catch (IOException e) {
               LOGGER.error("Error occured whiel closing stream: " + toClose);
               LOGGER.debug(e.getMessage());;
            }
        }
    }

}
