package fr.leviathanstudio.engine.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;

/**
 * @author ZeAmateis
 */
public class JsonStorageUtils extends DataStorageUtils {

    static final Gson gson = new GsonBuilder().create();

    public static void compressToFile(Object data, Path outputPath) {
        try {
            FileOutputStream fos = new FileOutputStream(outputPath.toFile());
            fos.write(DataStorageUtils.compress(gson.toJson(data).getBytes()).toByteArray());
            fos.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static <T> T fromCompressedFile(Path inputPath, Type typeOfT) {
        return gson.fromJson(new JsonReader(decompressToReader(inputPath)), typeOfT);
    }
}
