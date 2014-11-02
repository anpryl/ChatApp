package com.aprylutskyi.chat.client.processors.impl;

import com.aprylutskyi.chat.client.connection.ConnectionManagerThread;
import com.aprylutskyi.chat.client.dto.ErrorDto;
import com.aprylutskyi.chat.client.exceptions.InvalidDataFormatException;
import com.aprylutskyi.chat.client.processors.DataProcessor;
import com.aprylutskyi.chat.client.processors.format.JAXBProcessor;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.Reader;

import static com.aprylutskyi.chat.client.constants.DataConstants.ERROR_TAG;

public class ErrorProcessor extends JAXBProcessor<ErrorDto> implements DataProcessor {

    private final Logger LOGGER = Logger.getLogger(ErrorProcessor.class);

    private ConnectionManagerThread connectionManager;

    public ErrorProcessor(ConnectionManagerThread connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void processData(Reader reader) {
        try {
            LOGGER.info("Processing error data");
            ErrorDto error = unmarshallMessage(reader);
            String errorType = error.getErrorType();
            LOGGER.info("Processing " + errorType);
            connectionManager.onErrorFromServer(error);
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
    public String getDataTag() {
        return ERROR_TAG;
    }

}
