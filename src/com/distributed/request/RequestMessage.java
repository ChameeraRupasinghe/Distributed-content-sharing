package com.distributed.request;

import java.net.DatagramPacket;
import java.net.InetAddress;

public abstract class RequestMessage {

    public DatagramPacket getDatagramPacket(InetAddress destinationIp, int destinationPort) {
        String message = getMessageString();
        System.out.println("Message: " + message);
        return new DatagramPacket(message.getBytes(), message.getBytes().length, destinationIp, destinationPort);
    }

    public abstract String getMessageString();
}
