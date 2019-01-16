package br.com.pilovieira.updater4j;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UtilTest {

    @Mock private File root;
    @Mock private File file;
    @Mock private Consumer<File> scanner;

    @Before
    public void setup() {
        when(root.isDirectory()).thenReturn(true);
        when(root.getAbsolutePath()).thenReturn("/home/pilovieira/");
        when(file.getAbsolutePath()).thenReturn("/home/pilovieira/file.test");
    }

    @Test
    public void scanAllFiles() {
        File file2 = Mockito.mock(File.class);
        when(root.listFiles()).thenReturn(new File[]{file, file2});

        Util.scanAll(root, scanner);

        verify(scanner, never()).accept(root);
        verify(scanner).accept(file);
        verify(scanner).accept(file2);
    }

}
