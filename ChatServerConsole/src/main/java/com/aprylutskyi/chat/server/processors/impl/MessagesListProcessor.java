package com.aprylutskyi.chat.server.processors.impl;

import static com.aprylutskyi.chat.server.constants.DataConstants.MESSAGE_HISTORY_TAG;

import java.io.Reader;

import org.apache.log4j.Logger;

import com.aprylutskyi.chat.server.dto.MessagesListDto;
import com.aprylutskyi.chat.server.processors.DataProcessor;
import com.aprylutskyi.chat.server.processors.format.JAXBProcessor;

public class MessagesListProcessor extends JAXBProcessor<MessagesListDto> implements DataProcessor {

    private final Logger LOGGER = Logger.getLogger(MessagesListProcessor.class);

    @Override
    public void processData(Reader reader) {
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
