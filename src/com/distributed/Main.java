package com.distributed;

import com.distributed.request.*;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.Scanner;

public class Main {

    static InetAddress ipAddress;
    static int listeningPort;
    static String userName;

    private static boolean registered;
    private static boolean joined;

    static DatagramSocket socket;


    public static void main(String[] args) {

        registered = false;
        joined = false;

        userName = args[0].trim();
        listeningPort = Integer.parseInt(args[1].trim());

        System.out.println("----Distributed File Sharing System----");
        Scanner scanner = new Scanner(System.in);
//        System.out.println("Enter a User Name: ");
//        String userName = scanner.nextLine();

//        System.out.println("Enter the port number to communicate: ");
//        listeningPort = scanner.nextInt();

        System.out.println("UNAME: " + userName + " Port: " + listeningPort);


        try {
            initializeListener(listeningPort);

            ipAddress = InetAddress.getLocalHost();
            System.out.println("IP Address: " + ipAddress.getHostAddress());
            Thread.sleep(200);
            socket = SocketService.getSocket(listeningPort);

            //Send registration request
            RequestMessage regRequestMessage = new RegisterRequestMessage(ipAddress, listeningPort, userName);
            DatagramPacket regMessagePacket = regRequestMessage.getDatagramPacket(Config.BS_ADDRESS, Config.BS_PORT);
            socket.send(regMessagePacket);
            FileNameManager.initializeFiles();

            while (!registered) {
                Thread.sleep(200);
            }

            RequestMessage joinRequestMessage = new JoinRequestMessage(ipAddress, listeningPort);

            if (NeighbourManager.getNeighbours().size() > 0) {
                for (Neighbour neighbour : NeighbourManager.getNeighbours()) {

                    System.out.println("JOIN SENT " + neighbour.getPort());

                        String message = joinRequestMessage.getMessageString();
                        DatagramPacket responseDatagram = new DatagramPacket(
                                message.getBytes(),
                                message.getBytes().length,
                                InetAddress.getByName(neighbour.getIp()),
                                neighbour.getPort());
                        socket.send(responseDatagram);
                }
            }

            Thread.sleep(200);
            while (true) {
                // take input and send the packet
                System.out.println("Select option : ");
                System.out.println("1: Search");
                System.out.println("2: Disconnect");
                //System.out.println(scanner.nextLine().trim());          //This is nonsense, need to remove in future
                int option = Integer.parseInt(scanner.nextLine().trim());
                switch (option) {
                    case 1:
                        System.out.println("Enter the file name: ");
                        handleSearch(scanner.nextLine().trim(), 5);
                        break;
                    case 2:
                        handleDisconnect();
                        break;
                    default:
                        System.out.println("Please enter a valid input");
                        break;
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void initializeListener(int listeningPort) throws UnknownHostException {
        Listener listener = new Listener(listeningPort);
        listener.start();
    }

    static void handleSearch(String fileName, int hops) throws IOException {
        FileNameManager.resetResults();
        List<String> foundData = FileNameManager.findFile(fileName);
        if (foundData.size() > 0) {
            for (String file : foundData) {
                FileNameManager.addToResult(file, ipAddress, listeningPort);
            }
        }

        RequestMessage searchRequestMessage = new SearchRequestMessage(ipAddress, listeningPort, fileName, hops - 1);
        for (Neighbour neighbour : NeighbourManager.getNeighbours()) {
            DatagramPacket messPacket = searchRequestMessage.getDatagramPacket(InetAddress.getByName(neighbour.getIp()), neighbour.getPort());
            socket.send(messPacket);
            System.out.println("SEARCH sent to: " + neighbour.getIp() + ":" + neighbour.getPort());
        }

    }

    static void handleDisconnect() throws IOException {
        System.out.println("Disconnecting this node");
        RequestMessage leaveRequestMessage = new LeaveRequestMessage(ipAddress, listeningPort, userName);
        for (Neighbour neighbour : NeighbourManager.getNeighbours()) {
            DatagramPacket messPacket = leaveRequestMessage.getDatagramPacket(InetAddress.getByName(neighbour.getIp()), neighbour.getPort());
            socket.send(messPacket);
            System.out.println("LEAVE sent to: " + neighbour.getIp() + ":" + neighbour.getPort());
        }
        NeighbourManager.clearNeighbourList();
//        RequestMessage leaveRequestMessage = new LeaveRequestMessage(ipAddress, listeningPort, userName);
//        DatagramPacket messPacket = leaveRequestMessage.getDatagramPacket(Config., destinationPort)
        System.exit(0);
    }

    public static boolean isRegistered() {
        return registered;
    }

    public static void setRegistered(boolean registered) {
        Main.registered = registered;
    }
}
