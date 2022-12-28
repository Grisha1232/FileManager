package org.example;

import java.io.File;
import java.util.List;

public class RequiredFile implements Comparable<RequiredFile> {
    private File file;
    private List<File> required;

    public RequiredFile(File file) {
        this.file = file;
        required = null;
    }
    public RequiredFile(File file, List<File> required) {
        this.file = file;
        this.required = required;
    }

    @Override
    public int compareTo(RequiredFile other) {
        if (required.isEmpty() && other.required.isEmpty()) {
            return file.getAbsolutePath().compareToIgnoreCase(other.file.getAbsolutePath());
        } else if (required.isEmpty()) {
            for (var req : other.required) {
                if (req.getAbsolutePath().equals(file.getAbsolutePath())) {
                    return -1;
                }
            }
        } else if (other.required.isEmpty()) {
            for (var req : required) {
                if (req.getAbsolutePath().equals(other.file.getAbsolutePath())) {
                    return 1;
                }
            }
        } else {
            for (var req : required) {
                if (req.getAbsolutePath().equals(other.file.getAbsolutePath())) {
                    return 1;
                }
            }
            for (var req : other.required) {
                if (req.getAbsolutePath().equals(file.getAbsolutePath())) {
                    return -1;
                }
            }
        }
        return file.getAbsolutePath().compareToIgnoreCase(other.file.getAbsolutePath());
    }

    public File getFile() {
        return file;
    }

    public String toStringRequired() {
        StringBuilder result = new StringBuilder();
        for (var file : required) {
            result.append(file.getName()).append(" ");
        }
        return result.toString();
    }

}
