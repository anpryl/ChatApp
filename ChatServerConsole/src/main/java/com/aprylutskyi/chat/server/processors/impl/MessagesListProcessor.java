package com.aprylutskyi.chat.server.processors.impl;

import com.aprylutskyi.chat.server.dto.MessagesListDto;
import com.aprylutskyi.chat.server.processors.DataProcessor;
import com.aprylutskyi.chat.server.processors.format.JAXBProcessor;
import org.apache.log4j.Logger;

import java.io.Reader;

import static com.aprylutskyi.chat.server.constants.DataConstants.MESSAGE_HISTORY_TAG;

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
    public String getDataTag() {
        return MESSAGE_HISTORY_TAG;
    }
}
