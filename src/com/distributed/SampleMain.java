package com.distributed;

import com.distributed.FileNameManager;

import java.io.IOException;

public class SampleMain {

    public static void main(String[] args) throws IOException {
        FileNameManager.initializeFiles();
        for (String file: FileNameManager.getFileNames()){
            System.out.println(file);
        }
    }
}
