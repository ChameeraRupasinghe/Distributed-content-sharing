package com.distributed;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Listner extends Thread {

    private DatagramSocket socket;
    String s;

    @Override
    public void run() {

        System.out.println("Listner started... "+ this.getId());

        try {
            socket = SocketService.getSocket();


            while (true) {
                byte[] buffer = new byte[65536];
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                socket.receive(incoming);

                byte[] data = incoming.getData();
                s = new String(data, 0, incoming.getLength());

                System.out.println(incoming.getAddress().getHostAddress() + " : " + incoming.getPort() + " - " + s);



            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
