package com.distributed;

import com.distributed.request.RequestMessage;
import com.distributed.request.SearchRequestMessage;
import com.distributed.response.*;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.StringTokenizer;

public class Listener extends Thread {

    private DatagramSocket socket;
    private int portNumber;
    private InetAddress ipAddress;
    String s;

    public Listener(int portNumber) {
        this.portNumber = portNumber;
        try {
            this.ipAddress = InetAddress.getLocalHost();
        }catch (UnknownHostException h){
            h.getStackTrace();
        }
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
        } catch (InterruptedException e) {
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

    private void handleSearch(SearchResponseMessage searchResponseMessage) throws IOException, InterruptedException {
        System.out.println("SEARCH Received from " + searchResponseMessage.getOriginalNodePort());
        FileNameManager.resetResults();
        List<String> foundData = FileNameManager.findFile(searchResponseMessage.getQuery());
        if (foundData.size() > 0) {
            for (String file : foundData) {
                FileNameManager.addToResult(file, ipAddress, portNumber);
            }
        }
        String message = searchResponseMessage.getResponseMessage(ipAddress, portNumber, FileNameManager.getresultFileNameOnly());
        DatagramPacket responseDatagram = new DatagramPacket(
                message.getBytes(),
                message.getBytes().length,
                searchResponseMessage.getOriginalNodeIpAddress(),
                searchResponseMessage.getOriginalNodePort());
        socket.send(responseDatagram);

        Thread.sleep(100);

        if (searchResponseMessage.getHops() > 0) {
            RequestMessage searchRequestMessage = new SearchRequestMessage(
                    ipAddress, portNumber, searchResponseMessage.getQuery(), searchResponseMessage.getHops() - 1);
            if (NeighbourManager.getNeighbours().size() > 0) {
                for (Neighbour neighbour : NeighbourManager.getNeighbours()) {
                    DatagramPacket messPacket = searchRequestMessage.getDatagramPacket(InetAddress.getByName(neighbour.getIp()), neighbour.getPort());
                    socket.send(messPacket);
                    System.out.println("SEARCH sent to: " + neighbour.getIp() + ":" + neighbour.getPort());
                }
            }
        }
    }

    private void handleLeave(LeaveResponseMessage leaveResponseMessage) throws IOException {
//        System.out.println("You Leaving2? " + NeighbourManager.getNeighbours().size());
        if (NeighbourManager.getNeighbours().size() > 0) {
            for (Neighbour neighbour : NeighbourManager.getNeighbours()) {

//                System.out.println(neighbour.getPort());

                if (leaveResponseMessage.getRequestSenderIpAddress().getHostAddress().equals(neighbour.getIp()) &&
                        leaveResponseMessage.getRequestSenderPort() == neighbour.getPort()) {

                    NeighbourManager.removeNeighbour(neighbour);

                    String message = leaveResponseMessage.getResponseMessage(0);
                    DatagramPacket responseDatagram = new DatagramPacket(
                            message.getBytes(),
                            message.getBytes().length,
                            leaveResponseMessage.getRequestSenderIpAddress(),
                            leaveResponseMessage.getRequestSenderPort());
                    socket.send(responseDatagram);

                    System.out.println(neighbour.getPort() + " left");
                    break;
                }

            }

        }
    }
}
