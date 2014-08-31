package com.aprylutskyi.chat.client.processors.impl;

import com.aprylutskyi.chat.client.connection.ConnectionManagerThread;
import com.aprylutskyi.chat.client.dto.MessageDto;
import com.aprylutskyi.chat.client.exceptions.InvalidDataFormatException;
import com.aprylutskyi.chat.client.processors.DataProcessor;
import com.aprylutskyi.chat.client.processors.format.JAXBProcessor;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.Reader;

import static com.aprylutskyi.chat.client.constants.DataConstants.MESSAGE_TAG;

public class MessageProcessor extends JAXBProcessor<MessageDto> implements DataProcessor {

    private final Logger LOGGER = Logger.getLogger(MessageProcessor.class);

    private ConnectionManagerThread connectionManager;

    public MessageProcessor(ConnectionManagerThread connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void processData(Reader reader) {
        try {
            LOGGER.info("Processing message data");
            MessageDto message = unmarshallMessage(reader);
            LOGGER.info("Processing " + message);
            connectionManager.transferMessageToView(message);
        } catch (JAXBException e) {
            LOGGER.error("Error occurred when unmarshaling message");
            LOGGER.debug(e.getMessage());
        } catch (InvalidDataFormatException e) {
            LOGGER.error("Invalid data format for this processor");
        }
    }

    @Override
    public boolean isCorrectType(Object message) {
        return (message instanceof MessageDto);
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public String getDataTag() {
        return MESSAGE_TAG;
    }

}
