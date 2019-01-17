package br.com.pilovieira.updater4j.core;

import br.com.pilovieira.updater4j.Lang;
import br.com.pilovieira.updater4j.Options;
import br.com.pilovieira.updater4j.checksum.Checksum;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SynchronizerTest {

    private static final String REMOTE_CHECKSUM_TWO_FILES =
            "2D0B72DCF5E5D3B04DF6DB7E542A03B2BEF06D7EBFD1F99CFDF5B6FAE6E61EBA2BE6235489242D16541CDC2C898CF33A5C5052FB1BE18AFA49207CDB3D0316C2 --> simpletext.txt\n" +
            "2D0B72DCF5E5D3B04DF6DB7E542A03B2BEF06D7EBFD1F99CFDF5B6FAE6E61EBA2BE6235489242D16541CDC2C898CF33A5C5052FB1BE18AFA49207CDB3D0316C2 --> anothertext.txt\n";

    private static final String REMOTE_CHECKSUM_ONE_FILE =
            "2D0B72DCF5E5D3B04DF6DB7E542A03B2BEF06D7EBFD1F99CFDF5B6FAE6E61EBA2BE6235489242D16541CDC2C898CF33A5C5052FB1BE18AFA49207CDB3D0316C2 --> simpletext.txt\n";

    private static final String SIMPLE_TEXT_CHECKSUM =
            "2D0B72DCF5E5D3B04DF6DB7E542A03B2BEF06D7EBFD1F99CFDF5B6FAE6E61EBA2BE6235489242D16541CDC2C898CF33A5C5052FB1BE18AFA49207CDB3D0316C2";
    private static final String ANOTHER_TEXT_CHECKSUM =
            "2D0B72DCF5E5D3B04DF6DB7E542A03B2BEF06D7EBFD1F99CFDF5B6FAE6E61EBA2BE6235489242D16541CDC2C898CF33A5C5052FB1BE18AFA49207CDB3D0316C2";

    @Rule public ExpectedException thrown = ExpectedException.none();

    @Mock private FileWorker worker;
    @Mock private Checksum checksum;
    @Mock private Synchronizer.Callback callback;
    @Mock private File root;
    @Mock private File simpletext;
    @Mock private File anothertext;
    @Mock private File bkptext;
    @Mock private File updatedtext;

    private FileMatcher simpleTextUpdated;
    private FileMatcher anotherTextUpdated;
    private Options options;

    private Synchronizer subject;

    @Before
    public void setup() {
        simpleTextUpdated = new FileMatcher("/home/pilo/updater4j/simpletext.txt.updated");
        anotherTextUpdated = new FileMatcher("/home/pilo/updater4j/anothertext.txt.updated");

        options = new Options();
        options.remoteRepositoryUrl = "http://pilovieira.com.br/updater4j/";
        options.downloadPath = "/home/pilo/updater4j/";
        options.launchCommand = new String[]{"java -jar fakejar.jar"};
        options.lang = Lang.English;
        options.canUpdateNow = () -> true;
        options.launchWhenCannotUpdate = () -> false;
        options.launchWhenFail = () -> false;

        when(root.exists()).thenReturn(true);
        when(root.isDirectory()).thenReturn(true);
        when(root.getAbsolutePath()).thenReturn("/home/pilo/updater4j/");
        when(root.listFiles()).thenReturn(new File[]{simpletext, anothertext});

        when(simpletext.getAbsolutePath()).thenReturn("/home/pilo/updater4j/simpletext.txt");
        when(anothertext.getAbsolutePath()).thenReturn("/home/pilo/updater4j/anothertext.txt");

        subject = new Synchronizer(worker, checksum, options, root, callback);
    }

    @Test
    public void syncNoChanges() {
        when(worker.download("http://pilovieira.com.br/updater4j/loader-checksum.txt")).thenReturn(REMOTE_CHECKSUM_TWO_FILES);
        when(checksum.buildChecksum(simpletext)).thenReturn(SIMPLE_TEXT_CHECKSUM);
        when(checksum.buildChecksum(anothertext)).thenReturn(ANOTHER_TEXT_CHECKSUM);

        subject.sync();

        InOrder io = Mockito.inOrder(worker, callback, checksum);

        io.verify(worker).download("http://pilovieira.com.br/updater4j/loader-checksum.txt");

        io.verify(callback).setMessage("Verifiying simpletext.txt");
        io.verify(checksum).buildChecksum(simpletext);
        io.verify(callback).setProgress(1, 2);

        io.verify(callback).setMessage("Verifiying anothertext.txt");
        io.verify(checksum).buildChecksum(anothertext);
        io.verify(callback).setProgress(2, 2);

        verify(worker, never()).download(anyString(), anyString());
    }

    @Test
    public void syncClean() {
        when(bkptext.getAbsolutePath()).thenReturn("/home/pilo/updater4j/anyfile.txt.bkp");
        when(updatedtext.getAbsolutePath()).thenReturn("/home/pilo/updater4j/anyfile.txt.updated");

        when(root.listFiles()).thenReturn(new File[]{bkptext, updatedtext, simpletext, anothertext});

        when(worker.download("http://pilovieira.com.br/updater4j/loader-checksum.txt")).thenReturn(REMOTE_CHECKSUM_TWO_FILES);
        when(checksum.buildChecksum(simpletext)).thenReturn(SIMPLE_TEXT_CHECKSUM);
        when(checksum.buildChecksum(anothertext)).thenReturn(ANOTHER_TEXT_CHECKSUM);

        subject.sync();

        InOrder io = Mockito.inOrder(worker, callback, checksum);

        io.verify(worker).download("http://pilovieira.com.br/updater4j/loader-checksum.txt");

        io.verify(worker).delete(bkptext, true);
        io.verify(worker).delete(updatedtext, true);

        io.verify(callback).setMessage("Verifiying simpletext.txt");
        io.verify(checksum).buildChecksum(simpletext);
        io.verify(callback).setProgress(1, 2);

        io.verify(callback).setMessage("Verifiying anothertext.txt");
        io.verify(checksum).buildChecksum(anothertext);
        io.verify(callback).setProgress(2, 2);

        verify(worker, never()).download(anyString(), anyString());
    }

    @Test
    public void rootNotDirectory() {
        when(root.exists()).thenReturn(true);
        when(root.isDirectory()).thenReturn(false);

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Download Path must be a directory!");

        subject.sync();
    }

    @Test
    public void failCreatingRoot() {
        when(root.exists()).thenReturn(false);
        when(root.isDirectory()).thenReturn(false);

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Failed creating directories.");

        subject.sync();
    }

    @Test
    public void deleteLocal() {
        when(worker.download("http://pilovieira.com.br/updater4j/loader-checksum.txt")).thenReturn(REMOTE_CHECKSUM_ONE_FILE);
        when(checksum.buildChecksum(simpletext)).thenReturn(SIMPLE_TEXT_CHECKSUM);
        when(checksum.buildChecksum(anothertext)).thenReturn(ANOTHER_TEXT_CHECKSUM);

        subject.sync();

        InOrder io = Mockito.inOrder(worker, callback, checksum);

        io.verify(worker).download("http://pilovieira.com.br/updater4j/loader-checksum.txt");

        io.verify(callback).setMessage("Verifiying simpletext.txt");
        io.verify(checksum).buildChecksum(simpletext);
        io.verify(callback).setProgress(1, 1);

        io.verify(callback).setMessage("Verifiying anothertext.txt");
        io.verify(checksum).buildChecksum(anothertext);
        io.verify(callback).setMessage("Deleting anothertext.txt");
        io.verify(callback).setProgress(1, 1);

        io.verify(worker).addExtension(anothertext, ".bkp");
        io.verify(worker).delete(argThat(new ArgumentMatcher<File>() {
            @Override
            public boolean matches(Object o) {
                File f = (File) o;
                return normalizePath(f.getAbsolutePath()).endsWith("/home/pilo/updater4j/anothertext.txt.bkp");
            }
        }), eq(false));

        verify(worker, never()).download(anyString(), anyString());
    }

    @Test
    public void updateOutdated() {
        when(worker.download("http://pilovieira.com.br/updater4j/loader-checksum.txt")).thenReturn(REMOTE_CHECKSUM_TWO_FILES);
        when(checksum.buildChecksum(simpletext)).thenReturn("break checksum");
        when(checksum.buildChecksum(anothertext)).thenReturn(ANOTHER_TEXT_CHECKSUM);
        when(checksum.buildChecksum(argThat(simpleTextUpdated))).thenReturn(SIMPLE_TEXT_CHECKSUM);

        subject.sync();

        InOrder io = Mockito.inOrder(worker, callback, checksum);

        io.verify(worker).download("http://pilovieira.com.br/updater4j/loader-checksum.txt");
        io.verify(callback).setMessage("Verifiying simpletext.txt");
        io.verify(checksum).buildChecksum(simpletext);
        io.verify(callback).setMessage("Downloading simpletext.txt");
        io.verify(worker).download("http://pilovieira.com.br/updater4j/simpletext.txt", "/home/pilo/updater4j/simpletext.txt.updated");
        io.verify(checksum).buildChecksum(argThat(simpleTextUpdated));
        io.verify(callback).setProgress(1, 2);

        io.verify(callback).setMessage("Verifiying anothertext.txt");
        io.verify(checksum).buildChecksum(anothertext);
        io.verify(callback).setProgress(2, 2);

        io.verify(worker).addExtension(simpletext, ".bkp");
        io.verify(worker).removeExtension(argThat(simpleTextUpdated), eq(".updated"));
        io.verify(worker).delete(argThat(new FileMatcher("/home/pilo/updater4j/simpletext.txt.bkp")), eq(false));
    }

    @Test
    public void failUpdating_Rollback_Start() {
        options.launchWhenFail = () -> true;

        when(worker.download("http://pilovieira.com.br/updater4j/loader-checksum.txt")).thenReturn(REMOTE_CHECKSUM_TWO_FILES);
        when(checksum.buildChecksum(simpletext)).thenReturn("break checksum");
        when(checksum.buildChecksum(anothertext)).thenReturn(ANOTHER_TEXT_CHECKSUM);
        when(checksum.buildChecksum(argThat(simpleTextUpdated))).thenReturn("download error");

        subject.sync();

        InOrder io = Mockito.inOrder(worker, callback, checksum);

        io.verify(worker).download("http://pilovieira.com.br/updater4j/loader-checksum.txt");
        io.verify(callback).setMessage("Verifiying simpletext.txt");
        io.verify(checksum).buildChecksum(simpletext);
        io.verify(callback).setMessage("Downloading simpletext.txt");
        io.verify(worker).download("http://pilovieira.com.br/updater4j/simpletext.txt", "/home/pilo/updater4j/simpletext.txt.updated");
        io.verify(checksum).buildChecksum(argThat(simpleTextUpdated));

        //rollback
        io.verify(worker).removeExtension(simpletext, ".bkp");
        io.verify(worker).delete(argThat(simpleTextUpdated), eq(true));
    }

    @Test
    public void failUpdating_Rollback_Throw() {
        options.launchWhenFail = () -> false;

        when(worker.download("http://pilovieira.com.br/updater4j/loader-checksum.txt")).thenReturn(REMOTE_CHECKSUM_TWO_FILES);
        when(checksum.buildChecksum(simpletext)).thenReturn("break checksum");
        when(checksum.buildChecksum(argThat(simpleTextUpdated))).thenReturn("download error");

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Download has failed. Try again later.");

        subject.sync();
    }

    @Test
    public void downloadOnlyRemote() {
        when(root.listFiles()).thenReturn(new File[]{simpletext});

        when(worker.download("http://pilovieira.com.br/updater4j/loader-checksum.txt")).thenReturn(REMOTE_CHECKSUM_TWO_FILES);
        when(checksum.buildChecksum(simpletext)).thenReturn(SIMPLE_TEXT_CHECKSUM);
        when(checksum.buildChecksum(argThat(anotherTextUpdated))).thenReturn(ANOTHER_TEXT_CHECKSUM);

        subject.sync();

        InOrder io = Mockito.inOrder(worker, callback, checksum);

        io.verify(worker).download("http://pilovieira.com.br/updater4j/loader-checksum.txt");

        io.verify(callback).setMessage("Verifiying simpletext.txt");
        io.verify(checksum).buildChecksum(simpletext);
        io.verify(callback).setProgress(1, 2);

        io.verify(callback).setMessage("Downloading anothertext.txt");
        io.verify(worker).download(eq("http://pilovieira.com.br/updater4j/anothertext.txt"), Matchers.argThat(new ArgumentMatcher<String>() {
            @Override
            public boolean matches(Object o) {
                return normalizePath(o.toString()).endsWith("/home/pilo/updater4j/anothertext.txt.updated");
            }
        }));
        io.verify(callback).setProgress(2, 2);

        io.verify(worker).removeExtension(argThat(anotherTextUpdated), eq(".updated"));
    }

    private String normalizePath(String path) {
        return path.replace("\\", "/");
    }


    private class FileMatcher extends  ArgumentMatcher<File> {

        private String expected;

        public FileMatcher(String expected) {
            this.expected = expected;
        }

        @Override
        public boolean matches(Object o) {
            File f = (File) o;
            return normalizePath(f.getAbsolutePath()).endsWith(expected);
        }
    }

}
