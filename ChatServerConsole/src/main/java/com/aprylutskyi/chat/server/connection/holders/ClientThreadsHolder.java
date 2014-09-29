package com.aprylutskyi.chat.server.connection.holders;

import com.aprylutskyi.chat.server.connection.ClientThread;
import com.aprylutskyi.chat.server.constants.UserStatus;
import com.aprylutskyi.chat.server.dto.MessageDto;
import com.aprylutskyi.chat.server.dto.UserDto;
import com.aprylutskyi.chat.server.dto.UsersListDto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ClientThreadsHolder {

    private int maxSize;

    private List<ClientThread> clientThreads = new LinkedList<>();

    public ClientThreadsHolder(int maxClients) {
        maxSize = maxClients;
    }

    public boolean add(ClientThread clientThread) {
        synchronized (clientThreads) {
            if (isValidClient(clientThread)) {
                return clientThreads.add(clientThread);
            } else {
                return false;
            }
        }
    }

    public boolean removeClient(String userToKill) {
        synchronized (clientThreads) {
            for (ClientThread clientThread : clientThreads) {
                UserDto user = clientThread.getOwner();
                if (user.getName().equals(userToKill)) {
                    clientThread.closeServerConnection();
                    remove(clientThread);
                    return true;
                }
            }
            return false;
        }
    }

    public void remove(ClientThread clientThread) {
        synchronized (clientThreads) {
            clientThreads.remove(clientThread);
        }
    }

    public void removeAll() {
        synchronized (clientThreads) {
            for (Iterator<ClientThread> iterator = clientThreads.iterator(); iterator.hasNext(); ) {
                ClientThread clientThread = iterator.next();
                iterator.remove();
                clientThread.closeServerConnection();
            }
        }
    }

    public void sendMessageToAll(MessageDto message) {
        synchronized (clientThreads) {
            for (ClientThread clientThread : clientThreads) {
                clientThread.sendMessage(message);
            }
        }
    }

    public void sendOnlineListToAll() {
        synchronized (clientThreads) {
            for (ClientThread clientThread : clientThreads) {
                clientThread.sendOnlineUserList(getOnlineUsers());
            }
        }
    }

    public UsersListDto getOnlineUsers() {
        synchronized (clientThreads) {
            List<UserDto> onlineUsers = new ArrayList<>();
            for (ClientThread thread : clientThreads) {
                onlineUsers.add(thread.getOwner());
            }
            return new UsersListDto(onlineUsers);
        }
    }

    public int size() {
        synchronized (clientThreads) {
            return clientThreads.size();
        }
    }

    private boolean isValidClient(ClientThread clientThread) {
        UserDto newClient = clientThread.getOwner();
        if (clientThreads.size() == maxSize) {
            clientThread.onUserLimitError();
            return false;
        }
        if (newClient != null && UserStatus.ONLINE.equalsIgnoreCase(newClient.getStatus())) {
            return checkName(clientThread, newClient);
        }
        return false;
    }

    private boolean checkName(ClientThread clientThread, UserDto newClient) {
        String newClientName = newClient.getName();
        if (newClientName.trim().isEmpty()) {
            clientThread.onInvalidNameError();
            return false;
        }
        for (ClientThread client : clientThreads) {
            String clientName = client.getOwner().getName();
            if (clientName.equalsIgnoreCase(newClientName)) {
                clientThread.onInvalidNameError();
                return false;
            }
        }
        return true;
    }

}
