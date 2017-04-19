package ru.mirea.ippo.format.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public final class JsonObjectMarshal {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    static final class Property {
        @SerializedName("key")
        String key;
        @SerializedName("value")
        String value;
    }

    private static final class Properties {
        List<Property> properties = new LinkedList<>();
    }

    public static void main(String[] args) throws IOException {
        String json = createJson();
        Files.write(Paths.get("properties.json"), json.getBytes("UTF-8"));
        File file = new File("properties.json");
        file.deleteOnExit();
        Properties parsed = GSON.fromJson(new FileReader(file), Properties.class);
        assert parsed.properties.get(0).key.equals("Key1");
        assert parsed.properties.get(0).value.equals("Value1");
    }

    static String createJson() {
        Property property = new Property();
        property.key = "Key1";
        property.value = "Value1";
        Properties properties = new Properties();
        properties.properties.add(property);

        return GSON.toJson(properties);
    }
}
