package br.com.pilovieira.updater4j;

import java.io.InputStream;
import java.util.function.Supplier;

public class Options {

    public String remoteRepositoryUrl;
    public String downloadPath;
    public String[] launchCommand;
    public Lang lang;
    public String message;
    public InputStream logo;
    public Supplier<Boolean> canUpdateNow;
    public Supplier<Boolean> launchWhenCannotUpdate;
    public Supplier<Boolean> launchWhenFail;

    public void validate() {
        if (noe(remoteRepositoryUrl))
            fail("remoteRepository");

        if (noe(downloadPath))
            fail("downloadPath");

        if (launchCommand == null || launchCommand.length == 0)
            fail("launchCommand");

        if (lang == null)
            fail("lang");

        if (canUpdateNow == null)
            fail("canUpdateNow");

        if (launchWhenCannotUpdate == null)
            fail("launchWhenCannotUpdate");

        if (launchWhenFail == null)
            fail("launchWhenFail");
    }

    private void fail(String field) {
        throw new RuntimeException(String.format("Please configure '%s' Updater4j option.", field));
    }

    private static boolean noe(String value) {
        return value == null || value.isEmpty();
    }






}
