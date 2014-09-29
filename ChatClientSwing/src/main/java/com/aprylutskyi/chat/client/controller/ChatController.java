package com.aprylutskyi.chat.client.controller;

import com.aprylutskyi.chat.client.connection.ConnectionManagerThread;
import com.aprylutskyi.chat.client.dto.*;
import com.aprylutskyi.chat.client.view.FrameManager;

public class ChatController {

    private ConnectionManagerThread connectionManagerThread;

    private FrameManager frameManager;

    public ChatController(ConnectionManagerThread connectionManagerThread, FrameManager frameManager) {
        this.connectionManagerThread = connectionManagerThread;
        this.frameManager = frameManager;
    }

    public void initialConnection(UserDto owner, ConfigurationDto configurationDto) {
        connectionManagerThread.initialConnecion(owner, configurationDto);
    }

    public void onConnectionLost() {
        frameManager.onErrorFromServer(ErrorDto.DISCONNECTED);
    }

    public void transferMessageToView(MessageDto message) {
        frameManager.transferMessageToView(message);
    }

    public void updateOnlineUserList(UsersListDto users) {
        frameManager.updateOnlineUserList(users);
    }

    public void onErrorFromServer(ErrorDto error) {
        frameManager.onErrorFromServer(error);
    }

    public void transferMessageHistoryToView(MessagesListDto history) {
        frameManager.transferMessageHistoryToView(history);
    }

    public void clientDisconnect() {
        connectionManagerThread.clientDisconnect();
    }

    public void sendMessage(MessageDto messageDto) {
        connectionManagerThread.sendMessage(messageDto);

    }

}
