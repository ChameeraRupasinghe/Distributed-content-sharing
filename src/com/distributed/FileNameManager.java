package com.distributed;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FileNameManager {

    private static final List<String> fileNames = new ArrayList<>();

    public static List<String> getFileNames() {
        return fileNames;
    }

    public static void initializeFiles() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("/home/dulaj/Projects/temp_files/file_names"));
        List<String> temp = new ArrayList<>();

        String line = reader.readLine();

        while (line != null) {
            temp.add(line);
            line = reader.readLine();
        }
        reader.close();

        Random rand = new Random();
        int count = 0;
        while (count < 4 ){
            String file = temp.get(rand.nextInt(temp.size()));
            if (!fileNames.contains(file)){
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
}
