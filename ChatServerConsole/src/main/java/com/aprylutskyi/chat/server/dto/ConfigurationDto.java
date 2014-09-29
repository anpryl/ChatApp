package com.aprylutskyi.chat.server.dto;

import java.io.Serializable;

public class ConfigurationDto implements Serializable {

    private static final long serialVersionUID = 9056856499912636668L;

    private int port;

    private int maxUsers;

    private int messageHistorySize;

    private String logFilePath;

    public int getMessageHistorySize() {
        return messageHistorySize;
    }

    public void setMessageHistorySize(int messageHistorySize) {
        this.messageHistorySize = messageHistorySize;
    }

    public String getLogFilePath() {
        return logFilePath;
    }

    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
    }

    @Override
    public String toString() {
        return "Server configuration: [port=" + port + ", maxUsers=" + maxUsers + ", messageHistorySize="
                + messageHistorySize + ", logFilePath=" + logFilePath + "]";
    }

}
