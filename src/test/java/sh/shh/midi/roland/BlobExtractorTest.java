package sh.shh.midi.roland;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BlobExtractorTest {
    private static final File TEST_OUTPUT_DIR = new File("src/test/resources/blob-extracter-test-output");
    private static JarFile JAR_FILE;

    @BeforeAll
    public static void init() throws IOException {
        if (TEST_OUTPUT_DIR.exists()) {
            FileUtils.deleteDirectory(TEST_OUTPUT_DIR);
        }
        if (!TEST_OUTPUT_DIR.mkdir()) {
            throw new IOException("Could not create output directory");
        }
        JAR_FILE = new JarFile("src/test/resources/test.jar");
    }

    @AfterAll
    public static void cleanup() throws IOException {
        FileUtils.deleteDirectory(TEST_OUTPUT_DIR);
    }

    @AfterEach
    public void tearDown() throws IOException {
        FileUtils.cleanDirectory(TEST_OUTPUT_DIR);
    }

    @Nested
    @DisplayName("extract()")
    class TestExtract {

        @Test
        @DisplayName("success")
        public void testExtractSuccess() {
            String testFileName = "test.frs";
            BlobExtractor.extract(JAR_FILE, testFileName, TEST_OUTPUT_DIR);
            assertTrue(new File(TEST_OUTPUT_DIR, testFileName).exists());
            assertFalse(new File(TEST_OUTPUT_DIR, "test.ST8").exists());
        }

        @Test
        @DisplayName("invalid jar entry filename")
        public void testExtractInvalidFile() {
            String testFileName = "test1.frs";
            Throwable throwable = assertThrowsExactly(RuntimeException.class,
                    () -> BlobExtractor.extract(JAR_FILE, testFileName, TEST_OUTPUT_DIR));
            assertInstanceOf(NoSuchFileException.class, throwable.getCause());
            assertEquals("JAR entry test1.frs not present in src/test/resources/test.jar", throwable.getCause().getMessage());
        }

        @Test
        @DisplayName("invalid output directory")
        public void testExtractInvalidOutputDir() {
            String testFileName = "test.frs";
            Throwable throwable = assertThrowsExactly(RuntimeException.class,
                    () -> BlobExtractor.extract(JAR_FILE, testFileName, new File(TEST_OUTPUT_DIR, "asdlkajdakjd")));
            assertInstanceOf(NoSuchFileException.class, throwable.getCause());
            assertEquals(TEST_OUTPUT_DIR + File.separator + "asdlkajdakjd", throwable.getCause().getMessage());
        }

        @Test
        @DisplayName("invalid jar file")
        public void testExtractInvalidJarFile() {
            String testFileName = "test.frs";
            assertThrowsExactly(NoSuchFileException.class,
                    () -> BlobExtractor.extract(new JarFile("asdlkajdakjd.jar"), testFileName, TEST_OUTPUT_DIR),
                    "asdlkajdakjd.jar");

        }
    }

    @Nested
    @DisplayName("extractAll()")
    class TestExtractAll {

        @Test
        @DisplayName("success - final extracted set != jar entry set")
        public void testExtractAllSuccessFinalSetNotJarEntrySet() throws IOException {
            BlobExtractor.extractAll(JAR_FILE.getName(), TEST_OUTPUT_DIR.getPath());
            Set<String> jarEntrySet = JAR_FILE.stream()
                    .map(ZipEntry::getName)
                    .collect(Collectors.toUnmodifiableSet());
            Set<String> outputDirSet = FileUtils.listFiles(TEST_OUTPUT_DIR, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)
                    .stream()
                    .map(File::getPath)
                    .map(e -> e.replace(TEST_OUTPUT_DIR.getPath()+ File.separator, ""))
                    .collect(Collectors.toUnmodifiableSet());
            assertThat(jarEntrySet, hasItems(outputDirSet.stream().findFirst().orElseThrow()));
            assertNotEquals(jarEntrySet, outputDirSet);
        }

        @Test
        @DisplayName("success - final extracted set == filtered jar entry set")
        public void testExtractAllSuccessJarFinalSetIsFilteredJarEntrySet() throws IOException {
            BlobExtractor.extractAll(JAR_FILE.getName(), TEST_OUTPUT_DIR.getPath());
            Set<String> filteredJarEntrySet = JAR_FILE.stream()
                    .map(ZipEntry::getName)
                    .filter(e -> BlobExtractor.REGEX.matcher(e).find())
                    .collect(Collectors.toUnmodifiableSet());
            Set<String> outputDirSet = FileUtils.listFiles(TEST_OUTPUT_DIR, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)
                    .stream()
                    .map(File::getPath)
                    .map(e -> e.replace(TEST_OUTPUT_DIR.getPath()+ File.separator, ""))
                    .collect(Collectors.toUnmodifiableSet());
            assertEquals(filteredJarEntrySet, outputDirSet);
        }

        @Test
        @DisplayName("invalid jar file")
        public void testExtractAllInvalidJarFile() {
            assertThrowsExactly(NoSuchFileException.class,
                    () -> BlobExtractor.extractAll("foobar.jar", TEST_OUTPUT_DIR.getPath()),
                    "foobar.jar");
        }

        @Test
        @DisplayName("invalid output dir")
        public void testExtractAllInvalidOutputDir() {
            Throwable throwable = assertThrowsExactly(RuntimeException.class,
                    () -> BlobExtractor.extractAll(JAR_FILE.getName(), new File(TEST_OUTPUT_DIR, "asdlkajdakjd").getPath()));
            assertInstanceOf(NoSuchFileException.class, throwable.getCause());
            assertEquals(TEST_OUTPUT_DIR + File.separator + "asdlkajdakjd", throwable.getCause().getMessage());
        }
    }
}
