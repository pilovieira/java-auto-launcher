package br.com.pilovieira.updater4j;

import java.io.InputStream;
import java.util.function.Supplier;

public class Updater4j {

    private final Options options;

    public Updater4j(String remoteRepositoryUrl, String downloadPath, String... launchCommand) {
        options = new Options();
        options.remoteRepositoryUrl = remoteRepositoryUrl;
        options.downloadPath = downloadPath;
        options.launchCommand = launchCommand;
        options.lang = Lang.English;
        options.logo = getClass().getResourceAsStream("/image/download.png");
        options.updateConfirmation = () -> true;
        options.launchWhenCannotUpdate = () -> false;
        options.launchWhenFail = () -> false;
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

    public Updater4j setUpdateConfirmation(Supplier<Boolean> supplier) {
        options.updateConfirmation = supplier;
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

    public void start() {
       options.validate();
        Lang.initialize(options.lang);
        new Frame(options);
    }

}
