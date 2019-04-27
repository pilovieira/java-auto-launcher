package br.com.pilovieira.updater4j.ui;

import br.com.pilovieira.updater4j.Options;

public interface UpdaterUi {

    static UpdaterUi produce(Options options) {
        return options.gui ? new GuiUpdater(options) : new ConsoleUpdater(options);
    }

    void startUpdate();
}
