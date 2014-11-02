package com.aprylutskyi.chat.server.processors.impl;

import com.aprylutskyi.chat.server.connection.ClientThread;
import com.aprylutskyi.chat.server.constants.UserStatus;
import com.aprylutskyi.chat.server.dto.UserDto;
import com.aprylutskyi.chat.server.exceptions.InvalidDataFormatException;
import com.aprylutskyi.chat.server.processors.DataProcessor;
import com.aprylutskyi.chat.server.processors.format.JAXBProcessor;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.Reader;

import static com.aprylutskyi.chat.server.constants.DataConstants.USER_TAG;

public class UserProcessor extends JAXBProcessor<UserDto> implements DataProcessor {

    private final Logger LOGGER = Logger.getLogger(UserProcessor.class);

    private ClientThread clientThread;

    public UserProcessor(ClientThread clientThread) {
        this.clientThread = clientThread;
    }

    @Override
    public void processData(Reader reader) {
        try {
            LOGGER.info("Processing user data");
            UserDto user = unmarshallMessage(reader);
            String status = user.getStatus();
            if (UserStatus.OFFLINE.equals(status)) {
                LOGGER.info("Processing offline user: " + user);
                clientThread.closeClientConnection();
            } else if (UserStatus.ONLINE.equals(status)) {
                LOGGER.info("Processing online user: " + user);
                clientThread.initialConnection(user);
            }
        } catch (JAXBException e) {
            LOGGER.error("Error occurred when unmarshaling message");
            LOGGER.debug(e.getMessage());
        } catch (InvalidDataFormatException e) {
            LOGGER.error("Invalid data format for this processor");
        }
    }

    @Override
    public boolean isCorrectType(Object message) {
        return (message instanceof UserDto);
    }

    @Override
    public String getDataTag() {
        return USER_TAG;
    }
}
