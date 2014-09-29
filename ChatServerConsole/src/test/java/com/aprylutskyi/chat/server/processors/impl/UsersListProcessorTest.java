package com.aprylutskyi.chat.server.processors.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.StringReader;

import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.xml.sax.InputSource;

import com.aprylutskyi.chat.server.constants.DataConstants;
import com.aprylutskyi.chat.server.dto.UsersListDto;
import com.aprylutskyi.chat.server.processors.impl.UsersListProcessor;
import com.aprylutskyi.chat.server.util.JAXBHelper;

public class UsersListProcessorTest extends AbstractProcessorTest {

    private final String testListFilePath = getPathFromClassPath("/testUsersList.xml");

    private final UsersListProcessor listProcessor = new UsersListProcessor();

    @Test
    public void sendDateTest() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        listProcessor.sendData(getTestDataFromFile(testListFilePath), dataOutputStream);
        String rawData = byteArrayOutputStream.toString();
        ByteArrayInputStream inputStreamRaw = new ByteArrayInputStream(rawData.getBytes());
        DataInputStream dataInputStream = new DataInputStream(inputStreamRaw);
        String testData = dataInputStream.readUTF();

        assertTrue(testData.startsWith(DataConstants.ONLINE_USERS_TAG));

        testData = testData.replaceFirst(DataConstants.ONLINE_USERS_TAG + System.lineSeparator(), "");
        Unmarshaller unmarshaller = JAXBHelper.getJaxbContext().createUnmarshaller();
        UsersListDto usersFromProcessor = (UsersListDto) unmarshaller.unmarshal(new InputSource(new StringReader(
                testData)));
        UsersListDto userFromFile = (UsersListDto) unmarshaller.unmarshal(new File(testListFilePath));

        assertEquals(usersFromProcessor, userFromFile);
    }
}
