package com.aprylutskyi.chat.client.connection;

import com.aprylutskyi.chat.client.constants.DataConstants;
import com.aprylutskyi.chat.client.constants.UserStatus;
import com.aprylutskyi.chat.client.controller.ChatController;
import com.aprylutskyi.chat.client.dto.*;
import com.aprylutskyi.chat.client.processors.DataProcessor;
import com.aprylutskyi.chat.client.processors.ProcessorsFactory;
import com.aprylutskyi.chat.client.util.Closer;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class ConnectionManagerThread implements Runnable {

    private final Logger LOGGER = Logger.getLogger(ConnectionManagerThread.class);

    private ChatController chatController;

    private Map<String, DataProcessor> dataProcessors;

    private DataOutputStream outbound;

    private DataInputStream inbound;

    private volatile Socket socket;

    private volatile UserDto owner;

    private volatile boolean socketIsOpened = false;

    private boolean fullyConnected = false;

    private boolean doWork = true;

    private ConfigurationDto config;

    public ConnectionManagerThread() {
        dataProcessors = ProcessorsFactory.getDataProcessors(this);
    }

    @Override
    public void run() {
        while (isDoWork()) {
            while (getSocket() != null && getOwner() != null && isSocketIsOpened()) {
                try {
                    InputStream inputStream = socket.getInputStream();
                    OutputStream outputStream = socket.getOutputStream();
                    inbound = new DataInputStream(inputStream);
                    outbound = new DataOutputStream(outputStream);
                    LOGGER.info("Connection obtained on socket: " + socket);
                    sendInitialConnectionData();
                    processData();
                } catch (IOException e) {
                    LOGGER.error("Connection lost");
                    if (fullyConnected) {
                        chatController.onConnectionLost();
                    }
                } finally {
                    closeConnection();
                }
            }
        }
    }

    public void initialConnecion(UserDto owner, ConfigurationDto configuration) {
        try {
            LOGGER.info("Initial connection");
            LOGGER.info(config);
            LOGGER.info(owner);
            this.owner = owner;
            this.config = configuration;
            socket = new Socket(config.getIpAddress(), config.getPort());
            socketIsOpened = true;
        } catch (IOException e) {
            LOGGER.error("Invalid connection parameters");
            LOGGER.debug(e.getMessage());
            closeConnection();
            chatController.onErrorFromServer(ErrorDto.INVALID_PARAMETERS);
        }
    }

    public void onErrorFromServer(ErrorDto error) {
        LOGGER.info("Error from server");
        closeConnection();
        chatController.onErrorFromServer(error);
    }

    public void transferMessageToView(MessageDto message) {
        chatController.transferMessageToView(message);
    }

    public void transferMessageHistory(MessagesListDto messagesList) {
        fullyConnected = true;
        chatController.transferMessageHistoryToView(messagesList);
    }

    public void updateOnlineUserList(UsersListDto users) {
        chatController.updateOnlineUserList(users);
    }

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }

    public void onAppClose() {
        closeConnection();
        setDoWork(false);
    }

    public void clientDisconnect() {
        if (owner != null) {
            LOGGER.info("Disconnecting from server");
            owner.setStatus(UserStatus.OFFLINE);
            sendData(DataConstants.USER_TAG, owner);
        }
        onAppClose();
    }

    public boolean isDoWork() {
        return doWork;
    }

    public void setDoWork(boolean doWork) {
        this.doWork = doWork;
    }

    private void processData() throws IOException {
        LOGGER.info("Start processing data on socket " + socket);
        while (true) {
            String data = inbound.readUTF();
            BufferedReader reader = new BufferedReader(new StringReader(data));
            processData(reader.readLine(), reader);
        }
    }

    private void processData(String tag, Reader reader) {
        LOGGER.info("Processing data with tag: " + tag);
        DataProcessor dataProcessor = dataProcessors.get(tag);
        if (dataProcessor != null) {
            dataProcessor.processData(reader);
        } else {
            LOGGER.error("Can't find data processor with tag: " + tag);
        }
    }

    private void sendData(String tag, Processable data) {
        LOGGER.info("Sending data with tag: " + tag);
        DataProcessor dataProcessor = dataProcessors.get(tag);
        if (dataProcessor != null) {
            dataProcessor.sendData(data, outbound);
        } else {
            LOGGER.error("Can't find data processor with tag: " + tag + " for data: " + data);
        }
    }

    private void sendInitialConnectionData() {
        sendData(DataConstants.USER_TAG, owner);
    }

    private void closeConnection() {
        if (socket != null) {
            LOGGER.info("Closing connection with " + socket);
        }
        socketIsOpened = false;
        Closer.close(outbound);
        Closer.close(inbound);
        Closer.close(socket);
        owner = null;
    }

    public void sendMessage(MessageDto messageDto) {
        sendData(DataConstants.MESSAGE_TAG, messageDto);
    }

    public boolean isFullyConnected() {
        return fullyConnected;
    }

    public void setFullyConnected(boolean fullyConnected) {
        this.fullyConnected = fullyConnected;
    }

    public UserDto getOwner() {
        return owner;
    }

    public void setOwner(UserDto owner) {
        this.owner = owner;
    }

    public boolean isSocketIsOpened() {
        return socketIsOpened;
    }

    public void setSocketIsOpened(boolean socketIsOpened) {
        this.socketIsOpened = socketIsOpened;
    }

    private Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
