package com.aprylutskyi.chat.server.configuration;

import static com.aprylutskyi.chat.server.constants.ConfigConstants.DEFAULT_LOG_FILE_PATH;
import static com.aprylutskyi.chat.server.constants.ConfigConstants.DEFAULT_MAX_USERS;
import static com.aprylutskyi.chat.server.constants.ConfigConstants.DEFAULT_MESSAGE_HISTORY_SIZE;
import static com.aprylutskyi.chat.server.constants.ConfigConstants.DEFAULT_PORT;
import static com.aprylutskyi.chat.server.constants.ConfigConstants.LOG_FILE_PATH_KEY;
import static com.aprylutskyi.chat.server.constants.ConfigConstants.MAX_PORT;
import static com.aprylutskyi.chat.server.constants.ConfigConstants.MAX_USERS_KEY;
import static com.aprylutskyi.chat.server.constants.ConfigConstants.MESSAGE_HISTORY_SIZE_KEY;
import static com.aprylutskyi.chat.server.constants.ConfigConstants.MIN_PORT;
import static com.aprylutskyi.chat.server.constants.ConfigConstants.PORT_KEY;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.aprylutskyi.chat.server.dto.ConfigurationDto;

public class Configurer {

    private final Logger LOGGER = Logger.getLogger(Configurer.class);

    public ConfigurationDto getConfig(String filePath) {
        LOGGER.info("Start configuring server");
        Properties prop = loadFromFile(filePath);

        ConfigurationDto config = new ConfigurationDto();
        config.setLogFilePath(getLogFilePath(prop));
        config.setMaxUsers(getMaxUsers(prop));
        config.setMessageHistorySize(getHistorySize(prop));
        config.setPort(getPort(prop));

        LOGGER.info("Server configuration ended");
        LOGGER.info(config);

        return config;
    }

    private Properties loadFromFile(String filePath) {
        Properties prop = new Properties();
        if (filePath != null) {
            File propFile = new File(filePath);
            try (FileInputStream fileInputStream = new FileInputStream(propFile)) {
                prop.load(fileInputStream);
            } catch (IOException e) {
                LOGGER.info("Configuration file not found. Default configuration values will be applied");
            }
        }
        return prop;
    }

    private int getPort(Properties prop) {
        String portStr = prop.getProperty(PORT_KEY);
        int port = 0;
        try {
            port = Integer.parseInt(portStr);
            if (port < MIN_PORT || port > MAX_PORT) {
                port = DEFAULT_PORT;
            }
        } catch (NumberFormatException nfe) {
            port = DEFAULT_PORT;
            LOGGER.error("Invalid \"port\" value in settings.properties file! Using default value");
        }
        return port;
    }

    private int getHistorySize(Properties prop) {
        String historySizeStr = prop.getProperty(MESSAGE_HISTORY_SIZE_KEY);
        int historySize = 0;
        try {
            historySize = Integer.parseInt(historySizeStr);
        } catch (NumberFormatException nfe) {
            historySize = DEFAULT_MESSAGE_HISTORY_SIZE;
            LOGGER.error("Invalid \"history.size\" value in settings.properties file! Using default value");
        }
        return historySize;
    }

    private int getMaxUsers(Properties prop) {
        String maxUsersStr = prop.getProperty(MAX_USERS_KEY);
        int maxUsers = 0;
        try {
            maxUsers = Integer.parseInt(maxUsersStr);
            if (maxUsers < 1) {
                maxUsers = DEFAULT_MAX_USERS;
            }
        } catch (NumberFormatException nfe) {
            maxUsers = DEFAULT_MAX_USERS;
            LOGGER.error("Invalid \"max.users\" value in settings.properties file! Using default value");
        }
        return maxUsers;
    }

    private String getLogFilePath(Properties prop) {
        String logFilePath = prop.getProperty(LOG_FILE_PATH_KEY, DEFAULT_LOG_FILE_PATH);
        configLogger(logFilePath);
        return logFilePath;
    }

    private void configLogger(String logFilePath) {
        LoggerConfigurer.configureFileAppender(logFilePath);
    }

}
