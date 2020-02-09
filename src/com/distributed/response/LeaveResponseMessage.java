package com.distributed.response;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

public class LeaveResponseMessage extends ResponseMessage {
    private static final String TYPE = "LEAVEOK";
    private InetAddress requestSenderIpAddress;
    private int requestSenderPort;
    private int value;

    @Override
    public void decodeResponse(String response) throws UnknownHostException {
        System.out.println("You Leaving 'decode");
        StringTokenizer tokenizer = new StringTokenizer(response, " ");

        super.length = tokenizer.nextToken();
        super.type = tokenizer.nextToken();
        this.requestSenderIpAddress = InetAddress.getByName(tokenizer.nextToken());
        this.requestSenderPort = Integer.parseInt(tokenizer.nextToken());
        this.value = 111;
    }

    public InetAddress getRequestSenderIpAddress() {
        return requestSenderIpAddress;
    }

    public int getRequestSenderPort() {
        return requestSenderPort;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getResponseMessage(int value){
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(TYPE).append(" ");
        messageBuilder.append(value);

        String messageLength = String.format("%04d", messageBuilder.length() + 5);

        return messageLength + " " + messageBuilder.toString();
    }
}
