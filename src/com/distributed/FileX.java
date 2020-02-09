package com.distributed;

import java.net.InetAddress;

public class FileX {
    public int port;
    public InetAddress ipAddress;
    public String fileName;

    public FileX(int port, InetAddress ipAddress, String fileName) {
        this.port = port;
        this.ipAddress = ipAddress;
        this.fileName = fileName;
    }
}
