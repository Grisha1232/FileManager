package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        FileManager manager;
        try {
            manager = new FileManager("/Users/grigory/Desktop/ВШЭ/test For FileManager");
            manager.printAllFiles();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
    }
}