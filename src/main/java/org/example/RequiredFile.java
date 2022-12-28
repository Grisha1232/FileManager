package org.example;

import java.awt.image.AreaAveragingScaleFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RequiredFile implements Comparable<RequiredFile> {
    private final File file;
    private final List<RequiredFile> required;

    public RequiredFile(File file) {
        this.file = file;
        required = new ArrayList<>();
    }

    public RequiredFile(File file, List<RequiredFile> required) {
        this.file = file;
        this.required = required;
    }

    /**
     * Функция сравнение элементов
     * @param other the object to be compared.
     * @return отрицательное если должен находится левее в списке. 0 если равны. положительное если должен находится правее в списке.
     */
    @Override
    public int compareTo(RequiredFile other) {
        if (required.isEmpty() && other.required.isEmpty()) {
            return file.getAbsolutePath().compareToIgnoreCase(other.file.getAbsolutePath());
        } else if (required.isEmpty()) {
            for (var req : other.required) {
                if (req.file.getAbsolutePath().equals(file.getAbsolutePath())) {
                    return -1;
                }
            }
        } else if (other.required.isEmpty()) {
            for (var req : required) {
                if (req.file.getAbsolutePath().equals(other.file.getAbsolutePath())) {
                    return 1;
                }
            }
        } else {
            for (var req : required) {
                if (req.file.getAbsolutePath().equals(other.file.getAbsolutePath())) {
                    return 1;
                }
            }
            for (var req : other.required) {
                if (req.file.getAbsolutePath().equals(file.getAbsolutePath())) {
                    return -1;
                }
            }
        }
        return file.getAbsolutePath().compareToIgnoreCase(other.file.getAbsolutePath());
    }

    /**
     * Получение файла
     * @return файл
     */
    public File getFile() {
        return file;
    }

    /**
     * получение зависимостей файла
     * @return зависимости
     */
    public List<RequiredFile> getRequired() {
        return required;
    }

    /**
     * Добавление зависимости файлу
     * @param req
     */
    public void addRequire(RequiredFile req) {
        required.add(req);
    }

    public String getRequiredString() {
        StringBuilder result = new StringBuilder();
        for (var elem : required) {
            result.append(elem.getFile().getName()).append(" ");
        }
        return result.toString();
    }

}
