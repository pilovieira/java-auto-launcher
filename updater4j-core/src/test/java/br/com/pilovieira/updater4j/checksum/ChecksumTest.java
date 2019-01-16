package br.com.pilovieira.updater4j.checksum;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChecksumTest {

    private static final String SIMPLE_TEXT_CHECKSUM = "2D0B72DCF5E5D3B04DF6DB7E542A03B2BEF06D7EBFD1F99CFDF5B6FAE6E61EBA2BE6235489242D16541CDC2C898CF33A5C5052FB1BE18AFA49207CDB3D0316C2";

    @Mock private File file;

    private Checksum subject;

    @Before
    public void setup() {
        subject = new Checksum();
    }

    @Test
    public void buildChecksum() throws URISyntaxException {
        when(file.toURI()).thenReturn(getSimpleTest());
        when(file.getAbsolutePath()).thenReturn("/home/pilovieira/checksum/simplefile.txt");

        String generatedChecksum = subject.buildChecksum(file);

        Assert.assertEquals(SIMPLE_TEXT_CHECKSUM, generatedChecksum);
    }

    private URI getSimpleTest() throws URISyntaxException {
        URL resource = getClass().getResource("simpletext.txt");
        if (resource == null)
            resource = getClass().getClassLoader().getResource("simpletext.txt");
        return resource.toURI();
    }
}
