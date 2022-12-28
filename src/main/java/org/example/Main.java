package org.example;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FileManager manager = null;
        while (manager == null) {
            System.out.print("Enter the root directory: ");
            String root;
            root = scanner.nextLine();
            try {
                manager = new FileManager(root);
                manager.printAllFiles();
            } catch (IOException e) {
                manager = null;
                System.out.println(e.getMessage());
            }
        }
    }
}