package com.aprylutskyi.chat.server.processors.impl;

import com.aprylutskyi.chat.server.connection.ClientThread;
import com.aprylutskyi.chat.server.dto.MessageDto;
import com.aprylutskyi.chat.server.exceptions.InvalidDataFormatException;
import com.aprylutskyi.chat.server.processors.DataProcessor;
import com.aprylutskyi.chat.server.processors.format.JAXBProcessor;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.Reader;

import static com.aprylutskyi.chat.server.constants.DataConstants.MESSAGE_TAG;

public class MessageProcessor extends JAXBProcessor<MessageDto> implements DataProcessor {

    private final Logger LOGGER = Logger.getLogger(MessageProcessor.class);

    private ClientThread clientThread;

    public MessageProcessor(ClientThread clientThread) {
        this.clientThread = clientThread;
    }

    @Override
    public void processData(Reader reader) {
        try {
            LOGGER.info("Processing message data");
            MessageDto message = unmarshallMessage(reader);
            LOGGER.info("Processing " + message);
            clientThread.sendMessageToAllUsers(message);
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
    public String getDataTag() {
        return MESSAGE_TAG;
    }

}
