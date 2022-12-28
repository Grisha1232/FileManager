package org.example;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileManager {
    /**
     * Корневая папка.
     */
    private final File root;

    /**
     * Все файлы из корневой папки.
     */
    private final List<RequiredFile> filesList;

    private String cycleFiles = "";

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
        for (var file : Objects.requireNonNull(localRoot.listFiles())) {
            if (file.isFile()) {
                // Заглушка для непонятного файла на MACOS
                if (!file.getName().contains(".DS_Store")) {
                    RequiredFile reqFile = null;
                    boolean isAlreadyCreated = false;
                    for (var elem : filesList) {
                        if (elem.getFile().getAbsolutePath().equals(file.getAbsolutePath())) {
                            isAlreadyCreated = true;
                            reqFile = elem;
                        }
                    }
                    if (!isAlreadyCreated) {
                        reqFile = new RequiredFile(file);
                        filesList.add(reqFile);
                    }
                    // Регулярное выражение для поиска зависимых фалов
                    Pattern pattern = Pattern.compile("require '.+'");
                    var fr = new FileReader(file);
                    var reader = new BufferedReader(fr);
                    String line = reader.readLine();
                    while (line != null) {
                        Matcher matcher = pattern.matcher(line);
                        while (matcher.find()) {
                            var start = matcher.start();
                            var end = matcher.end();
                            // Небольшая помощь в написании require (если не указано расширение, добавляет автоматически '.txt')
                            String filePath = root.getAbsolutePath() + "/" + line.substring(start + 9, end - 1);
                            if (!filePath.endsWith(".txt")) {
                                filePath += ".txt";
                            }
                            // Проверка на существование файла
                            File checkFile = new File(filePath);
                            if (!checkFile.isFile()) {
                                System.out.println("\u001B[31m" + "В файле (" + file.getAbsolutePath() + ") указана неправильная директива. Директива (" + line.substring(start, end) + ") не будет использоваться" + "\u001B[0m");
                            } else {
                                isAlreadyCreated = false;
                                for (var elem : filesList) {
                                    if (elem.getFile().getAbsolutePath().equals(checkFile.getAbsolutePath())) {
                                        isAlreadyCreated = true;
                                        reqFile.addRequire(elem);
                                    }
                                }
                                if (!isAlreadyCreated) {
                                    RequiredFile reqNewFile = new RequiredFile(checkFile);
                                    reqFile.addRequire(reqNewFile);
                                    filesList.add(reqNewFile);
                                }
                            }
                        }
                        line = reader.readLine();
                    }
                }
            } else if (file.isDirectory()) {
                getAllFiles(file.getAbsolutePath());
            }
        }
    }

    private boolean isCorrectDirectives() {
        boolean check = false;

        for (var file : filesList) {
            Map<String, Boolean> used = new HashMap<>();
            for (var elem : filesList) {
                used.put(elem.getFile().getAbsolutePath(), false);
            }
            for (var req : file.getRequired()) {
                cycleFiles = file.getFile().getName();
                check = isCycledRequire(used, file, req);
                if (check) {
                    break;
                }
            }
            if (check) {
                break;
            }
        }
        return !check;
    }

    private boolean isCycledRequire(Map<String, Boolean> used, RequiredFile start, RequiredFile element) {
        if (start.getFile().getAbsolutePath().equals(element.getFile().getAbsolutePath())) {
            return true;
        }
        if (used.get(element.getFile().getAbsolutePath())) {
            return false;
        }
        used.put(element.getFile().getAbsolutePath(), true);
        for (var elem : element.getRequired()) {
            cycleFiles += " -> " + elem.getFile().getName();
            return isCycledRequire(used, start, elem);
        }
        return false;
    }

    /**
     * Вывод всех файлов. Сначала выведет в правильном порядке имена файлов (их абсолютные пути), затем выведет весь текст из этих файлов (в их правильном порядке).
     * Правильный порядок - это порядок в котором если файл А зависит от файла В, то файл А расположен ниже чем файл В, иначе в лексикографическом порядке имен файлов.
     *
     * @throws IOException если что-то пошло не так (файл открыт другим пользователем или удалили)
     */
    public void printAllFiles() throws IOException {
        if (!isCorrectDirectives()) {
            System.out.println("There is cycle in require sequence");
            System.out.println(cycleFiles);
            return;
        }
        filesList.sort(RequiredFile::compareTo);
        System.out.println("\u001B[32m" + root.getAbsolutePath() + "\u001B[0m");
        StringBuilder result = new StringBuilder();
        for (var file : filesList) {
            System.out.println("\u001B[32m" + (file.getFile().getAbsolutePath()) + " require: " + file.getRequiredString() + "\u001B[0m");
            var fr = new FileReader(file.getFile());
            var reader = new BufferedReader(fr);
            String line = reader.readLine();
            while (line != null) {
                result.append(line).append("\n");
                line = reader.readLine();
            }
        }
        System.out.println(result);
        File outputResult = new File(root.getParentFile().getAbsolutePath() + "/result.txt");
        var fw = new FileWriter(outputResult);
        var writer = new BufferedWriter(fw);
        writer.write(result.toString());
        writer.flush();
        writer.close();
    }

    /**
     * Получение абсолютного пути к корневой папке.
     *
     * @return абсолютный путь до папки
     */
    public String getRoot() {
        return root.getAbsolutePath();
    }

}
