package com.aprylutskyi.chat.server;

import com.aprylutskyi.chat.server.configuration.Configurer;
import com.aprylutskyi.chat.server.configuration.LoggerConfigurer;
import com.aprylutskyi.chat.server.connection.ClientManager;
import com.aprylutskyi.chat.server.control.CommandLineControl;
import com.aprylutskyi.chat.server.dto.ConfigurationDto;
import com.aprylutskyi.chat.server.util.JAXBHelper;

public class ServerStarter {

    private String configFilePath;

    public void init() {
        //To initialize jaxb context
        JAXBHelper.getJaxbContext();
        LoggerConfigurer.configureConsoleAppender();
        ConfigurationDto config = getConfig();
        ClientManager clientManager = new ClientManager(config);
        new Thread(clientManager).start();
        Thread controlThread = new Thread(new CommandLineControl(clientManager));
        controlThread.setDaemon(true);
        controlThread.start();
    }

    private ConfigurationDto getConfig() {
        return new Configurer().getConfig(configFilePath);
    }

    public void setConfigFilePath(String configFilePath) {
        this.configFilePath = configFilePath;
    }

}
