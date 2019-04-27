package br.com.pilovieira.updater4j;

import java.io.InputStream;
import java.util.function.Supplier;

public class Updater4j {

    private final Options options;

    public Updater4j() {
        options = new Options();
        options.lang = Lang.English;
        options.logo = getClass().getResourceAsStream("/image/download.png");
        options.canUpdateNow = () -> true;
        options.launchWhenCannotUpdate = () -> false;
        options.launchWhenFail = () -> false;
        options.afterUpdateCallback = () -> {};
    }

    public Updater4j setRemoteRepositoryUrl(String remoteRepositoryUrl) {
        options.remoteRepositoryUrl = remoteRepositoryUrl;
        return this;
    }

    public Updater4j setDownloadPath(String downloadPath) {
        options.downloadPath = downloadPath;
        return this;
    }

    public Updater4j setLaunchCommand(String... launchCommand) {
        options.launchCommand = launchCommand;
        return this;
    }

    public Updater4j setLang(Lang lang) {
        options.lang = lang;
        return this;
    }

    public Updater4j setMessage(String message) {
        options.message = message;
        return this;
    }

    public Updater4j setLogo(InputStream logo) {
        options.logo = logo;
        return this;
    }

    public Updater4j setCanUpdateNow(Supplier<Boolean> supplier) {
        options.canUpdateNow = supplier;
        return this;
    }

    public Updater4j setLaunchWhenCannotUpdate(Supplier<Boolean> supplier) {
        options.launchWhenCannotUpdate = supplier;
        return this;
    }

    public Updater4j setLaunchWhenFail(Supplier<Boolean> supplier) {
        options.launchWhenFail = supplier;
        return this;
    }

    public Updater4j setAfterUpdateCallback(Runnable callback) {
        options.afterUpdateCallback = callback;
        return this;
    }

    public void start() {
       options.validate();
        Lang.initialize(options.lang);
        new Frame(options);
    }

}
