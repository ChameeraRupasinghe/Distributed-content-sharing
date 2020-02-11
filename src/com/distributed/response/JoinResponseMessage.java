package com.distributed.response;

import com.distributed.Neighbour;
import com.distributed.NeighbourManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

public class JoinResponseMessage extends ResponseMessage {
    private static final String TYPE = "JOINOK";
//    private List<Neighbour> neighbours;
    private int noNodes;
    private InetAddress ip;
//    private String myip;
    private int port;

    @Override
    public void decodeResponse(String response) throws UnknownHostException {
        StringTokenizer tokenizer = new StringTokenizer(response, " ");
        super.length = tokenizer.nextToken();
        super.type = tokenizer.nextToken();
        ip = InetAddress.getByName(tokenizer.nextToken());
        port = Integer.parseInt(tokenizer.nextToken());
    }

    private void addNeighbours(String ip, String port) throws IOException {
        int portNumber = Integer.parseInt(port);
        Neighbour neighbour = new Neighbour(ip, portNumber);
        //neighbours.add(neighbour);
//        NeighbourManager.neighbours.add(neighbour);
        NeighbourManager.addNeighbour(neighbour);
//        System.out.println("New neighbour added: " + neighbour.toString());

    }

    public String getResponseMessage(int value) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(TYPE).append(" ");
        messageBuilder.append(value);

        String messageLength = String.format("%04d", messageBuilder.length() + 5);

        return messageLength + " " + messageBuilder.toString();
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}