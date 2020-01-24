package com.distributed;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {

    static InetAddress ipAddress;

    public static void main(String[] args) {

        try {
            ipAddress = InetAddress.getLocalHost();
            System.out.println("IP Address: "+ ipAddress.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // write your code here
    }
}
