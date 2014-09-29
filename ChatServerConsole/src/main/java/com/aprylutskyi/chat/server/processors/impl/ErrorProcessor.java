package com.aprylutskyi.chat.server.processors.impl;

import com.aprylutskyi.chat.server.connection.ClientThread;
import com.aprylutskyi.chat.server.dto.ErrorDto;
import com.aprylutskyi.chat.server.exceptions.InvalidDataFormatException;
import com.aprylutskyi.chat.server.processors.DataProcessor;
import com.aprylutskyi.chat.server.processors.format.JAXBProcessor;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.Reader;

import static com.aprylutskyi.chat.server.constants.DataConstants.ERROR_TAG;

public class ErrorProcessor extends JAXBProcessor<ErrorDto> implements DataProcessor {

    private final Logger LOGGER = Logger.getLogger(ErrorProcessor.class);

    private ClientThread clientThread;

    public ErrorProcessor(ClientThread clientThread) {
        this.clientThread = clientThread;
    }

    @Override
    public void processData(Reader reader) {
        try {
            LOGGER.info("Processing error data");
            ErrorDto error = unmarshallMessage(reader);
            String errorType = error.getErrorType();
            if (ErrorDto.OFFLINE.equals(error)) {
                LOGGER.info("Processing " + errorType);
                clientThread.closeClientConnection();
            } else {
                LOGGER.error("Unsupported error type: " + errorType);
            }
        } catch (JAXBException e) {
            LOGGER.error("Error occurred when unmarshaling error");
            LOGGER.debug(e.getMessage());
        } catch (InvalidDataFormatException e) {
            LOGGER.error("Invalid data format for this processor");
        }
    }

    @Override
    public boolean isCorrectType(Object message) {
        return (message instanceof ErrorDto);
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public String getDataTag() {
        return ERROR_TAG;
    }

}
