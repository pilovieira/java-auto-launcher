package br.com.pilovieira.updater4j.core;

import br.com.pilovieira.updater4j.AutoLauncherOptions;

import static br.com.pilovieira.updater4j.util.Lang.msg;

public class Processor implements Runnable {

    private AutoLauncherOptions options;
    private Callback callback;
    private boolean aborted;

    public Processor(AutoLauncherOptions options, Callback callback) {
        this.options = options;
        this.callback = callback;
    }

    public void abort() {
        aborted = true;
    }

    @Override
    public void run() {
        try {
            update();
            Runtime.getRuntime().exec(options.launchCommand);
            callback.onPostRun();
        } catch (Exception ex) {
            if (aborted)
                return;

            callback.onFail(ex);
            abort();
        } finally {
            callback.onFinish();
        }
    }

    private void update() {
        if (options.canUpdateNow.get()) {
            callback.onStart();
            new FileUpdater(options, callback.updaterCallback()).load();
        } else if (!options.launchWhenCannotUpdate.get())
            throw new RuntimeException(msg("updateCannotBeExecutedNow"));
    }


    public interface Callback {
        void onStart();
        void onFinish();
        void onPostRun();
        void onFail(Exception ex);
        FileUpdater.Callback updaterCallback();
    }

}
