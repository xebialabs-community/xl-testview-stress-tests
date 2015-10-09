package generator;

import org.databene.benerator.main.XmlCreator;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Generator {

//    public static final URL JUNIT_XSD = Generator.class.getResource("/junit.xsd");
    public static final String JUNIT_XSD = "test-results-generator/src/main/resources/junit.xsd";
    public static final String XML_ROOT_ELEMENT = "testsuite";
    public static final String DEFAULT_FILE_FORMAT = "TEST-{0}.xml";
    public static final String OUTPUT_DIRECTORY_PREFIX = "xltvgenerator";

    /**
     * Creates test results data based on JUnit gradle reports.
     * Returns a list of Path objects which point to temp directories
     * containing the results
     *
     * @param numOutputFiles       Number of output files
     * @param numOutputDirs        Number of output directories
     * @param outputFilenameFormat format used for output files. Can insert  placeholders,
     *                             for example TEST-{0}.xml, will replace {0} with file number
     *                             If null or empty, TEST-{0}.xml will be used as default.
     */
    public static List<Path> createTestData(int numOutputFiles, int numOutputDirs, String outputFilenameFormat) {
        List<Path> outputDirs = new ArrayList<>();

        for (int i=0; i<numOutputDirs; i++) {
            // create output folder
            try {
                Path dir = Files.createTempDirectory(OUTPUT_DIRECTORY_PREFIX);

                if (outputFilenameFormat == null || outputFilenameFormat.isEmpty()) {
                    outputFilenameFormat = DEFAULT_FILE_FORMAT;
                }

                XmlCreator.createXMLFiles(JUNIT_XSD.toString(),
                        XML_ROOT_ELEMENT,
                        dir.toString() + "/" + outputFilenameFormat,
                        numOutputFiles,
                        new String[0]);

                outputDirs.add(dir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return outputDirs;
    }
}
