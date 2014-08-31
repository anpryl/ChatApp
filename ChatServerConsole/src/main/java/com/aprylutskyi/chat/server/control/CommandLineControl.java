package com.aprylutskyi.chat.server.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.log4j.Logger;

import com.aprylutskyi.chat.server.connection.ClientManager;
import com.aprylutskyi.chat.server.dto.UserDto;
import com.aprylutskyi.chat.server.dto.UsersListDto;

public class CommandLineControl implements Runnable {

    private final Logger LOGGER = Logger.getLogger(CommandLineControl.class);

    private final String EXIT = "exit";

    private final String HELP = "help";

    private final String LIST = "list";

    private final String KILL = "kill";

    private ClientManager clientManager;

    private InputStream inputStream;

    public CommandLineControl(ClientManager clientManager) {
        this.clientManager = clientManager;
        setInputStream(System.in);
    }

    @Override
    public void run() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getInputStream()));
        System.out.println("Enter '" + HELP + "' for list of all commands");
        try {
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    processCommand(readLine);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error while reading commandline, w");
        }
    }

    private void processCommand(String readLine) {
        readLine = readLine.trim();
        if (EXIT.equals(readLine)) {
            System.out.println("Shutdown server");
            clientManager.shutdownServer();
        } else if (HELP.equals(readLine)) {
            showTips();
        } else if (LIST.equals(readLine)) {
            showOnlineUserList();
        } else if (readLine.startsWith(KILL)) {
            removeClient(readLine);
        } else {
            System.out.println("Enter '" + HELP + "' for list of all commands");
        }
    }

    private void removeClient(String userToKill) {
        userToKill = userToKill.replaceFirst(KILL, "").trim();
        if (!userToKill.isEmpty()) {
            if (clientManager.disconnectUserWithName(userToKill)) {
                System.out.println("User with name " + userToKill + " was disconnected");
            } else {
                System.out.println("User with name " + userToKill + " was not found");
            }
        } else {
            System.out.println("With " + KILL + " command you should add user name to disconnect");
        }
    }

    private void showOnlineUserList() {
        UsersListDto onlineUserList = clientManager.getOnlineUserList();
        List<UserDto> onlineUsers = onlineUserList.getUsers();
        System.out.println("=============Online user list==============");
        for (UserDto userDto : onlineUsers) {
            System.out.println(userDto.getName());
        }
        System.out.println("===========================================");
    }

    private void showTips() {
        System.out.println("=========================================================================");
        System.out.println(HELP + " - list of all commands");
        System.out.println(LIST + " - list of online users");
        System.out.println(KILL + " 'username' - disconnecting user");
        System.out.println(EXIT + " - shutdown server");
        System.out.println("=========================================================================");
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
