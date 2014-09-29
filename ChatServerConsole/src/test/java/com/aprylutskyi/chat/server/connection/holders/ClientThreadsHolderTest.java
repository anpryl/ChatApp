package com.aprylutskyi.chat.server.connection.holders;

import com.aprylutskyi.chat.server.connection.ClientThread;
import com.aprylutskyi.chat.server.constants.UserStatus;
import com.aprylutskyi.chat.server.dto.MessageDto;
import com.aprylutskyi.chat.server.dto.UserDto;
import com.aprylutskyi.chat.server.dto.UsersListDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ClientThread.class)
public class ClientThreadsHolderTest {

    public static final ClientThread mockedClientThread1 = PowerMockito.mock(ClientThread.class);
    public static final ClientThread mockedClientThread2 = PowerMockito.mock(ClientThread.class);
    public static final ClientThread mockedClientThread3 = PowerMockito.mock(ClientThread.class);
    private final int TEST_SIZE = 2;
    private ClientThreadsHolder holder = new ClientThreadsHolder(TEST_SIZE);
    private final UserDto testUser1 = new UserDto("Test1", UserStatus.ONLINE);

    private final UserDto testUser2 = new UserDto("Test2", UserStatus.ONLINE);

    private final UserDto testUser3 = new UserDto("Test3", UserStatus.ONLINE);

    @Before
    public void before() {
        Mockito.reset(mockedClientThread1, mockedClientThread2, mockedClientThread3);
    }

    @Test
    public void addSizeTest() {
        Mockito.when(mockedClientThread1.getOwner()).thenReturn(testUser1);
        Mockito.when(mockedClientThread2.getOwner()).thenReturn(testUser2);
        Mockito.when(mockedClientThread3.getOwner()).thenReturn(testUser3);

        holder.add(mockedClientThread1);
        holder.add(mockedClientThread2);
        holder.add(mockedClientThread3);
        assertEquals(TEST_SIZE, holder.size());
        Mockito.verify(mockedClientThread3).onUserLimitError();
    }

    @Test
    public void addErrorNullOwnerTest() {
        Mockito.when(mockedClientThread1.getOwner()).thenReturn(null);
        assertFalse(holder.add(mockedClientThread1));
    }

    @Test
    public void addErrorInvalidNameOwnerTest() {
        Mockito.when(mockedClientThread1.getOwner()).thenReturn(new UserDto(" ", UserStatus.ONLINE));
        holder.add(mockedClientThread1);
        Mockito.verify(mockedClientThread1).onInvalidNameError();
    }

    @Test
    public void addErrorOfflineUserTest() {
        Mockito.when(mockedClientThread1.getOwner()).thenReturn(new UserDto("TestName", UserStatus.OFFLINE));
        assertFalse(holder.add(mockedClientThread1));
    }

    @Test
    public void addErrorEqualsNameTest() {
        Mockito.when(mockedClientThread1.getOwner()).thenReturn(new UserDto("TestName", UserStatus.ONLINE));
        Mockito.when(mockedClientThread2.getOwner()).thenReturn(new UserDto("TestName", UserStatus.ONLINE));
        holder.add(mockedClientThread1);
        holder.add(mockedClientThread2);

        assertEquals(1, holder.size());
        Mockito.verify(mockedClientThread2).onInvalidNameError();
    }

    @Test
    public void removeAllTest() throws Exception {
        Mockito.when(mockedClientThread1.getOwner()).thenReturn(testUser1);
        Mockito.when(mockedClientThread2.getOwner()).thenReturn(testUser2);

        holder.add(mockedClientThread1);
        holder.add(mockedClientThread2);
        holder.removeAll();
        assertEquals(0, holder.size());

        Mockito.verify(mockedClientThread1).closeServerConnection();
        Mockito.verify(mockedClientThread2).closeServerConnection();
    }

    @Test
    public void sendMessageToAllTest() throws Exception {
        Mockito.when(mockedClientThread1.getOwner()).thenReturn(testUser1);
        Mockito.when(mockedClientThread2.getOwner()).thenReturn(testUser2);

        holder.add(mockedClientThread1);
        holder.add(mockedClientThread2);
        MessageDto message = new MessageDto();
        holder.sendMessageToAll(message);

        Mockito.verify(mockedClientThread1).sendMessage(message);
        Mockito.verify(mockedClientThread2).sendMessage(message);
    }

    @Test
    public void getOnlineUsersTest() throws Exception {
        Mockito.when(mockedClientThread1.getOwner()).thenReturn(testUser1);
        Mockito.when(mockedClientThread2.getOwner()).thenReturn(testUser2);

        holder.add(mockedClientThread1);
        holder.add(mockedClientThread2);
        UsersListDto onlineUsers = holder.getOnlineUsers();

        assertEquals(new UsersListDto(testUser1, testUser2), onlineUsers);
    }

    @Test
    public void sendOnlineListToAll() throws Exception {
        Mockito.when(mockedClientThread1.getOwner()).thenReturn(testUser1);
        Mockito.when(mockedClientThread2.getOwner()).thenReturn(testUser2);

        holder.add(mockedClientThread1);
        holder.add(mockedClientThread2);
        holder.sendOnlineListToAll();
        UsersListDto onlineUsers = holder.getOnlineUsers();

        Mockito.verify(mockedClientThread1).sendOnlineUserList(onlineUsers);
        Mockito.verify(mockedClientThread2).sendOnlineUserList(onlineUsers);
    }

    @Test
    public void removeUserTest() throws Exception {
        Mockito.when(mockedClientThread1.getOwner()).thenReturn(testUser1);
        Mockito.when(mockedClientThread2.getOwner()).thenReturn(testUser2);
        holder.add(mockedClientThread1);
        holder.add(mockedClientThread2);
        boolean isRemoved = holder.removeClient(testUser1.getName());
        assertTrue(isRemoved);
        assertEquals(1, holder.size());
        Mockito.verify(mockedClientThread1).closeServerConnection();
    }

    @Test
    public void removeUserNoUserName() throws Exception {
        Mockito.when(mockedClientThread1.getOwner()).thenReturn(testUser1);
        Mockito.when(mockedClientThread2.getOwner()).thenReturn(testUser2);
        holder.add(mockedClientThread1);
        holder.add(mockedClientThread2);
        boolean isRemoved = holder.removeClient("NO USER WITH THIS NAME");
        assertFalse(isRemoved);
        assertEquals(2, holder.size());
        Mockito.verify(mockedClientThread1, Mockito.times(0)).closeServerConnection();
        Mockito.verify(mockedClientThread2, Mockito.times(0)).closeServerConnection();
    }
}
