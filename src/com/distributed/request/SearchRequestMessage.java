package com.distributed.request;

import java.net.InetAddress;

public class SearchRequestMessage extends RequestMessage{

    private static final String TYPE = "SER";
    private InetAddress myAddress;
    private int myPort;

    private String fileName;
    private int hops;


    public SearchRequestMessage(InetAddress myAddress, int myPort, String fileName, int hops) {
        this.myAddress = myAddress;
        this.myPort = myPort;
        this.fileName = fileName;
        this.hops = hops;
    }

    @Override
    public String getMessageString() {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(TYPE).append(" ");
        messageBuilder.append(myAddress.getHostAddress().trim()).append(" ");
        messageBuilder.append(myPort).append(" ");
        messageBuilder.append(fileName).append(" ");
        messageBuilder.append(hops);

        String messageLength = String.format("%04d", messageBuilder.length() + 5);

        return messageLength + " " + messageBuilder.toString();
    }
}
