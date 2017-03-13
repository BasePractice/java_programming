package ru.mirea.ippo.stream;

import com.google.common.io.ByteStreams;

import java.io.*;
import java.nio.charset.Charset;

public final class XorTransform {

    private static final Transform XOR = ch -> ch ^ 10;

    public static void main(String[] args) throws IOException {
        Charset charset = Charset.forName("UTF-8");
        String content = "HELLO";
        System.out.println("INPUT : " + content);
        InputStream stream = new ByteArrayInputStream(content.getBytes(charset));
        InputStream xStream = new TransformInputStream(XOR, stream);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteStreams.copy(xStream, output);
        byte[] secretContent = output.toByteArray();
        System.out.println("SECRET: " + new String(secretContent, charset));

        output.reset();
        OutputStream os = new TransformOutputStream(XOR, output);
        stream = new ByteArrayInputStream(secretContent);
        ByteStreams.copy(stream, os);
        System.out.println("RESULT: " + new String(output.toByteArray(), charset));
    }
}
