package com.distributed;

import com.distributed.request.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {

    static InetAddress ipAddress;
    static int listeningPort;
    static String userName;

    private static boolean registered;
    private static boolean joined;
    private static String search_done;

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

        try {
            initializeListener(listeningPort);

            ipAddress = InetAddress.getLocalHost();
            System.out.println("IP Address: " + ipAddress.getHostAddress());
            Thread.sleep(200);
            socket = SocketService.getSocket(listeningPort);

            LoggerX.log(userName + " " + listeningPort + " started");            //logging

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
                    String message = joinRequestMessage.getMessageString();
                    DatagramPacket responseDatagram = new DatagramPacket(
                            message.getBytes(),
                            message.getBytes().length,
                            InetAddress.getByName(neighbour.getIp()),
                            neighbour.getPort());
                    socket.send(responseDatagram);
                    LoggerX.log("Join request sent to " + neighbour.getPort());        //logging
                }
            }

            Thread.sleep(200);
            while (true) {
                // take input and send the packet
                System.out.println("Select option : ");
                System.out.println("1: Search");
                System.out.println("2: Disconnect");
                System.out.println("3: Get last query results");
                System.out.println("4: Download file");
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
                    case 3:
                        handleGetLastQueryResult();
                        break;
                    case 4:
                        System.out.println("Enter File Name to download: ");
                        handleDownload(scanner.nextLine().trim());
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
            LoggerX.log("Some files were found in here. hops=" + hops);        //logging
        }

        RequestMessage searchRequestMessage = new SearchRequestMessage(ipAddress, listeningPort, fileName, hops - 1);
        for (Neighbour neighbour : NeighbourManager.getNeighbours()) {
            DatagramPacket messPacket = searchRequestMessage.getDatagramPacket(InetAddress.getByName(neighbour.getIp()), neighbour.getPort());
            socket.send(messPacket);

            LoggerX.log("Search request sent to " + neighbour.getPort() + ". with hops=" + (hops - 1));        //logging
        }

    }

    static void handleDisconnect() throws IOException {
        LoggerX.log("-------Disconnecting this node---------");        //logging

        RequestMessage leaveRequestMessage = new LeaveRequestMessage(ipAddress, listeningPort, userName);
        for (Neighbour neighbour : NeighbourManager.getNeighbours()) {
            DatagramPacket messPacket = leaveRequestMessage.getDatagramPacket(InetAddress.getByName(neighbour.getIp()), neighbour.getPort());
            socket.send(messPacket);

            LoggerX.log("leave request sent to " + neighbour.getPort());        //logging
        }
        NeighbourManager.clearNeighbourList();

        RequestMessage unregisterRequestMessage = new UnregisterRequestMessage(ipAddress, listeningPort, userName);
        DatagramPacket unregisterRequestMessageDatagramPacket = unregisterRequestMessage.getDatagramPacket(Config.BS_ADDRESS, Config.BS_PORT);
        socket.send(unregisterRequestMessageDatagramPacket);

        System.exit(0);
    }

    static void handleGetLastQueryResult() {
        if (FileNameManager.getResults().size() > 0) {
            FileNameManager.printFileInfo();
        }
    }

    private static void handleDownload(String movie) throws UnknownHostException {
        if (FileNameManager.getResults().size() > 0) {
            List<String> ipAndPort = FileNameManager.getHashTableForQueriedFiles().get(movie);
            Random rand = new Random();
            String randIpAndPortToDownloadFile = ipAndPort.get(rand.nextInt(ipAndPort.size()));

            String ipToDownload = randIpAndPortToDownloadFile.split(" ")[0];
            String portToDownload = randIpAndPortToDownloadFile.split(" ")[1];

            Util.downloadFile(ipToDownload, portToDownload, movie);

        }
    }

    public static boolean isRegistered() {
        return registered;
    }

    public static void setRegistered(boolean registered) {
        Main.registered = registered;
    }

    public static String getSearch_done() {
        return search_done;
    }

    public static void setSearch_done(String search_done) {
        Main.search_done = search_done;
    }
}
