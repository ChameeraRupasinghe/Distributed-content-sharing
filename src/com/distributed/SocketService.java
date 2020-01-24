package com.distributed;

import java.net.DatagramSocket;
import java.net.SocketException;

public class SocketService {

    private static DatagramSocket socket = null;

    public static DatagramSocket getSocket() throws SocketException {
        if(socket == null){
            socket = new DatagramSocket(Config.LISTENING_PORT);
        }
        return socket;
    }
}
