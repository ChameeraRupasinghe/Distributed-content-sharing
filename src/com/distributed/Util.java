package com.distributed;

import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {

    public static void downloadFile(String ip, String port, String fileName) {
        String fileUrl = "http://" + ip + ":" + port + "/" + fileName;
        System.out.println("URL " + fileUrl);

        try (BufferedInputStream in = new BufferedInputStream(new URL(fileUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getHash(File file) throws NoSuchAlgorithmException, IOException {
        int count;
        byte[] dataBytes = new byte[65536];

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        while ((count = bufferedInputStream.read(dataBytes)) > 0) {
            digest.update(dataBytes, 0, count);
        }
        bufferedInputStream.close();

        byte[] hash = digest.digest();
        String encodedHash = new BASE64Encoder().encode(hash);
        System.out.println(encodedHash);
    }


}
