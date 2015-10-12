package generator;


import java.nio.file.Path;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class GeneratorTest {

    @Test
    public void generatorTest1() {
        final List<Path> testData = new Generator().createTestData(10, 1, null);

        assertThat(testData.size(), is(1));
    }

    @Test
//    @Ignore("Long running test to see if it was an error that does not occur often")
    public void generatorStressTest() {
        final Generator generator = new Generator();

        for (int i = 0; i < 100; i++) {
            final List<Path> testData = generator.createTestData(10, 1, null);

            assertThat(testData.size(), is(1));
        }
    }

}