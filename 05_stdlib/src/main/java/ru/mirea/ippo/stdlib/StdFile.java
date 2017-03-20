package ru.mirea.ippo.stdlib;

import java.io.*;

public final class StdFile {
    public static void main(String[] args) throws IOException {
        File file = new File(".temporary_file");
        System.out.println("Exists: " + file.exists());
        try (OutputStream stream = new FileOutputStream(file)) {
            stream.write("HELLO".getBytes("UTF-8"));
        }
        System.out.println("Delete: " + file.delete());
        file = File.createTempFile("XXX", "PPPP");
        System.out.println("FileName: " + file.getName());
        System.out.println("Delete: " + file.delete());
    }
}
