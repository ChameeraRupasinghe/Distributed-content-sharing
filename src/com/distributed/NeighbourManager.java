package com.distributed;

import com.distributed.request.JoinRequestMessage;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class NeighbourManager {

    public static final List<Neighbour> neighbours = new ArrayList<>();


    public static List<Neighbour> getNeighbours() {
        return neighbours;
    }

    public static void addNeighbour(Neighbour neighbour, int listeningPort) throws IOException {
        neighbours.add(neighbour);
        JoinRequestMessage joinRequestMessage = new JoinRequestMessage(neighbour);


        DatagramPacket joinDataPacket = joinRequestMessage.getDatagramPacket(InetAddress.getByName(neighbour.getIp()),
                neighbour.getPort());
        DatagramSocket socket = SocketService.getSocket(listeningPort);
        socket.send(joinDataPacket);


    }

}
