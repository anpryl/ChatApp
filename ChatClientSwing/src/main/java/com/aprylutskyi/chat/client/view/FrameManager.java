package com.aprylutskyi.chat.client.view;

import com.aprylutskyi.chat.client.controller.ChatController;
import com.aprylutskyi.chat.client.dto.*;
import com.aprylutskyi.chat.client.view.frames.ChatView;
import com.aprylutskyi.chat.client.view.frames.LoginView;

import java.util.List;

public class FrameManager {

    private ChatController chatController;

    private LoginView loginView;

    private ChatView chatView;

    private UserDto user;

    private ConfigurationDto config;

    public void init() {
        chatView = new ChatView(this);
        loginView = new LoginView(this);
        loginView.init();
    }

    public void clientDisconnect() {
        chatController.clientDisconnect();
    }

    public void initialConnection() {
        chatController.initialConnection(user, config);
    }

    public void initialConnection(UserDto owner, ConfigurationDto configurationDto) {
        user = owner;
        config = configurationDto;
        chatView.setUser(owner);
        chatController.initialConnection(owner, configurationDto);
    }

    public void transferMessageHistoryToView(MessagesListDto history) {
        loginView.setVisible(false);
        List<MessageDto> messages = history.getMessages();
        chatView.clearMessages();
        for (MessageDto messageDto : messages) {
            chatView.addMessage(messageDto);
        }
        chatView.setVisible(true);
        chatView.setTitle(user.getName());
        chatView.enableSendButton();
    }

    public void onConnectionLost() {
        if (loginView.isVisible()) {
            loginView.onConnectionLost();
        } else if (chatView.isVisible()) {
            chatView.onConnectionLost();
        }

    }

    public void transferMessageToView(MessageDto message) {
        chatView.addMessage(message);
    }

    public void updateOnlineUserList(UsersListDto users) {
        chatView.updateOnlineUserList(users);
    }

    public void onErrorFromServer(ErrorDto error) {
        if (chatView.isVisible()) {
            chatView.onError(error);
        } else if (loginView.isVisible()) {
            loginView.onError(error);
        }
    }

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }

    public void sendMessage(MessageDto messageDto) {
        chatController.sendMessage(messageDto);

    }

}
