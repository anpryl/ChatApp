package com.aprylutskyi.chat.server.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "messages")
@XmlAccessorType(XmlAccessType.FIELD)
public class MessagesListDto implements Processable {

    private static final long serialVersionUID = 1003285616103365630L;

    @XmlElement(name = "message")
    private List<MessageDto> messages = new ArrayList<>();

    public MessagesListDto() {
    }

    public MessagesListDto(MessageDto... messages) {
        for (MessageDto messageDto : messages) {
            this.messages.add(messageDto);
        }
    }

    public MessagesListDto(List<MessageDto> messages) {
        this.messages = messages;
    }

    public List<MessageDto> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDto> messages) {
        this.messages = messages;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((messages == null) ? 0 : messages.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MessagesListDto other = (MessagesListDto) obj;
        if (messages == null) {
            if (other.messages != null)
                return false;
        } else if (!messages.equals(other.messages))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "MessagesListDto [messages=" + messages + "]";
    }

}
