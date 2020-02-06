package com.distributed;

import com.distributed.request.RegisterRequestMessage;
import com.distributed.request.RequestMessage;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Main {

    static InetAddress ipAddress;
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
            DatagramPacket messagePacket = regRequestMessage.getDatagramPacket(Config.BS_ADDRESS, Config.BS_PORT);
            socket.send(messagePacket);

//            TODO: JOIN Request to other nodes (get the deatils from the bootstrap

//            TODO: SEARCH (get input from the terminal)

//            TODO: Download

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
}
