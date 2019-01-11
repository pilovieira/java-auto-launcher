package br.com.pilovieira.updater4j.checksum;

import br.com.pilovieira.updater4j.util.Lang;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChecksumGeneratorTest {

    private static final String SIMPLE_TEXT_CHECKSUM = "2D0B72DCF5E5D3B04DF6DB7E542A03B2BEF06D7EBFD1F99CFDF5B6FAE6E61EBA2BE6235489242D16541CDC2C898CF33A5C5052FB1BE18AFA49207CDB3D0316C2 --> /simplefile.txt\n";

    @Rule public ExpectedException thrown = ExpectedException.none();

    @Mock private File root;
    @Mock private File file;

    private ChecksumGenerator subject;

    @Before
    public void setup() {
        Lang.initialize(Lang.English);

        when(root.exists()).thenReturn(true);
        when(root.isDirectory()).thenReturn(true);
        when(root.getAbsolutePath()).thenReturn("/home/pilovieira/checksum");

        subject = new ChecksumGenerator(root);
    }

    @Test
    public void rootNotExists() {
        when(root.exists()).thenReturn(false);

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Root must be a directory!");

        new ChecksumGenerator(root);
    }

    @Test
    public void rootNotDirectory() {
        when(root.isDirectory()).thenReturn(false);

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Root must be a directory!");

        new ChecksumGenerator(root);
    }

    @Test
    public void createChecksum() throws URISyntaxException {
        when(root.listFiles()).thenReturn(new File[]{file});
        when(file.toURI()).thenReturn(getSimpleTest());
        when(file.getAbsolutePath()).thenReturn("/home/pilovieira/checksum/simplefile.txt");

        String generatedChecksum = subject.createChecksum();

        Assert.assertEquals(SIMPLE_TEXT_CHECKSUM, generatedChecksum);
    }

    private URI getSimpleTest() throws URISyntaxException {
        URL resource = getClass().getResource("simpletext.txt");
        if (resource == null)
            resource = getClass().getClassLoader().getResource("simpletext.txt");
        return resource.toURI();
    }

}
