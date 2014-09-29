package com.aprylutskyi.chat.server.connection.holders;

import com.aprylutskyi.chat.server.dto.MessageDto;
import com.aprylutskyi.chat.server.dto.MessagesListDto;
import com.aprylutskyi.chat.server.util.TimeHelper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MessageHistoryHolderTest {

    private final int TEST_SIZE = 2;
    private final MessageHistoryHolder holder = new MessageHistoryHolder(TEST_SIZE);
    private final MessageDto message1 = new MessageDto("Author1", "Text1", TimeHelper.getNow());
    private final MessageDto message2 = new MessageDto("Author2", "Text2", TimeHelper.getNow());
    private final MessageDto message3 = new MessageDto("Author3", "Text3", TimeHelper.getNow());

    @Test
    public void addTest() {
        holder.add(message1);
        holder.add(message2);
        holder.add(message3);
        MessagesListDto messageHistory = holder.getMessageHistory();

        assertEquals(TEST_SIZE, holder.size());
        assertEquals(message3, messageHistory.getMessages().get(0));
        assertEquals(message2, messageHistory.getMessages().get(1));
    }

}
