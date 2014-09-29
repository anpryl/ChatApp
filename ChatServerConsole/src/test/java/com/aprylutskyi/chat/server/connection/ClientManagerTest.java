package com.aprylutskyi.chat.server.connection;

import com.aprylutskyi.chat.server.configuration.Configurer;
import com.aprylutskyi.chat.server.connection.holders.ClientThreadsHolder;
import com.aprylutskyi.chat.server.connection.holders.MessageHistoryHolder;
import com.aprylutskyi.chat.server.dto.ConfigurationDto;
import com.aprylutskyi.chat.server.dto.MessageDto;
import com.aprylutskyi.chat.server.dto.MessagesListDto;
import com.aprylutskyi.chat.server.dto.UserDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ClientThreadsHolder.class, MessageHistoryHolder.class, ClientThread.class})
public class ClientManagerTest {

    protected final ClientThreadsHolder mockedThreadsHolder = PowerMockito.mock(ClientThreadsHolder.class);

    protected final MessageHistoryHolder mockedHistoryHolder = PowerMockito.mock(MessageHistoryHolder.class);

    protected final ClientThread mockedClientThread = PowerMockito.mock(ClientThread.class);

    private final ConfigurationDto configurationDto = new Configurer().getConfig(null);

    private ClientManager clientManager = new ClientManager(configurationDto);

    private final MessageDto testMessage = new MessageDto("Test", "Test", "Test");

    private final UserDto owner = new UserDto("OWNER", "ONLINE");

    public ClientManagerTest() {
        clientManager.setClientThreads(mockedThreadsHolder);
        clientManager.setHistoryHolder(mockedHistoryHolder);
    }

    @Before
    public void before() {
        Mockito.reset(mockedThreadsHolder, mockedHistoryHolder, mockedClientThread);
    }

    @Test
    public void sendMessageToAllClientsTest() {
        clientManager.sendMessageToAllClients(testMessage);
        Mockito.verify(mockedThreadsHolder).sendMessageToAll(testMessage);
    }

    @Test
    public void getMessageHistoryTest() {
        MessagesListDto testMessages = new MessagesListDto(testMessage);
        Mockito.when(mockedHistoryHolder.getMessageHistory()).thenReturn(testMessages);
        MessagesListDto messageHistory = clientManager.getMessageHistory();
        Mockito.verify(mockedHistoryHolder).getMessageHistory();
        assertEquals(testMessages, messageHistory);
    }

    @Test
    public void getOnlineUserListForTest() throws Exception {
        clientManager.getOnlineUserListFor(owner);
        Mockito.verify(mockedThreadsHolder).sendMessageToAll(Mockito.any(MessageDto.class));
        Mockito.verify(mockedThreadsHolder).getOnlineUsers();
    }

    @Test
    public void removeClientThreadWithOwnerTest() throws Exception {
        Mockito.when(mockedClientThread.getOwner()).thenReturn(owner);
        clientManager.removeClientThread(mockedClientThread);
        Mockito.verify(mockedClientThread).getOwner();
        Mockito.verify(mockedThreadsHolder).remove(mockedClientThread);
        Mockito.verify(mockedThreadsHolder).sendMessageToAll(Mockito.any(MessageDto.class));
        Mockito.verify(mockedThreadsHolder).sendOnlineListToAll();
    }
}
