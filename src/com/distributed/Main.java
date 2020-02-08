package com.distributed;

import com.distributed.request.RegisterRequestMessage;
import com.distributed.request.LeaveRequestMessage;
import com.distributed.request.RequestMessage;
import com.distributed.request.SearchRequestMessage;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Main {

    static InetAddress ipAddress;
    static int listeningPort;
//    static String userName;

    static DatagramSocket socket;


    public static void main(String[] args) {

        System.out.println("----Distributed File Sharing System----");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a User Name: ");
        String userName = scanner.nextLine();
        System.out.println("Enter the port number to communicate: ");
        int listeningPort = scanner.nextInt();

        System.out.println("UNAME: " + userName + " Port: " + listeningPort);


        try {
            initializeListener(listeningPort);

            ipAddress = InetAddress.getLocalHost();
            System.out.println("IP Address: " + ipAddress.getHostAddress());
            socket = SocketService.getSocket(listeningPort);

            //Send registration request
            RequestMessage regRequestMessage = new RegisterRequestMessage(ipAddress, listeningPort, userName);
            DatagramPacket regMessagePacket = regRequestMessage.getDatagramPacket(Config.BS_ADDRESS, Config.BS_PORT);
            socket.send(regMessagePacket);

//            TODO: JOIN Request to other nodes (get the details from the bootstrap

//            TODO: SEARCH (get input from the terminal)
            while (true) {
                // take input and send the packet
                System.out.println("\nSelect option : ");
                System.out.println("1: Search");
                System.out.println("2: Disconnect");
                int option = Integer.parseInt(scanner.nextLine().trim());
                switch (option) {
                    case 1:
                        System.out.println("Enter the file name: ");
                        handleSearch(scanner.nextLine().trim());
                        break;

                    case 2:
                        System.out.println("Disconnecting this node");
                        handleDisconnect();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Please enter a valid input");
                        break;
                }
            }

//            TODO: Download

//            TODO: LEAVE
//            RequestMessage leaveRequestMessage = new LeaveRequestMessage(ipAddress, listeningPort, userName);
//            DatagramPacket messPacket = leaveRequestMessage.getDatagramPacket(Config., destinationPort)

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void initializeListener(int listeningPort) {
        Listener listener = new Listener(listeningPort);
        listener.start();
    }

    static void handleSearch(String filename){
        RequestMessage searchRequestMessage = new SearchRequestMessage(ipAddress, );
    }

    static void handleDisconnect(){

    }
}
