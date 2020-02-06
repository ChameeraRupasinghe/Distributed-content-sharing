package com.distributed.request;

import com.distributed.Neighbour;

public class JoinRequestMessage extends RequestMessage {

    private static final String TYPE = "JOIN";
    private String ipAddress;
    private int port;

    public JoinRequestMessage(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public JoinRequestMessage(Neighbour neighbour){
        this.ipAddress = neighbour.getIp();
        this.port = neighbour.getPort();
    }



    @Override
    public String getMessageString() {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(TYPE).append(" ");
        messageBuilder.append(ipAddress).append(" ");
        messageBuilder.append(port);

        String messageLength = String.format("%04d", messageBuilder.length() + 5);
        return messageLength + " " + messageBuilder.toString();
    }
}
