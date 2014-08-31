package com.aprylutskyi.chat.server.connection;

import static com.aprylutskyi.chat.server.constants.DataConstants.ERROR_TAG;
import static com.aprylutskyi.chat.server.constants.DataConstants.MESSAGE_HISTORY_TAG;
import static com.aprylutskyi.chat.server.constants.DataConstants.MESSAGE_TAG;
import static com.aprylutskyi.chat.server.constants.DataConstants.ONLINE_USERS_TAG;
import static com.aprylutskyi.chat.server.constants.DataConstants.USER_TAG;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.aprylutskyi.chat.server.connection.ClientManager;
import com.aprylutskyi.chat.server.connection.ClientThread;
import com.aprylutskyi.chat.server.dto.ErrorDto;
import com.aprylutskyi.chat.server.dto.MessageDto;
import com.aprylutskyi.chat.server.dto.MessagesListDto;
import com.aprylutskyi.chat.server.dto.UserDto;
import com.aprylutskyi.chat.server.dto.UsersListDto;
import com.aprylutskyi.chat.server.processors.DataProcessor;
import com.aprylutskyi.chat.server.util.TimeHelper;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ClientManager.class, Socket.class, DataProcessor.class })
public class ClientThreadTest {

    private final ClientManager mockedClientManager = PowerMockito.mock(ClientManager.class);

    private final Socket mockedClientSocket = PowerMockito.mock(Socket.class);

    private final ClientThread clientThread = new ClientThread(mockedClientManager, mockedClientSocket);

    private final DataProcessor mockedMessageProcessor = PowerMockito.mock(DataProcessor.class);

    private final DataProcessor mockedMessagesListProcessor = PowerMockito.mock(DataProcessor.class);

    private final DataProcessor mockedUsersListProcessor = PowerMockito.mock(DataProcessor.class);

    private final DataProcessor mockedUserProcessor = PowerMockito.mock(DataProcessor.class);

    private final DataProcessor mockedErrorProcessor = PowerMockito.mock(DataProcessor.class);

    private final Map<String, DataProcessor> mockedDataProcessors = getMockedDataProcessors();

    private final UserDto testOwner = new UserDto("Owner", "ONLINE");

    private final UserDto testUser1 = new UserDto("Test1", "ONLINE");

    private final UserDto testUser2 = new UserDto("Test2", "ONLINE");

    private final UserDto testUser3 = new UserDto("Test3", "ONLINE");

    private final UsersListDto testUsers = new UsersListDto();

    private final MessageDto testMessage1 = new MessageDto("Author1", "test message1", TimeHelper.getNow());

    private final MessageDto testMessage2 = new MessageDto("Author2", "test message2", TimeHelper.getNow());

    private final MessageDto testMessage3 = new MessageDto("Author3", "test message3", TimeHelper.getNow());

    private final MessagesListDto testMessages = new MessagesListDto();

    public ClientThreadTest() throws Exception {
        List<UserDto> users = new ArrayList<>();
        users.add(testUser1);
        users.add(testUser2);
        users.add(testUser3);
        testUsers.setUsers(users);

        List<MessageDto> messages = new ArrayList<>();
        messages.add(testMessage1);
        messages.add(testMessage2);
        messages.add(testMessage3);
        testMessages.setMessages(messages);

        clientThread.setOwner(testUser1);
        clientThread.setProcessors(mockedDataProcessors);
    }

    @Before
    public void before() {
        Mockito.reset(mockedClientManager, mockedClientSocket, mockedMessageProcessor, mockedMessagesListProcessor,
                mockedUsersListProcessor, mockedUserProcessor, mockedErrorProcessor);
    }

    @Test
    public void initialConnectionClientManagerTest() throws Exception {
        Mockito.when(mockedClientManager.addClientThread(clientThread)).thenReturn(true);
        
        clientThread.initialConnection(testOwner);
        Mockito.verify(mockedClientManager).getMessageHistory();
        Mockito.verify(mockedClientManager).getOnlineUserListFor(testOwner);
        Mockito.verify(mockedClientManager).addClientThread(clientThread);
    }

    @Test
    public void initialConnectionProcessorTest() throws Exception {
        Mockito.when(mockedClientManager.addClientThread(clientThread)).thenReturn(true);
        Mockito.when(mockedClientManager.getMessageHistory()).thenReturn(testMessages);
        Mockito.when(mockedClientManager.getOnlineUserListFor(testOwner)).thenReturn(testUsers);
        
        clientThread.initialConnection(testOwner);
        Mockito.verify(mockedMessagesListProcessor, Mockito.times(1)).sendData(testMessages, null);
        Mockito.verify(mockedUsersListProcessor, Mockito.times(1)).sendData(testUsers, null);
    }
    
    @Test
    public void sendMessageTest() {
        clientThread.sendMessage(testMessage1);
        Mockito.verify(mockedMessageProcessor).sendData(testMessage1, null);
    }

    @Test
    public void tendMessageToAllUsersTest() {
        clientThread.sendMessageToAllUsers(testMessage1);
        Mockito.verify(mockedClientManager).sendMessageToAllClients(testMessage1);
    }

    @Test
    public void onUserLimitErrorTest() {
        clientThread.onUserLimitError();
        Mockito.verify(mockedErrorProcessor).sendData(ErrorDto.USER_LIMIT, null);
    }

    @Test
    public void testOnInvalidNameError() {
        clientThread.onInvalidNameError();
        Mockito.verify(mockedErrorProcessor).sendData(ErrorDto.INVALID_NAME, null);
    }

    @Test
    public void processDataTest() throws Exception {
        ByteArrayOutputStream outputStreamForMockedSocket = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStreamForMockedSocket);
        String startMessage = MESSAGE_TAG + System.lineSeparator();
        dataOutputStream.writeUTF(startMessage);
        //Get data in UTF format
        startMessage = outputStreamForMockedSocket.toString();
        InputStream inputStreamForMockedSocket = new ByteArrayInputStream(startMessage.getBytes());
        DataInputStream dataInputStream = new DataInputStream(inputStreamForMockedSocket);

        Mockito.when(mockedClientSocket.getInputStream()).thenReturn(dataInputStream);
        Mockito.when(mockedClientSocket.getOutputStream()).thenReturn(dataOutputStream);
        new Thread(clientThread).start();
        Thread.sleep(100);
        Mockito.verify(mockedMessageProcessor, Mockito.times(1)).processData(Mockito.any(Reader.class));
        Mockito.verifyZeroInteractions(mockedUsersListProcessor, mockedUserProcessor, mockedMessagesListProcessor,
                mockedErrorProcessor);
        clientThread.closeClientConnection();
    }

    private Map<String, DataProcessor> getMockedDataProcessors() {
        Map<String, DataProcessor> mockedDataProcessors = new HashMap<>();
        mockedDataProcessors.put(ONLINE_USERS_TAG, mockedUsersListProcessor);
        mockedDataProcessors.put(USER_TAG, mockedUserProcessor);
        mockedDataProcessors.put(MESSAGE_HISTORY_TAG, mockedMessagesListProcessor);
        mockedDataProcessors.put(MESSAGE_TAG, mockedMessageProcessor);
        mockedDataProcessors.put(ERROR_TAG, mockedErrorProcessor);
        return mockedDataProcessors;
    }

}
