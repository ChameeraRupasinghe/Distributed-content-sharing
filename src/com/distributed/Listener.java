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
        } catch (UnknownHostException h) {
            h.getStackTrace();
        }
    }

    @Override
    public void run() {

        LoggerX.log("Listener started on " + portNumber);        //logging

        try {
            socket = SocketService.getSocket(this.portNumber);

            while (true) {
                byte[] buffer = new byte[65536];
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                socket.receive(incoming);

                byte[] data = incoming.getData();
                s = new String(data, 0, incoming.getLength());

                LoggerX.log("Received: (" + incoming.getAddress().getHostAddress() + ":" + incoming.getPort() + ") " + s);        //logging

                String incomPort = Integer.toString(incoming.getPort());
                String joinRes = s + " " + incomPort;
                StringTokenizer resTokenizer = new StringTokenizer(s, " ");
                String resLength = resTokenizer.nextToken();
                String type = resTokenizer.nextToken();

                ResponseMessage responseMessage;

                switch (type) {
                    case "REGOK":
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
                        handleSearchOk(s);
                        break;

                    case "LEAVE":
                        responseMessage = new LeaveResponseMessage();
                        responseMessage.decodeResponse(s);
                        handleLeave((LeaveResponseMessage) responseMessage);
                        break;

                    case "LEAVEOK":
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
            LoggerX.log("Searching finished. " + FileNameManager.getResults().size() + " files found.");        //logging
        }
    }

    private void handleRegOk(RegisterResponseMessage registerResponseMessage) {
        if (registerResponseMessage.getNoNodes() <= 2) {
            if (registerResponseMessage.getNeighbours() != null && registerResponseMessage.getNeighbours().size() > 0) {
                for (Neighbour neighbour : registerResponseMessage.getNeighbours()) {
                    try {
                        NeighbourManager.addNeighbour(neighbour);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            Main.setRegistered(true);
        } else {
            Main.setRegistered(false);
            System.exit(0);
        }
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

        LoggerX.log("JoinOk sent to " + joinResponseMessage.getPort());        //logging

        String message = joinResponseMessage.getResponseMessage(value);
        DatagramPacket responseDatagram = new DatagramPacket(
                message.getBytes(),
                message.getBytes().length,
                joinResponseMessage.getIp(),
                joinResponseMessage.getPort());
        socket.send(responseDatagram);
    }

    private void handleSearch(SearchResponseMessage searchResponseMessage) throws IOException, InterruptedException {
        FileNameManager.resetResults();
        List<String> foundData = FileNameManager.findFile(searchResponseMessage.getQuery());
        if (foundData.size() > 0) {
            for (String file : foundData) {
                FileNameManager.addToResult(file, ipAddress, portNumber);       //Not needed
            }
        }
        String message = searchResponseMessage.getResponseMessage(ipAddress, portNumber, FileNameManager.getResultFileNameOnly());      //foundData can be used directly
        DatagramPacket responseDatagram = new DatagramPacket(
                message.getBytes(),
                message.getBytes().length,
                searchResponseMessage.getOriginalNodeIpAddress(),
                searchResponseMessage.getOriginalNodePort());
        socket.send(responseDatagram);

        LoggerX.log("SearchOk sent to " + searchResponseMessage.getOriginalNodePort() + ". found=" + foundData.size());        //logging

        Thread.sleep(100);

        if (searchResponseMessage.getHops() > 0) {

            RequestMessage searchRequestMessage = new SearchRequestMessage(
                    searchResponseMessage.getOriginalNodeIpAddress(),
                    searchResponseMessage.getOriginalNodePort(),
                    searchResponseMessage.getQuery(),
                    searchResponseMessage.getHops() - 1);

            if (NeighbourManager.getNeighbours().size() > 0) {
                for (Neighbour neighbour : NeighbourManager.getNeighbours()) {

                    //TODO: Check whether the original search requester is NOT selected
                    //TODO: Check whether previous recipients are NOT selected

                    DatagramPacket messPacket = searchRequestMessage.getDatagramPacket(InetAddress.getByName(neighbour.getIp()), neighbour.getPort());
                    socket.send(messPacket);

                    LoggerX.log("Search sent to " + neighbour.getPort() + ". with hops=" + (searchResponseMessage.getHops() - 1));        //logging
                }
            }
        } else {
            LoggerX.log("Searching finished for this at hops=" + (searchResponseMessage.getHops()));        //logging
        }
    }

    private void handleLeave(LeaveResponseMessage leaveResponseMessage) throws IOException {

        if (NeighbourManager.getNeighbours().size() > 0) {

            for (Neighbour neighbour : NeighbourManager.getNeighbours()) {

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

                    LoggerX.log("Node:" + neighbour.getPort() + " left");        //logging

                    break;
                }

            }

        }
    }
}
