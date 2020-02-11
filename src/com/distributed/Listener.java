package com.distributed;

import com.distributed.response.*;

import java.io.IOException;
import java.net.*;
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
                String incomPort = Integer.toString(incoming.getPort());
                String joinRes = s + " " + incomPort;
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
                        handleRegOk((RegisterResponseMessage) responseMessage);
                        break;

                    case "JOIN":
                        responseMessage = new JoinResponseMessage();
                        responseMessage.decodeResponse(joinRes);
                        handleJoin((JoinResponseMessage) responseMessage);
                        break;

                    case "SER":
                        System.out.println("SEARCH Received");
                        responseMessage = new SearchResponseMessage();
                        responseMessage.decodeResponse(s);
                        handleSearch((SearchResponseMessage) responseMessage);
                        break;

                    case "SEROK":
                        System.out.println("SER OK came");
                        handleSearchOk(s);
                        break;

                    case "LEAVE":
                        System.out.println("You Leaving?");
                        responseMessage = new LeaveResponseMessage();
                        responseMessage.decodeResponse(s);
                        handleLeave((LeaveResponseMessage) responseMessage);
                        break;

                    case "LEAVEOK":
                        System.out.println("LEAVEOK Received");
                        System.exit(0);
                        break;
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSearchOk(String response) throws UnknownHostException {
        System.out.println("You OK SEARCH 'decode");
        StringTokenizer tokenizer = new StringTokenizer(response, " ");

        int length = Integer.parseInt(tokenizer.nextToken());
        String type = tokenizer.nextToken();
        int no_files = Integer.parseInt(tokenizer.nextToken());
        InetAddress ip = InetAddress.getByName(tokenizer.nextToken());
        int port = Integer.parseInt(tokenizer.nextToken());
        int hops = Integer.parseInt(tokenizer.nextToken());

        if (no_files > 0 && no_files < SearchResponseMessage.ERROR)
            for (int i = 0; i < no_files; i++) {
                FileNameManager.addToResult(tokenizer.nextToken(), ip, port);
            }

        if (hops < 1) {
            System.out.println("Finished");
        }
    }

    private void handleRegOk(RegisterResponseMessage registerResponseMessage) {
        if (registerResponseMessage.getNeighbours() != null && registerResponseMessage.getNeighbours().size() > 0) {
            for (Neighbour neighbour : registerResponseMessage.getNeighbours()) {
                try {
                    NeighbourManager.addNeighbour(neighbour);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Main.setIs_Reged(true);
    }

    private void handleJoin(JoinResponseMessage joinResponseMessage) throws IOException {
        int value = 0;

        try {
            NeighbourManager.addNeighbour(
                    new Neighbour(joinResponseMessage.getIp().getHostAddress(), joinResponseMessage.getPort())
            );
        } catch (IOException e) {
            e.getStackTrace();
            value = 9999;
        }
        System.out.println("JOIN OK to " + joinResponseMessage.getPort());

        String message = joinResponseMessage.getResponseMessage(value);
        DatagramPacket responseDatagram = new DatagramPacket(
                message.getBytes(),
                message.getBytes().length,
                joinResponseMessage.getIp(),
                joinResponseMessage.getPort());
        socket.send(responseDatagram);
    }

    private void handleSearch(SearchResponseMessage searchResponseMessage) {

    }

    private void handleLeave(LeaveResponseMessage leaveResponseMessage) throws IOException {
        System.out.println("You Leaving2? " + NeighbourManager.getNeighbours().size());
        if (NeighbourManager.getNeighbours().size() > 0) {
            for (Neighbour neighbour : NeighbourManager.getNeighbours()) {

                System.out.println(neighbour.getPort());
                System.out.println(leaveResponseMessage.getRequestSenderIpAddress().toString() + "," + neighbour.getIp());

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
