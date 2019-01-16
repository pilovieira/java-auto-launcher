package br.com.pilovieira.updater4j.checksum;

import br.com.pilovieira.updater4j.core.FileWorker;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.Consumer;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChecksumFileGeneratorTest {

    private static final String SIMPLE_TEXT_CHECKSUM = "2D0B72DCF5E5D3B04DF6DB7E542A03B2BEF06D7EBFD1F99CFDF5B6FAE6E61EBA2BE6235489242D16541CDC2C898CF33A5C5052FB1BE18AFA49207CDB3D0316C2 --> /simplefile.txt\n";

    @Rule public ExpectedException thrown = ExpectedException.none();

    @Mock private File path;
    @Mock private File file;
    @Mock private File loaderChecksum;
    @Mock private FileWorker fileWorker;
    @Mock private Consumer<String> log;

    private ChecksumFileGenerator subject;

    @Before
    public void setup() {
        when(path.exists()).thenReturn(true);
        when(path.isDirectory()).thenReturn(true);
        when(path.getAbsolutePath()).thenReturn("/home/pilovieira/checksum");
        when(loaderChecksum.getName()).thenReturn("loader-checksum.txt");

        subject = new ChecksumFileGenerator(fileWorker, log);
    }

    @Test
    public void pathNull() {
        when(path.exists()).thenReturn(false);

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Please insert parameter 'path'");

        subject.generate(null);
    }

    @Test
    public void pathNotExists() {
        when(path.exists()).thenReturn(false);

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Path must be a directory!");

        subject.generate(path);
    }

    @Test
    public void pathNotDirectory() {
        when(path.isDirectory()).thenReturn(false);

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Path must be a directory!");

        subject.generate(path);
    }

    @Test
    public void createChecksum() throws URISyntaxException {
        when(path.listFiles()).thenReturn(new File[]{file, loaderChecksum});
        when(file.toURI()).thenReturn(getSimpleTest());
        when(file.getAbsolutePath()).thenReturn("/home/pilovieira/checksum/simplefile.txt");

        subject.generate(path);

        verify(log).accept(SIMPLE_TEXT_CHECKSUM);
        verify(fileWorker).create(Matchers.argThat(new ArgumentMatcher<File>() {
            @Override
            public boolean matches(Object o) {
                File newFile = (File) o;
                return normalizePath(newFile.getAbsolutePath()).endsWith("/home/pilovieira/checksum/loader-checksum.txt");
            }
        }), eq(SIMPLE_TEXT_CHECKSUM), eq(log));
    }

    @Test
    public void main_PathNull() {
        when(path.exists()).thenReturn(false);

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Please insert parameter 'path'");

        ChecksumFileGenerator.main(new String[] {});
    }

    private URI getSimpleTest() throws URISyntaxException {
        URL resource = getClass().getResource("simpletext.txt");
        if (resource == null)
            resource = getClass().getClassLoader().getResource("simpletext.txt");
        return resource.toURI();
    }

    private String normalizePath(String path) {
        return path.replace("\\", "/");
    }

}