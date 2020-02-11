package com.distributed.response;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class SearchResponseMessage extends ResponseMessage {
    public static final String TYPE = "SEROK";
    public static final int FAILURE = 9999; //failure due to node unreachable
    public static final int ERROR = 9998; // some other error

//    private InetAddress myIpAddress;
//    private int myPort;

    private InetAddress originalNodeIpAddress;
    private int originalNodePort;

    private int hops;
    private String query;

    List<String> foundFileNames;

    @Override
    public void decodeResponse(String response) throws UnknownHostException {
        StringTokenizer tokenizer = new StringTokenizer(response, " ");

        super.length = tokenizer.nextToken();
        super.type = tokenizer.nextToken();
        this.originalNodeIpAddress = InetAddress.getByName(tokenizer.nextToken());
        this.originalNodePort = Integer.parseInt(tokenizer.nextToken());
        this.query = tokenizer.nextToken();
        this.hops = Integer.parseInt(tokenizer.nextToken());
    }

    public String getResponseMessage(InetAddress myIpAddress, int myPort, List<String> files) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(TYPE).append(" ");
        messageBuilder.append(files.size()).append(" ");
        messageBuilder.append(myIpAddress.getHostAddress().trim()).append(" ");
        messageBuilder.append(myPort).append(" ");
        messageBuilder.append(hops).append(" ");
        if (files.size() > 0) {
            for (String file : files) {
                messageBuilder.append(file).append(" ");
            }
        }

        String messageLength = String.format("%04d", messageBuilder.length() + 5);

        return messageLength + " " + messageBuilder.toString().trim();
    }

    public InetAddress getOriginalNodeIpAddress() {
        return originalNodeIpAddress;
    }

    public int getOriginalNodePort() {
        return originalNodePort;
    }

    public String getQuery() {
        return query;
    }

    public int getHops() {
        return hops;
    }
}
