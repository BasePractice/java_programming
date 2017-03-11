package ru.mirea.ippo.regexp;

import com.google.common.io.CharStreams;
import com.google.common.io.LineProcessor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//https://regex101.com/r/DsakLr/1
public final class WorkMatcher {


    //1. Извлеч все href
    public static void main(String[] args) throws IOException {
        final String content;
        try (Reader reader =
                     new InputStreamReader(
                             WorkMatcher.class.getResourceAsStream("/for_crawler.html"), "UTF-8")) {
            content = CharStreams.toString(reader);
        }

        try (Reader reader = new StringReader(content)) {
            List<String> globalLinks = new ArrayList<>();
            printGlobalLinks(reader, globalLinks);
            reader.reset();
            List<String> lineLinks = new ArrayList<>();
            printLineLinks(reader, lineLinks);
            System.out.println(lineLinks.size() == globalLinks.size());
        }
    }

    private static void printGlobalLinks(Reader reader, List<String> links) throws IOException {
        Pattern pattern = Pattern.compile("^.*href\\s*=\\s*\"(.*?)\".*$", Pattern.MULTILINE);
        String content = CharStreams.toString(reader);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            links.add(matcher.group(1));
        }
    }

    private static void printLineLinks(Reader reader, List<String> links) throws IOException {
        Pattern pattern = Pattern.compile("^.*href\\s*=\\s*\"(.*?)\".*$");
        CharStreams.readLines(reader, new LineProcessor<Void>() {
            @Override
            public boolean processLine(String line) throws IOException {
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    for (int i = 0; i < matcher.groupCount(); ++i) {
                        links.add(matcher.group(i + 1));
                    }
                }
                return true;
            }

            @Override
            public Void getResult() {
                return null;
            }
        });
    }
}
