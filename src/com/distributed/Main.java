package com.distributed;

import com.distributed.messasges.RegisterRequestMessage;
import com.distributed.messasges.RequestMessage;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Main {

    static InetAddress ipAddress;

    static DatagramSocket socket;


    public static void main(String[] args) {

        System.out.println("----Distributed File Sharing System----");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the port number to communicate: ");
        int portNumber = scanner.nextInt();


        try {
            Listener listener = new Listener(portNumber);
            listener.start();

            ipAddress = InetAddress.getLocalHost();
            System.out.println("IP Address: " + ipAddress.getHostAddress());
            socket = SocketService.getSocket(portNumber);

            RequestMessage regRequestMessage = new RegisterRequestMessage(ipAddress, portNumber,
                    Config.USER_NAME);
            DatagramPacket messagePacket = regRequestMessage.getDatagramPacket(Config.BS_ADDRESS, Config.BS_PORT);
            socket.send(messagePacket);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
