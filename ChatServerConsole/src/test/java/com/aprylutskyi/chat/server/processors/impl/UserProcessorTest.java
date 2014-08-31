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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.xml.sax.InputSource;

import com.aprylutskyi.chat.server.constants.DataConstants;
import com.aprylutskyi.chat.server.dto.UserDto;
import com.aprylutskyi.chat.server.processors.impl.UserProcessor;
import com.aprylutskyi.chat.server.util.JAXBHelper;

public class UserProcessorTest extends AbstractProcessorTest{

    private final String testDataPathUserOnline = getPathFromClassPath("/testUserOnline.xml");

    private final String testDataPathUserOffline = getPathFromClassPath("/testUserOffline.xml");

    private final UserProcessor userProcessor = new UserProcessor(mockedClientThread);

    @Override
    @Before
    public void before() {
        Mockito.reset(mockedClientThread);
    }

    @Test
    public void processDataUserOnlineTest() throws Exception {
        FileReader reader = new FileReader(new File(testDataPathUserOnline));
        userProcessor.processData(reader);
        UserDto user = (UserDto) getTestDataFromFile(testDataPathUserOnline);
        Mockito.verify(mockedClientThread).initialConnection(user);
    }

    @Test
    public void processDataUserOfflineTest() throws Exception {
        FileReader reader = new FileReader(new File(testDataPathUserOffline));
        userProcessor.processData(reader);
        Mockito.verify(mockedClientThread).closeClientConnection();
    }

    @Test
    public void sendDateTest() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        userProcessor.sendData(getTestDataFromFile(testDataPathUserOnline), dataOutputStream);
        String rawData = byteArrayOutputStream.toString();
        ByteArrayInputStream inputStreamRaw = new ByteArrayInputStream(rawData.getBytes());
        DataInputStream dataInputStream = new DataInputStream(inputStreamRaw);
        String testData = dataInputStream.readUTF();
        assertTrue(testData.startsWith(DataConstants.USER_TAG));
        
        testData = testData.replaceFirst(DataConstants.USER_TAG + System.lineSeparator(), "");
        Unmarshaller unmarshaller = JAXBHelper.getJaxbContext().createUnmarshaller();
        UserDto userFromProcessor = (UserDto) unmarshaller.unmarshal(new InputSource(new StringReader(testData)));
        UserDto userFromFile = (UserDto) unmarshaller.unmarshal(new File(testDataPathUserOnline));
        assertEquals(userFromProcessor, userFromFile);
    }

}
