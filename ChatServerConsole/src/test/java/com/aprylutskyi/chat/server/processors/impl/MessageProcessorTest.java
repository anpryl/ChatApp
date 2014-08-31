package com.aprylutskyi.chat.server.processors.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;

import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.mockito.Mockito;
import org.xml.sax.InputSource;

import com.aprylutskyi.chat.server.constants.DataConstants;
import com.aprylutskyi.chat.server.dto.MessageDto;
import com.aprylutskyi.chat.server.processors.impl.MessageProcessor;
import com.aprylutskyi.chat.server.util.JAXBHelper;

public class MessageProcessorTest extends AbstractProcessorTest {

    private final MessageProcessor messageProcessor = new MessageProcessor(mockedClientThread);

    private final String testMessagePath = getPathFromClassPath("/testMessage.xml");

    private final String testUserPath = getPathFromClassPath("/testUserOffline.xml");

    @Test
    public void processDataTest() throws Exception {
        FileReader reader = new FileReader(new File(testMessagePath));
        messageProcessor.processData(reader);
        MessageDto message = (MessageDto) getTestDataFromFile(testMessagePath);
        Mockito.verify(mockedClientThread).sendMessageToAllUsers(message);
    }

    @Test
    public void sendDataTest() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        messageProcessor.sendData(getTestDataFromFile(testMessagePath), dataOutputStream);
        String rawData = byteArrayOutputStream.toString();
        ByteArrayInputStream inputStreamRaw = new ByteArrayInputStream(rawData.getBytes());
        DataInputStream dataInputStream = new DataInputStream(inputStreamRaw);
        String testData = dataInputStream.readUTF();
        assertTrue(testData.startsWith(DataConstants.MESSAGE_TAG));

        testData = testData.replaceFirst(DataConstants.MESSAGE_TAG + System.lineSeparator(), "");
        Unmarshaller unmarshaller = JAXBHelper.getJaxbContext().createUnmarshaller();
        MessageDto messageFromProcessor = (MessageDto) unmarshaller.unmarshal(new InputSource(
                new StringReader(testData)));
        MessageDto messageFromFile = (MessageDto) unmarshaller.unmarshal(new File(testMessagePath));
        assertEquals(messageFromProcessor, messageFromFile);
    }

    @Test
    public void processDataInvalidDataFormatTest() throws Exception {
        FileReader reader = new FileReader(new File(testUserPath));
        messageProcessor.processData(reader);
        Mockito.verifyZeroInteractions(mockedClientThread);
    }

    @Test
    public void sendDataInvalidDataFormatTest() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        messageProcessor.sendData(getTestDataFromFile(testUserPath), dataOutputStream);
        String testData = byteArrayOutputStream.toString();
        assertTrue(testData.isEmpty());
    }

}
