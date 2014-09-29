package com.aprylutskyi.chat.client.processors;

import com.aprylutskyi.chat.client.connection.ConnectionManagerThread;
import com.aprylutskyi.chat.client.processors.impl.*;

import java.util.HashMap;
import java.util.Map;

import static com.aprylutskyi.chat.client.constants.DataConstants.*;

public class ProcessorsFactory {

    public static Map<String, DataProcessor> getDataProcessors(ConnectionManagerThread connectionManager) {
        Map<String, DataProcessor> dataProcessors = new HashMap<>();
        addMessageProcessor(dataProcessors, connectionManager);
        addMessagesProcessor(dataProcessors, connectionManager);
        addUserProcessor(dataProcessors);
        addUsersProcessor(dataProcessors, connectionManager);
        addErrorProcessor(dataProcessors, connectionManager);
        return dataProcessors;
    }

    private static void addUsersProcessor(Map<String, DataProcessor> dataProcessors, ConnectionManagerThread connectionManager) {
        dataProcessors.put(ONLINE_USERS_TAG, new UsersListProcessor(connectionManager));
    }

    private static void addUserProcessor(Map<String, DataProcessor> dataProcessors) {
        dataProcessors.put(USER_TAG, new UserProcessor());
    }

    private static void addMessagesProcessor(Map<String, DataProcessor> dataProcessors, ConnectionManagerThread connectionManager) {
        dataProcessors.put(MESSAGE_HISTORY_TAG, new MessagesListProcessor(connectionManager));
    }

    private static void addErrorProcessor(Map<String, DataProcessor> dataProcessors, ConnectionManagerThread connectionManager) {
        dataProcessors.put(ERROR_TAG, new ErrorProcessor(connectionManager));
    }

    private static void addMessageProcessor(Map<String, DataProcessor> dataProcessors, ConnectionManagerThread connectionManager) {
        dataProcessors.put(MESSAGE_TAG, new MessageProcessor(connectionManager));
    }

}
