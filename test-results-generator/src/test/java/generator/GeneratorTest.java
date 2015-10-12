package generator;


import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

public class GeneratorTest {

    private final static Logger LOG = LoggerFactory.getLogger(GeneratorTest.class);
    DocumentBuilder builder = null;

    public GeneratorTest() {
        DocumentBuilderFactory builderFactory =
                DocumentBuilderFactory.newInstance();
        try {
            builder = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
    XPath xPath =  XPathFactory.newInstance().newXPath();

    @Test
    public void generatorTest1() throws IOException, SAXException {
        final List<Path> testData = new Generator().createTestData(10, 1);

        assertThat(testData.size(), is(1));
        System.out.println(testData.get(0).toString());
        try (DirectoryStream<Path> paths = Files.newDirectoryStream(testData.get(0))) {
            for (Path path : paths) {
                Document document = builder.parse(Files.newInputStream(path));

            }

        }


    }

    @Test
    @Ignore("Long running test to see if it was an error that does not occur often")
    public void generatorStressTest() {
        final Generator generator = new Generator();

        for (int i = 0; i < 100; i++) {
            final List<Path> testData = generator.createTestData(10, 1);

            assertThat(testData.size(), is(1));
        }
    }

}