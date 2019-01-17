package br.com.pilovieira.updater4j;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class OptionsTest {

    @Rule public ExpectedException thrown = ExpectedException.none();

    private Options subject;

    @Before
    public void setup() {
        subject = new Options();
        createRequiredOptions();
    }

    private void createRequiredOptions() {
        subject.remoteRepositoryUrl = "http://pilovieira.com.br/updater4j/";
        subject.downloadPath = "/home/pilo/updater4j/";
        subject.launchCommand = new String[]{"java -jar fakejar.jar"};
        subject.lang = Lang.English;
        subject.canUpdateNow = () -> true;
        subject.launchWhenCannotUpdate = () -> false;
        subject.launchWhenFail = () -> false;
    }

    @Test
    public void requiredOptions() {
        subject.validate();
    }

    @Test
    public void remoteRepositoryNull() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Please configure 'remoteRepository' Updater4j option.");

        subject.remoteRepositoryUrl = null;
        subject.validate();
    }

    @Test
    public void downloadPathNull() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Please configure 'downloadPath' Updater4j option.");

        subject.downloadPath = null;
        subject.validate();
    }

    @Test
    public void launchCommandNull() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Please configure 'launchCommand' Updater4j option.");

        subject.launchCommand = null;
        subject.validate();
    }

    @Test
    public void launchCommandEmpty() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Please configure 'launchCommand' Updater4j option.");

        subject.launchCommand = new String[]{};
        subject.validate();
    }

    @Test
    public void langNull() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Please configure 'lang' Updater4j option.");

        subject.lang = null;
        subject.validate();
    }

    @Test
    public void canUpdateNowNull() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Please configure 'canUpdateNow' Updater4j option.");

        subject.canUpdateNow = null;
        subject.validate();
    }

    @Test
    public void launchWhenCannotUpdateNull() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Please configure 'launchWhenCannotUpdate' Updater4j option.");

        subject.launchWhenCannotUpdate = null;
        subject.validate();
    }

    @Test
    public void launchWhenFailNull() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Please configure 'launchWhenFail' Updater4j option.");

        subject.launchWhenFail = null;
        subject.validate();
    }

}
