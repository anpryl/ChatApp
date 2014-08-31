package com.aprylutskyi.chat.server.constants;

public class ConfigConstants {

    private ConfigConstants() {}

    public static final int DEFAULT_PORT = 3000;

    public static final int DEFAULT_MAX_USERS = 5;

    public static final int DEFAULT_MESSAGE_HISTORY_SIZE = 5;

    public static final String DEFAULT_LOG_FILE_PATH = "logs/serverLogs.log";

    public static final String PORT_KEY = "port";

    public static final String MAX_USERS_KEY = "max.users";

    public static final String MESSAGE_HISTORY_SIZE_KEY = "message.history.size";

    public static final String LOG_FILE_PATH_KEY = "log.file.path";

    public static final int MAX_PORT = 65535;

    public static final int MIN_PORT = 1;
}
