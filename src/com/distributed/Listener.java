package com.distributed;

import com.distributed.response.RegisterResponseMessage;
import com.distributed.response.ResponseMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.StringTokenizer;

public class Listener extends Thread {

    private DatagramSocket socket;
    private int portNumber;
    String s;

    public Listener(int portNumber) {
        this.portNumber = portNumber;
    }

    @Override
    public void run() {

        System.out.println("Listener started... " + this.getId());

        try {
            socket = SocketService.getSocket(this.portNumber);

            while (true) {
                byte[] buffer = new byte[65536];
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                socket.receive(incoming);

                byte[] data = incoming.getData();
                s = new String(data, 0, incoming.getLength());
                System.out.println(incoming.getAddress().getHostAddress() + " : " + incoming.getPort() + " - " + s);

                StringTokenizer resTokenizer = new StringTokenizer(s, " ");
                String resLength = resTokenizer.nextToken();
                String type = resTokenizer.nextToken();

                ResponseMessage responseMessage;

                switch (type) {
                    case "REGOK":
                        System.out.println("REGOK BRO");
                        responseMessage = new RegisterResponseMessage();
                        responseMessage.decodeResponse(s);
                        saveNeighbourDetails((RegisterResponseMessage) responseMessage);
                        break;

//                    TODO: JOIN
//                    TODO: SER ->
//                    TODO: LEAVE

                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveNeighbourDetails(RegisterResponseMessage registerResponseMessage) {
        if (registerResponseMessage.getNeighbours() != null && registerResponseMessage.getNeighbours().size() > 0) {
            for (Neighbour neighbour : registerResponseMessage.getNeighbours()) {
                try {
                    NeighbourManager.addNeighbour(neighbour, portNumber);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
