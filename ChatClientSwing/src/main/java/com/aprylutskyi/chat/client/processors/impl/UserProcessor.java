package com.aprylutskyi.chat.client.processors.impl;

import com.aprylutskyi.chat.client.dto.UserDto;
import com.aprylutskyi.chat.client.processors.DataProcessor;
import com.aprylutskyi.chat.client.processors.format.JAXBProcessor;
import org.apache.log4j.Logger;

import java.io.Reader;

import static com.aprylutskyi.chat.client.constants.DataConstants.USER_TAG;

public class UserProcessor extends JAXBProcessor<UserDto> implements DataProcessor {

    private final Logger LOGGER = Logger.getLogger(UserProcessor.class);

    @Override
    public void processData(Reader reader) {
        LOGGER.error("Unsupported opperation");
    }

    @Override
    public boolean isCorrectType(Object message) {
        return (message instanceof UserDto);
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public String getDataTag() {
        return USER_TAG;
    }
}
