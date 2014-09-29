package com.aprylutskyi.chat.client;

import com.aprylutskyi.chat.client.configuration.LoggerConfigurer;
import com.aprylutskyi.chat.client.connection.ConnectionManagerThread;
import com.aprylutskyi.chat.client.controller.ChatController;
import com.aprylutskyi.chat.client.view.FrameManager;

public class ClientStarter {

    public void init() {
        LoggerConfigurer.configureConsoleAppender();
        ConnectionManagerThread connectionManagerThread = new ConnectionManagerThread();
        FrameManager frameManager = new FrameManager();
        ChatController chatController = new ChatController(connectionManagerThread, frameManager);
        connectionManagerThread.setChatController(chatController);
        frameManager.setChatController(chatController);

        new Thread(connectionManagerThread).start();
        frameManager.init();
    }

}
