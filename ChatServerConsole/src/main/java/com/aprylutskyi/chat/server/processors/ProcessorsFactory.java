package com.aprylutskyi.chat.server.processors;

import static com.aprylutskyi.chat.server.constants.DataConstants.ERROR_TAG;
import static com.aprylutskyi.chat.server.constants.DataConstants.MESSAGE_HISTORY_TAG;
import static com.aprylutskyi.chat.server.constants.DataConstants.MESSAGE_TAG;
import static com.aprylutskyi.chat.server.constants.DataConstants.ONLINE_USERS_TAG;
import static com.aprylutskyi.chat.server.constants.DataConstants.USER_TAG;

import java.util.HashMap;
import java.util.Map;

import com.aprylutskyi.chat.server.connection.ClientThread;
import com.aprylutskyi.chat.server.processors.impl.ErrorProcessor;
import com.aprylutskyi.chat.server.processors.impl.MessageProcessor;
import com.aprylutskyi.chat.server.processors.impl.MessagesListProcessor;
import com.aprylutskyi.chat.server.processors.impl.UserProcessor;
import com.aprylutskyi.chat.server.processors.impl.UsersListProcessor;

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
