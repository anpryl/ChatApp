package com.aprylutskyi.chat.server.processors.impl;

import com.aprylutskyi.chat.server.dto.UsersListDto;
import com.aprylutskyi.chat.server.processors.DataProcessor;
import com.aprylutskyi.chat.server.processors.format.JAXBProcessor;
import org.apache.log4j.Logger;

import java.io.Reader;

import static com.aprylutskyi.chat.server.constants.DataConstants.ONLINE_USERS_TAG;

public class UsersListProcessor extends JAXBProcessor<UsersListDto> implements DataProcessor {

    private final Logger LOGGER = Logger.getLogger(UsersListProcessor.class);

    @Override
    public void processData(Reader reader) {
        LOGGER.error("Unsupported operation");
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
