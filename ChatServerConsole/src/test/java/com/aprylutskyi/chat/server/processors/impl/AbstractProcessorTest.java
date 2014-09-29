package com.aprylutskyi.chat.server.processors.impl;

import java.io.File;
import java.io.FileReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.aprylutskyi.chat.server.connection.ClientThread;
import com.aprylutskyi.chat.server.dto.Processable;
import com.aprylutskyi.chat.server.util.JAXBHelper;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ClientThread.class)
public abstract class AbstractProcessorTest {

    public static final ClientThread mockedClientThread = PowerMockito.mock(ClientThread.class);

    @Before
    public void before() {
        Mockito.reset(mockedClientThread);
    }

    public Processable getTestDataFromFile(String testMessagePath) throws Exception {
        FileReader reader = new FileReader(new File(testMessagePath));
        JAXBContext jaxbContext = JAXBHelper.getJaxbContext();
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (Processable) unmarshaller.unmarshal(reader);
    }

    public String getPathFromClassPath(String fileName) {
        return getClass().getResource(fileName).getFile();
    }

}
