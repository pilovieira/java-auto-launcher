package br.com.pilovieira.updater4j;

import br.com.pilovieira.updater4j.util.Lang;

import java.io.InputStream;
import java.util.function.Supplier;

public class AutoLauncherOptions {

    public String remoteRepositoryUrl;
    public String downloadPath;
    public String[] launchCommand;

    public Lang lang = Lang.English;
    public String launcherTitle = "";
    public String updateMessage = "";
    public InputStream launcherScreenIcon = getClass().getResourceAsStream("/image/download.png");
    public InputStream launcherLogo = getClass().getResourceAsStream("/image/download.png");
    public Supplier<Boolean> canUpdateNow = () -> true;
    public Supplier<Boolean> launchWhenCannotUpdate = () -> false;
    public Supplier<Boolean> launchWhenFail = () -> false;

    public AutoLauncherOptions(String remoteRepositoryUrl, String downloadPath, String... launchCommand) {
        this.remoteRepositoryUrl = remoteRepositoryUrl;
        this.downloadPath = downloadPath;
        this.launchCommand = launchCommand;
        validate();
    }

    private void validate() {
        if (noe(remoteRepositoryUrl))
            throw new RuntimeException("Please configure 'remoteRepository' AutoLauncher option.");

        if (noe(downloadPath))
            throw new RuntimeException("Please configure 'downloadPath' AutoLauncher option.");

        if (launchCommand.length == 0)
            throw new RuntimeException("Please configure 'launchCommand' AutoLauncher option.");
    }

    private static boolean noe(String value) {
        return value == null || value.isEmpty();
    }




}
