package com.aprylutskyi.chat.client.processors.format;

import com.aprylutskyi.chat.client.dto.Processable;
import com.aprylutskyi.chat.client.exceptions.InvalidDataFormatException;
import com.aprylutskyi.chat.client.processors.DataProcessor;
import com.aprylutskyi.chat.client.util.JAXBHelper;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

public abstract class JAXBProcessor<T> implements DataProcessor {

    private final Logger LOGGER = Logger.getLogger(JAXBProcessor.class);

    private Unmarshaller unmarshaller;

    private Marshaller marshaller;

    public abstract boolean isCorrectType(Object message);

    public abstract String getDataTag();

    @Override
    public void sendData(Processable data, DataOutputStream outbound) {
        try {
            LOGGER.info("Sending data: " + data);
            StringWriter writerForMarhaller = new StringWriter();
            marshallObject(data, writerForMarhaller);
            String outputData = getDataTag() + System.lineSeparator() + writerForMarhaller.toString();
            outbound.writeUTF(outputData);
            outbound.flush();
        } catch (JAXBException e) {
            LOGGER.error("Error occurred when marshaling message object");
            LOGGER.debug(e.getMessage());
        } catch (IOException e) {
            LOGGER.error("Can't write data into stream");
            LOGGER.debug(e.getMessage());
        } catch (InvalidDataFormatException e) {
            LOGGER.error("Invalid data format for this processor");
        }
    }

    public void marshallObject(Processable data, StringWriter writerForMarhaller) throws InvalidDataFormatException,
            JAXBException {
        if (marshaller == null) {
            marshaller = JAXBHelper.getJaxbContext().createMarshaller();
        }
        if (isCorrectType(data)) {
            marshaller.marshal(data, writerForMarhaller);
        } else {
            throw new InvalidDataFormatException();
        }
    }

    @SuppressWarnings({"unchecked"})
    public T unmarshallMessage(Reader reader) throws InvalidDataFormatException, JAXBException {
        if (unmarshaller == null) {
            unmarshaller = JAXBHelper.getJaxbContext().createUnmarshaller();
        }
        Object unmarshalledObject = unmarshaller.unmarshal(reader);
        if (isCorrectType(unmarshalledObject)) {
            return (T) unmarshalledObject;
        } else {
            throw new InvalidDataFormatException();
        }
    }
}
