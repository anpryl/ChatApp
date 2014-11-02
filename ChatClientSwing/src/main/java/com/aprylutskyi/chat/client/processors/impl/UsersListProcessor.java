package com.aprylutskyi.chat.client.processors.impl;

import com.aprylutskyi.chat.client.connection.ConnectionManagerThread;
import com.aprylutskyi.chat.client.dto.UsersListDto;
import com.aprylutskyi.chat.client.exceptions.InvalidDataFormatException;
import com.aprylutskyi.chat.client.processors.DataProcessor;
import com.aprylutskyi.chat.client.processors.format.JAXBProcessor;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.Reader;

import static com.aprylutskyi.chat.client.constants.DataConstants.ONLINE_USERS_TAG;

public class UsersListProcessor extends JAXBProcessor<UsersListDto> implements DataProcessor {

    private final Logger LOGGER = Logger.getLogger(UsersListProcessor.class);

    private ConnectionManagerThread connectionManager;

    public UsersListProcessor(ConnectionManagerThread connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void processData(Reader reader) {
        try {
            LOGGER.info("Processing message data");
            UsersListDto users = unmarshallMessage(reader);
            LOGGER.info("Processing " + users);
            connectionManager.updateOnlineUserList(users);
        } catch (JAXBException e) {
            LOGGER.error("Error occurred when unmarshaling message");
            LOGGER.debug(e.getMessage());
        } catch (InvalidDataFormatException e) {
            LOGGER.error("Invalid data format for this processor");
        }
    }

    @Override
    public boolean isCorrectType(Object message) {
        return (message instanceof UsersListDto);
    }

    @Override
    public String getDataTag() {
        return ONLINE_USERS_TAG;
    }

}
