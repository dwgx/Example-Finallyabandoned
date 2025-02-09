package com.example.utils;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class FileUtils {
    public static byte[] readFile(File file) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            long fileLength = file.length();

            if (fileLength > Integer.MAX_VALUE) {
                throw new IOException("File is too large to fit into an array: " + file.getAbsolutePath());
            }

            byte[] fileBytes = new byte[(int) fileLength];
            int totalBytesRead = 0;
            int bytesRead;

            while ((bytesRead = bis.read(fileBytes, totalBytesRead, fileBytes.length - totalBytesRead)) != -1) {
                totalBytesRead += bytesRead;
                if (totalBytesRead == fileBytes.length) {
                    break;
                }
            }

            if (totalBytesRead != fileBytes.length) {
                throw new IOException("Unable to read entire file: " + file.getAbsolutePath());
            }

            return fileBytes;
        }
    }

    public static void writeFile(byte[] data, Path path) throws IOException {
        File file = path.toFile();
        file.getParentFile().mkdirs();

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
            bos.write(data);
        }
    }

    public static void traverseDirectory(File directory, List<File> fileList) {
        if (directory.exists() && directory.isDirectory()) {
            Optional.ofNullable(directory.listFiles())
                    .ifPresent(files -> {
                        for (File file : files) {
                            if (file.isDirectory()) {
                                traverseDirectory(file, fileList);
                            } else {
                                fileList.add(file);
                            }
                        }
                    });
        }
    }
}
