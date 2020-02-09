package com.distributed.request;

import java.net.InetAddress;

public class LeaveRequestMessage extends RequestMessage {

    private static final String TYPE = "LEAVE";
    private InetAddress myAddress;
    private int myPort;
    private String userName;

    public LeaveRequestMessage(InetAddress myAddress, int myPort, String userName) {
        this.myAddress = myAddress;
        this.myPort = myPort;
        this.userName = userName;
    }

    @Override
    public String getMessageString() {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(TYPE).append(" ");
        messageBuilder.append(myAddress.getHostAddress().trim()).append(" ");
        messageBuilder.append(myPort);

        String messageLength = String.format("%04d", messageBuilder.length() + 5);

        return messageLength + " " + messageBuilder.toString();
    }
}
