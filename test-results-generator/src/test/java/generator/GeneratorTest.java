package generator;


import java.nio.file.Path;
import java.util.List;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class GeneratorTest {

    @Test
    public void GeneratorTest1() {
        final List<Path> testData = new Generator().createTestData(1, 1);

        assertThat(testData.size(), is(1));
    }


}