package com.distributed;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

public class FileNameManager {

    private static final List<String> fileNames = new ArrayList<>();

    private static List<FileX> results = new ArrayList<>();

    public static List<String> getFileNames() {
        return fileNames;
    }

    public static void initializeFiles() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("/home/dulaj/projects/temp_files/file_names"));
        List<String> temp = new ArrayList<>();

        String line = reader.readLine();

        while (line != null) {
            temp.add(line);
            line = reader.readLine();
        }
        reader.close();

        Random rand = new Random();
        int count = 0;
        while (count < 4) {
            String file = temp.get(rand.nextInt(temp.size()));
            if (!fileNames.contains(file)) {
                fileNames.add(file);
                count = count + 1;
            }
        }
    }

    public static List<String> findFile(String fileName) {

        List<String> tempFileNames = new ArrayList<>();

        if (fileNames.size() > 0) {
            for (String file : fileNames) {
                if (file.toLowerCase().contains(fileName.toLowerCase())) {
                    tempFileNames.add(file);
                }
            }
        }

        return tempFileNames;
    }

    public static void addToResult(String filename, InetAddress ipAddress, int port) {
        results.add(new FileX(port, ipAddress, filename));
    }

    public static void printFileInfo() {
        System.out.println("Available Files are ..");
        if (results.size() > 0) {
            for (FileX filex : results) {
                System.out.println(filex.fileName);
            }
        }
    }

    public static void resetResults() {
        results.clear();
    }

    public static List<FileX> getResults() {
        return results;
    }

    public static List<String> getResultFileNameOnly() {
        List<String> resultsFileNameOnly = new ArrayList<>();
        for (FileX file : getResults()) {
            resultsFileNameOnly.add(file.fileName);
        }
        return resultsFileNameOnly;
    }

    public static Hashtable<String, List<String>> getHashTableForQueriedFiles() throws UnknownHostException {
        Hashtable<String, List<String>> hashTableForQueriedFiles = new Hashtable<>();

        for (FileX file : getResults()) {

            if (!hashTableForQueriedFiles.containsKey(file.fileName)) {
                String movie = file.fileName;
                List<String> listOfIpAndPorts = new ArrayList<>();
                listOfIpAndPorts.add(file.ipAddress + " " + file.port);

                hashTableForQueriedFiles.put(movie, listOfIpAndPorts);
            }
        }

        return hashTableForQueriedFiles;
    }

    public static void printHashtable() throws UnknownHostException {
        Hashtable<String, List<String>> hashTableForQueriedFiles = getHashTableForQueriedFiles();

        hashTableForQueriedFiles.forEach((K, v) -> {
            System.out.println(K);
            for (String s : v) {
                System.out.print(s + " , ");
            }
        });
    }
}
