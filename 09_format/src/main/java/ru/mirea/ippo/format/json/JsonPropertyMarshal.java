package ru.mirea.ippo.format.json;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.mirea.ippo.format.Property;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

public final class JsonPropertyMarshal {

    private static String createJson() throws IOException {
        StringWriter out = new StringWriter();
        JsonWriter writer = new JsonWriter(out);
        writer.beginObject();
        writer.name("properties")
                .beginArray()
                    .beginObject()
                        .name("key").value("Key1")
                        .name("value").value("Value1")
                    .endObject()
                    .beginObject()
                        .name("key").value("Key2")
                        .name("value").value("Value2")
                    .endObject()
                .endArray();
        writer.endObject();
        return out.toString();
    }

    public static void main(String[] args) throws IOException {
        List<Property> properties = new LinkedList<>();
        String json = createJson();
        JsonReader reader = new JsonReader(new StringReader(json));
        reader.beginObject();
        while (reader.hasNext()) {
            String objectName = reader.nextName();
            if (objectName.equals("properties")) {
                reader.beginArray();
                while (reader.hasNext()) {
                    Property property = new Property();
                    reader.beginObject();
                    String name = reader.nextName();
                    readProperty(reader, property, name);
                    name = reader.nextName();
                    readProperty(reader, property, name);
                    reader.endObject();
                    properties.add(property);
                }
                reader.endArray();
            }
        }
        reader.endObject();
        assert properties.get(0).getKey().equals("Key1");
        assert properties.get(0).getValue().equals("Value1");
    }

    private static void readProperty(JsonReader reader, Property property, String name) throws IOException {
        if (name.equals("key")) {
            property.setKey(reader.nextString());
        } else if (name.equals("value")) {
            property.setValue(reader.nextString());
        }
    }
}
