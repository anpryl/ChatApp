package com.aprylutskyi.chat.server.connection;

import static com.aprylutskyi.chat.server.constants.DataConstants.ERROR_TAG;
import static com.aprylutskyi.chat.server.constants.DataConstants.MESSAGE_HISTORY_TAG;
import static com.aprylutskyi.chat.server.constants.DataConstants.MESSAGE_TAG;
import static com.aprylutskyi.chat.server.constants.DataConstants.ONLINE_USERS_TAG;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.Socket;
import java.util.Map;

import org.apache.log4j.Logger;

import com.aprylutskyi.chat.server.dto.ErrorDto;
import com.aprylutskyi.chat.server.dto.MessageDto;
import com.aprylutskyi.chat.server.dto.MessagesListDto;
import com.aprylutskyi.chat.server.dto.Processable;
import com.aprylutskyi.chat.server.dto.UserDto;
import com.aprylutskyi.chat.server.dto.UsersListDto;
import com.aprylutskyi.chat.server.processors.DataProcessor;
import com.aprylutskyi.chat.server.processors.ProcessorsFactory;
import com.aprylutskyi.chat.server.util.Closer;

public class ClientThread implements Runnable {

    private final Logger LOGGER = Logger.getLogger(ClientThread.class);

    private Socket clientSocket;

    private DataInputStream inbound;

    private DataOutputStream outbound;

    private ClientManager clientManager;

    private Map<String, DataProcessor> dataProcessors;

    private UserDto owner;

    public ClientThread(ClientManager clientManager, Socket clientSocket) {
        this.clientManager = clientManager;
        this.clientSocket = clientSocket;
        setProcessors(ProcessorsFactory.getDataProcessors(this));
    }

    @Override
    public void run() {
        try {
            LOGGER.info("Gettin streams from socket " + clientSocket);
            inbound = new DataInputStream(clientSocket.getInputStream());
            outbound = new DataOutputStream(clientSocket.getOutputStream());
            processData();
        } catch (IOException e) {
            LOGGER.error("Problems with connection on socket: " + clientSocket);
            LOGGER.debug(e.getMessage());
        } finally {
            closeConnection();
            clientManager.removeClientThread(this);
            owner = null;
        }
    }

    public void sendMessage(MessageDto message) {
        DataProcessor dataProcessor = dataProcessors.get(MESSAGE_TAG);
        if (dataProcessor != null) {
            dataProcessor.sendData(message, outbound);
            LOGGER.info("Message sended to " + owner.getName());
        } else {
            LOGGER.error("Can't find message data processor");
        }
    }

    public void sendMessageToAllUsers(MessageDto message) {
        LOGGER.info("Sending message to all users: " + message);
        clientManager.sendMessageToAllClients(message);
    }

    public void setProcessors(Map<String, DataProcessor> dataProcessors) {
        this.dataProcessors = dataProcessors;
    }

    public UserDto getOwner() {
        return owner;
    }

    public void setOwner(UserDto owner) {
        this.owner = owner;
    }

    public void initialConnection(UserDto user) {
        LOGGER.info("Processing initialConnection for user: " + user);
        setOwner(user);
        if (clientManager.addClientThread(this)) {
            LOGGER.info("Connection obtained with user: " + user);
            LOGGER.info("Sending message history and list of online users to " + user);
            sendMessageHistoryList();
            sendOnlineUserList(clientManager.getOnlineUserListFor(owner));
            clientManager.sendOnlineUserListToAll();
        }
    }
    
    public void sendOnlineUserList(UsersListDto onlineUsers) {
        sendData(ONLINE_USERS_TAG, onlineUsers);
    }

    public void onUserLimitError() {
        sendData(ERROR_TAG, ErrorDto.USER_LIMIT);
    }

    public void onInvalidNameError() {
        sendData(ERROR_TAG, ErrorDto.INVALID_NAME);
    }

    public void closeClientConnection() {
        closeConnection();
    }

    public void closeServerConnection() {
        sendData(ERROR_TAG, ErrorDto.OFFLINE);
        closeConnection();
    }

    /*
     * DataInputStream + BufferedReader = workaround for JAXB + socket specialty(JAXB don't close stream on the end tag)
     * Workaround(not suitable): https://weblogs.java.net/blog/kohsuke/archive/2005/07/socket_xml_pitf .html
     * Other side have to close stream for successful unmarshalling. But closing stream on every data transfer is not
     * suitable for our application. Workaround with Data(Input/Output)Streams and BufferedReader 
     * Using BufferedReader for line splitting(first line always data/processor type tag)
     */
    private void processData() throws IOException {
        LOGGER.info("Start processing data on socket " + clientSocket);
        while (true) {
            String data = inbound.readUTF();
            BufferedReader reader = new BufferedReader(new StringReader(data));
            processData(reader.readLine(), reader);
        }
    }

    private void sendMessageHistoryList() {
        MessagesListDto messageHistory = clientManager.getMessageHistory();
        sendData(MESSAGE_HISTORY_TAG, messageHistory);
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

    private void closeConnection() {
        LOGGER.info("Closing connection with " + owner);
        Closer.close(outbound);
        Closer.close(inbound);
        Closer.close(clientSocket);
    }

}
