package br.com.pilovieira.updater4j.core;

import br.com.pilovieira.updater4j.Options;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;

@RunWith(MockitoJUnitRunner.class)
public class ProcessorTest {

    @Mock private Processor.Callback callback;
    @Mock private Synchronizer synchronizer;
    @Mock private Launcher launcher;
    private Options options;

    private Processor subject;

    @Before
    public void setup() {
        options = new Options();
        options.updateConfirmation = () -> true;
        options.launchCommand = new String[]{"wget", "http://pilovieira.com.br/checksum/"};

        subject = new Processor(options, callback, synchronizer, launcher);
    }

    @Test
    public void startUpdate() throws IOException {
        subject.run();

        InOrder inOrder = Mockito.inOrder(callback, synchronizer, launcher);

        inOrder.verify(callback).onStart();
        inOrder.verify(synchronizer).sync();
        inOrder.verify(launcher).launch(new String[]{"wget", "http://pilovieira.com.br/checksum/"});
        inOrder.verify(callback).onPostLaunch();
        inOrder.verify(callback, never()).onFail(any(Exception.class));
        inOrder.verify(callback).onFinish();
    }

    @Test
    public void callbackWhenFail() throws IOException {
        RuntimeException ex = new RuntimeException("Sync Failed");
        doThrow(ex).when(synchronizer).sync();

        subject.run();

        InOrder inOrder = Mockito.inOrder(callback, synchronizer, launcher);

        inOrder.verify(callback).onStart();
        inOrder.verify(synchronizer).sync();
        inOrder.verify(launcher, never()).launch(any());
        inOrder.verify(callback, never()).onPostLaunch();
        inOrder.verify(callback).onFail(ex);
        inOrder.verify(callback).onFinish();
    }

    @Test
    public void ignoreCallbackWhenFail() throws IOException {
        RuntimeException ex = new RuntimeException("Sync Failed");
        doThrow(ex).when(synchronizer).sync();

        subject.abort();
        subject.run();

        InOrder inOrder = Mockito.inOrder(callback, synchronizer, launcher);

        inOrder.verify(callback).onStart();
        inOrder.verify(synchronizer).sync();
        inOrder.verify(launcher, never()).launch(any());
        inOrder.verify(callback, never()).onPostLaunch();
        inOrder.verify(callback, never()).onFail(ex);
        inOrder.verify(callback).onFinish();
    }

    @Test
    public void cantUpdateDontLaunch() throws IOException {
        options.updateConfirmation = () -> false;
        options.launchWhenCannotUpdate = () -> false;

        subject.run();

        InOrder inOrder = Mockito.inOrder(callback, synchronizer, launcher);

        inOrder.verify(callback, never()).onStart();
        inOrder.verify(synchronizer, never()).sync();
        inOrder.verify(launcher, never()).launch(any());
        inOrder.verify(callback, never()).onPostLaunch();
        inOrder.verify(callback).onFail(argThat(new ArgumentMatcher<Exception>() {
            @Override
            public boolean matches(Object o) {
                Exception ex = (Exception) o;
                Assert.assertEquals("Update cannot be executed now.", ex.getMessage());
                return true;
            }
        }));
        inOrder.verify(callback).onFinish();
    }

    @Test
    public void cantUpdateButLaunch() throws IOException {
        options.updateConfirmation = () -> false;
        options.launchWhenCannotUpdate = () -> true;

        subject.run();

        InOrder inOrder = Mockito.inOrder(callback, synchronizer, launcher);

        inOrder.verify(callback, never()).onStart();
        inOrder.verify(synchronizer, never()).sync();
        inOrder.verify(launcher).launch(new String[]{"wget", "http://pilovieira.com.br/checksum/"});
        inOrder.verify(callback).onPostLaunch();
        inOrder.verify(callback, never()).onFail(any(Exception.class));
        inOrder.verify(callback).onFinish();
    }
}
