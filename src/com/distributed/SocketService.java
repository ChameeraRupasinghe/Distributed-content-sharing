package com.distributed;

import java.net.DatagramSocket;
import java.net.SocketException;

public class SocketService {

    private static DatagramSocket socket = null;

    public static DatagramSocket getSocket(int portNumber) throws SocketException {
        System.out.println(""+socket == null);
        if(socket == null){
            socket = new DatagramSocket(portNumber);
        }
        return socket;
    }
}
