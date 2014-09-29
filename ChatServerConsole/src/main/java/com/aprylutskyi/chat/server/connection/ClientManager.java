package com.aprylutskyi.chat.server.connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.AlreadyBoundException;

import org.apache.log4j.Logger;

import com.aprylutskyi.chat.server.connection.holders.ClientThreadsHolder;
import com.aprylutskyi.chat.server.connection.holders.MessageHistoryHolder;
import com.aprylutskyi.chat.server.dto.ConfigurationDto;
import com.aprylutskyi.chat.server.dto.MessageDto;
import com.aprylutskyi.chat.server.dto.MessagesListDto;
import com.aprylutskyi.chat.server.dto.UserDto;
import com.aprylutskyi.chat.server.dto.UsersListDto;
import com.aprylutskyi.chat.server.util.Closer;
import com.aprylutskyi.chat.server.util.TimeHelper;

public class ClientManager implements Runnable {

    private final Logger LOGGER = Logger.getLogger(ClientManager.class);

    private ConfigurationDto config;

    private ServerSocket serverSocket;

    private boolean doWork = true;

    private ClientThreadsHolder clientThreads;

    private MessageHistoryHolder historyHolder;

    public ClientManager(ConfigurationDto config) {
        this.config = config;
        setClientThreads((new ClientThreadsHolder(config.getMaxUsers())));
        setHistoryHolder(new MessageHistoryHolder(config.getMessageHistorySize()));
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(config.getPort());
            LOGGER.info("Server started: " + serverSocket);
            while (doWork) {
                Socket clientSocket = serverSocket.accept();
                LOGGER.info("Client connected on socket: " + clientSocket);
                startClientThread(clientSocket);
            }
        } catch (AlreadyBoundException abe) {
            LOGGER.error("Port " + config.getPort() + " already used by aanother application"
                    + "\nChange port in settings.properties");
        } catch (IOException e) {
            if (!doWork) {
                LOGGER.info("Closing server");
            } else {
                LOGGER.error("Connection error occured, please restart server");
                LOGGER.debug(e.getMessage());
            }
        } finally {
            shutdownServer();
        }
    }

    public void shutdownServer() {
        doWork = false;
        Closer.close(serverSocket);
    }

    public void sendMessageToAllClients(MessageDto message) {
        LOGGER.info("New message: " + message);
        historyHolder.add(message);
        getClientThreads().sendMessageToAll(message);
    }

    public MessagesListDto getMessageHistory() {
        return getHistoryHolder().getMessageHistory();
    }

    public UsersListDto getOnlineUserListFor(UserDto user) {
        synchronized (getClientThreads()) {
            MessageDto newOnlineUserMessage = getNewOnlineUserMessage(user);
            getClientThreads().sendMessageToAll(newOnlineUserMessage);
            return getClientThreads().getOnlineUsers();
        }
    }

    public void removeClientThread(ClientThread clientThread) {
        synchronized (getClientThreads()) {
            getClientThreads().remove(clientThread);
            UserDto owner = clientThread.getOwner();
            if (owner != null) {
                LOGGER.info("User disconnected " + owner);
                getClientThreads().sendMessageToAll(getOfflineUserMessage(owner));
                getClientThreads().sendOnlineListToAll();
            }
        }
    }

    public boolean addClientThread(ClientThread clientThread) {
        return getClientThreads().add(clientThread);
    }

    private void startClientThread(Socket socket) {
        ClientThread clientThread = new ClientThread(this, socket);
        LOGGER.debug("Starting cliend thread from socket " + socket);
        new Thread(clientThread).start();
    }

    private MessageDto getNewOnlineUserMessage(UserDto user) {
        MessageDto messageDto = new MessageDto();
        messageDto.setAuthor("Server");
        messageDto.setDate(TimeHelper.getNow());
        messageDto.setText(user.getName() + " connected to chat");
        return messageDto;
    }

    private MessageDto getOfflineUserMessage(UserDto user) {
        MessageDto messageDto = new MessageDto();
        messageDto.setAuthor("Server");
        messageDto.setDate(TimeHelper.getNow());
        messageDto.setText(user.getName() + " disconnected from chat");
        return messageDto;
    }

    public ClientThreadsHolder getClientThreads() {
        return clientThreads;
    }

    public void setClientThreads(ClientThreadsHolder clientThreads) {
        this.clientThreads = clientThreads;
    }

    public MessageHistoryHolder getHistoryHolder() {
        return historyHolder;
    }

    public void setHistoryHolder(MessageHistoryHolder historyHolder) {
        this.historyHolder = historyHolder;
    }

    public void sendOnlineUserListToAll() {
        clientThreads.sendOnlineListToAll();
    }

    public boolean disconnectUserWithName(String userToKill) {
        return clientThreads.removeClient(userToKill);
    }

    public UsersListDto getOnlineUserList() {
        return clientThreads.getOnlineUsers();
    }
}
