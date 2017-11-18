package com.example.clases;

import java.util.Date;

/**
 * Created by juanpablorn30 on 31/10/17.
 */

public class ChatMessage {

    private String messageText;
    private String UidSender;
    private Date messageTime;

    public ChatMessage(String messageText, String UidSender) {
        this.messageText = messageText;
        this.UidSender = UidSender;
        this.messageTime = new Date();
    }

    public ChatMessage(String messageText, String uidSender, Date messageTime) {
        this.messageText = messageText;
        UidSender = uidSender;
        this.messageTime = messageTime;
    }

    public ChatMessage() {

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getUidSender() {
        return UidSender;
    }

    public void setUidSender(String uidSender) {
        UidSender = uidSender;
    }

    public Long getMessageTime() {
        return messageTime.getTime();
    }

    public void setMessageTime(Long messageTime) {
        this.messageTime = new Date(messageTime);
    }
}
