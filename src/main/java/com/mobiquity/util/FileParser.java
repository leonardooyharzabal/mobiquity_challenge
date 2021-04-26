package com.mobiquity.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileParser {

    //it is reusable for other kind of files to parse
    public static String parseFileIntoString(String filePath) throws IOException {

        Path path = Paths.get(filePath);
        return Files.readString(path);
    }
}
