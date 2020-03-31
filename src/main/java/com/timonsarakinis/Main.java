package com.timonsarakinis;

import com.timonsarakinis.engine.Engine;
import com.timonsarakinis.engine.JackCompilationEngine;
import com.timonsarakinis.tokenizer.JackTokenizer;

import java.nio.file.Path;
import java.util.List;

import static com.timonsarakinis.utils.IOUtils.*;
import static com.timonsarakinis.utils.TokenUtils.*;

public class Main {
    public static void main(String[] args) {
        //List<Path> filePaths = FileReaderWriter.getPaths(args[0]);
        createOutputDirectory();
        String directory = "src/main/resources/";
        List<Path> filePaths = getPaths(directory);
        filePaths.forEach(Main::prepareForTokenization);
    }

    private static void prepareForTokenization(Path path) {
        String fileName = extractFileName(path.getFileName().toString());
        deleteFile(getOutputPath(fileName));
        List<String> lines = readFile(path);
        String tokens = removeNoneTokens(lines);
        compile(splitIntoTokens(tokens), fileName);
    }

    private static void compile(List<String> tokens, String fileName) {
        JackTokenizer tokenizer = new JackTokenizer(tokens);
        Engine engine = new JackCompilationEngine(tokenizer, fileName);
        engine.compile();
        System.out.printf("wrote output successfully to file: %s \n", fileName);
    }
}
