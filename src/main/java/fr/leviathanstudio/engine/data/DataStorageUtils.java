package fr.leviathanstudio.engine.data;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Path;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * @author ZeAmateis
 */
public class DataStorageUtils {

    public static InputStreamReader decompressToReader(Path inputPath) {
        ByteArrayOutputStream dataOutputStream = null;
        try {
            FileInputStream is = new FileInputStream(inputPath.toFile());
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            dataOutputStream = decompress(buffer.toByteArray());

            return new InputStreamReader(new ByteArrayInputStream(dataOutputStream.toByteArray()));
        } catch (IOException | DataFormatException ex) {
            ex.printStackTrace();
            return null;
        } finally {
            try {
                dataOutputStream.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public static void compressToFile(byte[] data, Path outputPath) {
        try {
            FileOutputStream fos = new FileOutputStream(outputPath.toFile());
            ByteArrayOutputStream dataOutputStream = DataStorageUtils.compress(data);
            fos.write(dataOutputStream.toByteArray());
            fos.flush();
            fos.close();
            dataOutputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void decompressToFile(Path inputPath, Path outputPath) {
        try {
            FileInputStream fileInputStream = new FileInputStream(inputPath.toFile());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = fileInputStream.read(data, 0, data.length)) != -1) {
                byteArrayOutputStream.write(data, 0, nRead);
            }

            FileOutputStream fos2 = new FileOutputStream(outputPath.toFile());
            ByteArrayOutputStream dataOutputStream = decompress(byteArrayOutputStream.toByteArray());
            fos2.write(dataOutputStream.toByteArray());
            fos2.flush();
            fos2.close();
            dataOutputStream.close();
            fileInputStream.close();
        } catch (IOException | DataFormatException ex) {
            ex.printStackTrace();
        }
    }

    public static byte[] compress(ByteArrayOutputStream streamIn) {
        return compress(streamIn);
    }

    public static ByteArrayOutputStream compress(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        deflater.finish();
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer); // returns the generated code... index
            outputStream.write(buffer, 0, count);
        }
        System.out.println("Uncompressed File Size: " + FileUtils.byteCountToDisplaySize(data.length));
        System.out.println("Compressed File Size: " + FileUtils.byteCountToDisplaySize(outputStream.toByteArray().length));
        return outputStream;
    }

    public static byte[] decompress(ByteArrayOutputStream streamIn) throws DataFormatException {
        return decompress(streamIn.toByteArray()).toByteArray();
    }

    public static ByteArrayOutputStream decompress(byte[] data) throws DataFormatException {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        return outputStream;
    }
}
