package com.distributed;

import com.distributed.messasges.RegisterRequestMessage;
import com.distributed.messasges.RequestMessage;

import java.io.IOException;
import java.net.*;

public class Main {

    static InetAddress ipAddress;

    static DatagramSocket socket;


    public static void main(String[] args) {


        try {
            Listner listner = new Listner();
            listner.start();

            ipAddress = InetAddress.getLocalHost();
            System.out.println("IP Address: " + ipAddress.getHostAddress());
            socket = SocketService.getSocket();

            RequestMessage regRequestMessage = new RegisterRequestMessage(ipAddress, Config.LISTENING_PORT,
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
