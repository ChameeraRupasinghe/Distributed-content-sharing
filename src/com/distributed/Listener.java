package com.distributed;

import com.distributed.response.LeaveResponseMessage;
import com.distributed.response.RegisterResponseMessage;
import com.distributed.response.ResponseMessage;
import com.distributed.response.JoinResponseMessage;

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
                String incomPort= Integer.toString(incoming.getPort());
                String joinRes= s+ " " + incomPort;
                StringTokenizer resTokenizer = new StringTokenizer(s, " ");
                String resLength = resTokenizer.nextToken();
                String type = resTokenizer.nextToken();
                //System.out.println(type);
                ResponseMessage responseMessage;

                switch (type) {
                    case "REGOK":
                        System.out.println("REGOK BRO");
                        responseMessage = new RegisterResponseMessage();
                        responseMessage.decodeResponse(s);
                        saveNeighbourDetails((RegisterResponseMessage) responseMessage);
                        break;

                    case "JOIN" :
                        System.out.println("JOIN OK");
                        responseMessage = new JoinResponseMessage();
                        responseMessage.decodeResponse(joinRes);


                    case "LEAVE":
                        System.out.println("You Leaving?");
                        responseMessage = new LeaveResponseMessage();
                        responseMessage.decodeResponse(s);
                        handleLeave((LeaveResponseMessage) responseMessage);
                        break;

                    case "LEAVEOK":
                        System.out.println("LEAVEOK Received");
                        System.exit(0);
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

    private void handleLeave(LeaveResponseMessage leaveResponseMessage) throws IOException {
        System.out.println("You Leaving2? " + NeighbourManager.getNeighbours().size());
        if (NeighbourManager.getNeighbours().size() > 0) {
            for (Neighbour neighbour : NeighbourManager.getNeighbours()) {

                System.out.println(neighbour.getPort());

                if (leaveResponseMessage.getRequestSenderIpAddress().toString().equals(neighbour.getIp()) &&
                        leaveResponseMessage.getRequestSenderPort() == neighbour.getPort()) {

                    NeighbourManager.removeNeighbour(neighbour);
                    System.out.println("One Down");

                    String message = leaveResponseMessage.getResponseMessage(0);
                    DatagramPacket responseDatagram = new DatagramPacket(
                            message.getBytes(),
                            message.getBytes().length,
                            leaveResponseMessage.getRequestSenderIpAddress(),
                            leaveResponseMessage.getRequestSenderPort());
                    socket.send(responseDatagram);

                    System.out.println("LEAVEOK BRO");
                    break;
                }

            }

        }
    }
}
