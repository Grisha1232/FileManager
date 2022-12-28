package org.example;

import java.io.*;
import java.util.*;

public class FileManager {
    /**
     * Корневая папка.
     */
    private File root;
    /**
     * Все файлы из корневой папки.
     */
    private ArrayList<File> filesList;

    public FileManager(String rootPath) throws IOException {
        root = new File(rootPath);
        filesList = new ArrayList<>();
        if (!root.isDirectory()) {
            throw new IOException("That is not a directory");
        }
        getAllFiles(rootPath);
    }

    /**
     * Получение всех файлов из директории. Работает рекурсивно: если по пути - директория, вызывается этот метод.
     *
     * @param path путь, по которому нужно получить все файлы
     */
    private void getAllFiles(String path) {
        File root = new File(path);
        for (var file : root.listFiles()) {
            if (file.isFile()) {
                if (!file.getName().contains(".DS_Store")) {

                    filesList.add(file);
                }
            } else if (file.isDirectory()) {
                getAllFiles(file.getAbsolutePath());
            }
        }
    }

    public void printAllFiles() throws IOException {
        System.out.println("\u001B[32m" + root.getAbsolutePath() +  "\u001B[0m");
        for (var file : filesList) {
            if (file.isFile()) {
                try {
                    System.out.println("\u001B[32m" + file.getName() +  "\u001B[0m");
                    var fr = new FileReader(file);
                    var reader = new BufferedReader(fr);
                    String line = reader.readLine();
                    while (line != null) {
                        System.out.println(line);
                        line = reader.readLine();
                    }
                } catch (IOException e) {
                    throw e;
                }
            }
        }
    }
}
