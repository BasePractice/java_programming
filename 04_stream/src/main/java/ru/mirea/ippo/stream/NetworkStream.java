package ru.mirea.ippo.stream;

import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class NetworkStream {
    private static final Pattern PATTERN = Pattern.compile("^.*charset=(.*).*$");
    private static final String DEFAULT_CONTENT_ENCODING = "UTF-8";
    //TODO: http://code.joejag.com/2012/how-to-send-a-raw-http-request-via-java.html

    public static void main(String[] args) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("https://www.google.com/").openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("GET");
        connection.connect();
        try (InputStream stream = connection.getInputStream()) {
            contentStream(connection, stream);
        }
    }

    private static String contentStream(URLConnection connection, InputStream stream) throws IOException {
        String charsetName = DEFAULT_CONTENT_ENCODING;
        Matcher matcher = PATTERN.matcher(connection.getContentType());
        if (matcher.find()) {
            charsetName = matcher.group(1);
        }
        byte[] content = ByteStreams.toByteArray(stream);
        return new String(content, charsetName);
    }
}
