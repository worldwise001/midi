package sh.shh.midi.roland;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlobExtractor {
    private static final Pattern REGEX = Pattern.compile("^*/?([^/.]+\\.(SET|ST\\d|FRS))$", Pattern.CASE_INSENSITIVE);

    public static void extractAll(String jarFileName, String outputDir) throws IOException {
        System.out.println("Extracting " + jarFileName);
        JarFile jarFile = new JarFile(jarFileName);
        jarFile.stream()
                .filter(jarEntry -> REGEX.matcher(jarEntry.getName()).find())
                .forEach(jarEntry -> extract(jarFile, jarEntry, outputDir));
    }

    static void extract(JarFile jarFile, JarEntry jarEntry, String outputDir) {
        try {
            Matcher matcher = REGEX.matcher(jarEntry.getName());
            matcher.find();
            String extractedFileName = matcher.group(1);
            File outputFile = new File(outputDir, extractedFileName);
            System.out.println("Extracting " + jarEntry.getName() + " -> " + outputFile.getPath());
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
            FileOutputStream os = new FileOutputStream(outputFile);
            InputStream is = jarFile.getInputStream(jarEntry);
            IOUtils.copy(is, os);
            is.close();
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
