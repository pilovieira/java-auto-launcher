package br.com.pilovieira.updater4j;

import br.com.pilovieira.updater4j.util.Lang;
import br.com.pilovieira.updater4j.view.Updater4jFrame;

public class Updater4j {

    public static void start(Options options) {
        Lang.initialize(options.lang);
        new Updater4jFrame(options);
    }

}
