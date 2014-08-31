package com.aprylutskyi.chat.server;

public class EntryPoint {

    public static void main(String[] args) {
        ServerStarter serverStarter = new ServerStarter();
        if (args.length > 0) {
            serverStarter.setConfigFilePath(args[0]);
        }
        serverStarter.init();
    }
}
