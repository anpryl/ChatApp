package com.aprylutskyi.chat.server.connection.holders;

import java.util.LinkedList;

import com.aprylutskyi.chat.server.dto.MessageDto;
import com.aprylutskyi.chat.server.dto.MessagesListDto;

public class MessageHistoryHolder {

    private int maxSize;

    private LinkedList<MessageDto> messages = new LinkedList<MessageDto>();

    public MessageHistoryHolder(int historySize) {
        maxSize = historySize;
    }

    public void add(MessageDto messageDto) {
        synchronized (messages) {
            if (messages.size() == maxSize) {
                messages.pollLast();
            }
            messages.offerFirst(messageDto);
        }
    }

    public MessagesListDto getMessageHistory() {
        synchronized (messages) {
            return new MessagesListDto(messages);
        }
    }

    public int size() {
        synchronized (messages) {
            return messages.size();
        }
    }
}
