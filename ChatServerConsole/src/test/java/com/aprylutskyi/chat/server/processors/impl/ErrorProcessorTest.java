package com.aprylutskyi.chat.server.processors.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.mockito.Mockito;
import org.xml.sax.InputSource;

import com.aprylutskyi.chat.server.constants.DataConstants;
import com.aprylutskyi.chat.server.dto.ErrorDto;
import com.aprylutskyi.chat.server.processors.impl.ErrorProcessor;
import com.aprylutskyi.chat.server.util.JAXBHelper;

public class ErrorProcessorTest extends AbstractProcessorTest {

    private final String testErrorInvalidName = getPathFromClassPath("/testErrorInvalidName.xml");

    private final String testErrorUserLimit = getPathFromClassPath("/testErrorUserLimit.xml");

    private final String testErrorOffline = getPathFromClassPath("/testErrorOffline.xml");
    
    private final ErrorProcessor errorProcessor = new ErrorProcessor(mockedClientThread);

    @Test
    public void processDataInvalidNameTest() throws Exception {
        FileReader reader = new FileReader(new File(testErrorInvalidName));
        errorProcessor.processData(reader);
        Mockito.verifyZeroInteractions(mockedClientThread);
    }

    @Test
    public void processDataUserLimitTest() throws Exception {
        FileReader reader = new FileReader(new File(testErrorUserLimit));
        errorProcessor.processData(reader);
        Mockito.verifyZeroInteractions(mockedClientThread);
    }
    
    @Test
    public void processDataOfflineTest() throws Exception {
        FileReader reader = new FileReader(new File(testErrorOffline));
        errorProcessor.processData(reader);
        Mockito.verify(mockedClientThread).closeClientConnection();
    }

    @Test
    public void sendDataTest() throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        errorProcessor.sendData(getTestDataFromFile(testErrorUserLimit), dataOutputStream);
        String rawData = byteArrayOutputStream.toString();
        InputStream inputStreamRaw = new ByteArrayInputStream(rawData.getBytes());
        DataInputStream dataInputStream = new DataInputStream(inputStreamRaw);
        String testData = dataInputStream.readUTF();
        assertTrue(testData.startsWith(DataConstants.ERROR_TAG));

        testData = testData.replaceFirst(DataConstants.ERROR_TAG + System.lineSeparator(), "");
        Unmarshaller unmarshaller = JAXBHelper.getJaxbContext().createUnmarshaller();
        ErrorDto errorFromProcessor = (ErrorDto) unmarshaller.unmarshal(new InputSource(
                new StringReader(testData)));
        ErrorDto errorFromFile = (ErrorDto) unmarshaller.unmarshal(new File(testErrorUserLimit));
        assertEquals(errorFromProcessor, errorFromFile);
    }
}
