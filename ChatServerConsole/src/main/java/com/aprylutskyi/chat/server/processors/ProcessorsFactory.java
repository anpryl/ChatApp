package com.aprylutskyi.chat.server.processors;

import com.aprylutskyi.chat.server.connection.ClientThread;
import com.aprylutskyi.chat.server.processors.impl.*;

import java.util.HashMap;
import java.util.Map;

import static com.aprylutskyi.chat.server.constants.DataConstants.*;

public class ProcessorsFactory {

    public static Map<String, DataProcessor> getDataProcessors(ClientThread clientThread) {
        Map<String, DataProcessor> dataProcessors = new HashMap<>();
        addMessageProcessor(dataProcessors, clientThread);
        addMessagesProcessor(dataProcessors);
        addUserProcessor(dataProcessors, clientThread);
        addUsersProcessor(dataProcessors);
        addErrorProcessor(dataProcessors, clientThread);
        return dataProcessors;
    }

    private static void addUsersProcessor(Map<String, DataProcessor> dataProcessors) {
        dataProcessors.put(ONLINE_USERS_TAG, new UsersListProcessor());
    }

    private static void addUserProcessor(Map<String, DataProcessor> dataProcessors, ClientThread clientThread) {
        dataProcessors.put(USER_TAG, new UserProcessor(clientThread));
    }

    private static void addMessagesProcessor(Map<String, DataProcessor> dataProcessors) {
        dataProcessors.put(MESSAGE_HISTORY_TAG, new MessagesListProcessor());
    }

    private static void addErrorProcessor(Map<String, DataProcessor> dataProcessors, ClientThread clientThread) {
        dataProcessors.put(ERROR_TAG, new ErrorProcessor(clientThread));
    }

    private static void addMessageProcessor(Map<String, DataProcessor> dataProcessors, ClientThread clientThread) {
        dataProcessors.put(MESSAGE_TAG, new MessageProcessor(clientThread));
    }

}
