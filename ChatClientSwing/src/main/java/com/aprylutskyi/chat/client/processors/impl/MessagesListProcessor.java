package com.aprylutskyi.chat.client.processors.impl;

import com.aprylutskyi.chat.client.connection.ConnectionManagerThread;
import com.aprylutskyi.chat.client.dto.MessagesListDto;
import com.aprylutskyi.chat.client.dto.Processable;
import com.aprylutskyi.chat.client.exceptions.InvalidDataFormatException;
import com.aprylutskyi.chat.client.processors.DataProcessor;
import com.aprylutskyi.chat.client.processors.format.JAXBProcessor;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.DataOutputStream;
import java.io.Reader;

import static com.aprylutskyi.chat.client.constants.DataConstants.MESSAGE_HISTORY_TAG;

public class MessagesListProcessor extends JAXBProcessor<MessagesListDto> implements DataProcessor {

    private final Logger LOGGER = Logger.getLogger(MessagesListProcessor.class);

    private ConnectionManagerThread connectionManager;

    public MessagesListProcessor(ConnectionManagerThread connectionManager) {
        this.connectionManager = connectionManager;
    }
    
    @Override
    public void processData(Reader reader) {
        try {
            LOGGER.info("Processing messages list data");
            MessagesListDto messages = unmarshallMessage(reader);
            LOGGER.info("Processing " + messages);
            connectionManager.transferMessageHistory(messages);
        } catch (JAXBException e) {
            LOGGER.error("Error occurred when unmarshaling message");
            LOGGER.debug(e.getMessage());
        } catch (InvalidDataFormatException e) {
            LOGGER.error("Invalid data format for this processor");
        }
    }

    @Override
    public void sendData(Processable data, DataOutputStream outbound) {
        LOGGER.error("Unsupported operation");
    }

    @Override
    public boolean isCorrectType(Object message) {
        return (message instanceof MessagesListDto);
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public String getDataTag() {
        return MESSAGE_HISTORY_TAG;
    }
}
