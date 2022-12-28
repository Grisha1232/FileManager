package org.example;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileManager {
    /**
     * Корневая папка.
     */
    private File root;
    /**
     * Все файлы из корневой папки.
     */
    private List<RequiredFile> filesList;

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
    private void getAllFiles(String path) throws IOException {
        File localRoot = new File(path);
        for (var file : localRoot.listFiles()) {
            if (file.isFile()) {
                if (!file.getName().contains(".DS_Store")) {
                    List<File> listRequired = new ArrayList<File>();
                    Pattern pattern = Pattern.compile("require '.+'");
                    var fr = new FileReader(file);
                    var reader = new BufferedReader(fr);
                    String line = reader.readLine();
                    while (line != null) {
                        Matcher matcher = pattern.matcher(line);
                        while (matcher.find()) {
                            var start = matcher.start();
                            var end = matcher.end();
                            String filePath = root.getAbsolutePath() + "/" + line.substring(start + 9, end - 1);
                            if (!filePath.endsWith(".txt")) {
                                filePath += ".txt";
                            }
                            File file1 = new File(filePath);
                            if (!file1.isFile()) {
                                System.out.println("\u001B[31m" + "В файле (" + file.getAbsolutePath() + ") указана неправильная директива. Директива (" + line.substring(start, end) + ") не будет использоваться" + "\u001B[0m");
                            } else {
                                System.out.println("\u001B[33m" + filePath + "\u001B[0m");
                                listRequired.add(file1);
                            }
                        }
                        line = reader.readLine();
                    }
                    filesList.add(new RequiredFile(file, listRequired));
                }
            } else if (file.isDirectory()) {
                getAllFiles(file.getAbsolutePath());
            }
        }
    }

    public void printAllFiles() throws IOException {

        filesList.sort(RequiredFile::compareTo);
        System.out.println("\u001B[32m" + root.getAbsolutePath() + "\u001B[0m");
        StringBuilder result = new StringBuilder();
        for (var file : filesList) {
            try {
                System.out.println("\u001B[32m" + (file.getFile().getAbsolutePath()) + "\u001B[0m");
                var fr = new FileReader(file.getFile());
                var reader = new BufferedReader(fr);
                String line = reader.readLine();
                while (line != null) {
                    result.append(line).append("\n");
                    line = reader.readLine();
                }
            } catch (IOException e) {
                throw e;
            }
        }
        System.out.println(result);
    }

    public String getRoot() {
        return root.getAbsolutePath();
    }

}
