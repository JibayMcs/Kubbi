package fr.leviathanstudio.engine.resources;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

class TranslationTypeAdapter extends TypeAdapter<TranslationEntry> {
    @Override
    public void write(JsonWriter out, TranslationEntry language) throws IOException {

    }

    @Override
    public TranslationEntry read(JsonReader reader) throws IOException {
        String key = null, value = null;
        reader.beginObject();
        while (reader.hasNext()) {
            JsonToken token = reader.peek();

            if (token.equals(JsonToken.NAME)) {
                key = reader.nextName();
            }

            if (key != null) {
                value = reader.nextString();
            }
        }
        reader.endObject();
        return new TranslationEntry(key, value);
    }
}