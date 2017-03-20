package ru.mirea.ippo.stdlib.nio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class NioFile {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get(".temporary_file");
        System.out.println("Exists: " + Files.isExecutable(path));
        Files.write(path, "HELLO".getBytes("UTF-8"));
        System.out.println("Delete: " + Files.deleteIfExists(path));
        path = Files.createTempFile("XXX", "PPP");
        System.out.println("FileName: " + path.getFileName().getName(0));
        Files.delete(path);
    }
}
