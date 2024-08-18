package sh.shh.midi.roland;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlobExtractor {
    private static final Logger logger = LogManager.getLogger(BlobExtractor.class);
    static final Pattern REGEX = Pattern.compile("^(.+\\.(SET|ST\\d|FRS))$", Pattern.CASE_INSENSITIVE);

    public static void extractAll(String jarFileName, String outputDirName) throws IOException {
        logger.debug("Extracting " + jarFileName);
        File outputDir = new File(outputDirName);
        JarFile jarFile = new JarFile(jarFileName);
        jarFile.stream()
                .filter(jarEntry -> REGEX.matcher(jarEntry.getName()).find())
                .forEach(jarEntry -> extract(jarFile, jarEntry.getName(), outputDir));
    }

    static void extract(JarFile jarFile, String jarEntryName, File outputDir) {
        try {
            if (!outputDir.exists() || !outputDir.isDirectory()) {
                throw new NoSuchFileException(outputDir.getPath());
            }
            JarEntry jarEntry = jarFile.getJarEntry(jarEntryName);
            Matcher matcher = REGEX.matcher(Path.of(jarEntryName).normalize().toString());
            matcher.find();
            String extractedFileName = Path.of(matcher.group(1)).normalize().toString();
            File outputFile = new File(outputDir, extractedFileName);
            FileUtils.createParentDirectories(outputFile);
            logger.debug("Extracting " + jarEntry.getName() + " -> " + outputFile.getPath());
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
            FileOutputStream os = new FileOutputStream(outputFile);
            InputStream is = jarFile.getInputStream(jarEntry);
            IOUtils.copy(is, os);
            is.close();
            os.close();
        } catch (NullPointerException e) {
            throw new RuntimeException(new NoSuchFileException(String.format("JAR entry %s not present in %s", jarEntryName, jarFile.getName())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
