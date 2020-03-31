package com.timonsarakinis.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class IOUtils {
    public static final String DIR_PATH = "src/main/resources/output/";
    public static final int FALSE = -1;
    public static final String FILE_EXTENSION = ".xml";

    public static List<Path> getPaths(String path) {
        List<Path> filePaths = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            filePaths = paths
                    .filter(p -> isPathJackFile(p.getFileName().toString()))
                    .collect(Collectors.toList());
        } catch (IOException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return filePaths;
    }

    private static boolean isPathJackFile(String fileName) {
        return fileName.indexOf('.') != FALSE && fileName.substring(fileName.lastIndexOf('.')).equals(".jack");
    }

    public static List<String> readFile(Path filePath) {
        try {
            BufferedReader reader = Files.newBufferedReader(filePath);
            System.out.printf("Successfully read %s \n", filePath.getFileName());
            return reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static void writeToFile(byte[] line, String fileName) {
        try {
            Files.write(getOutputPath(fileName), line, CREATE, APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Path getOutputPath(String fileName) {
        return Paths.get(DIR_PATH + fileName + FILE_EXTENSION);
    }

    public static void createOutputDirectory() {
        Path path = Paths.get(DIR_PATH);
        try {
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(Path path) {
        if (Files.exists(path)) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String extractFileName(String fileName) {
       return fileName.substring(0, fileName.lastIndexOf('.'));
    }
}
