package br.com.pilovieira.updater4j.core;

import br.com.pilovieira.updater4j.Options;

import static br.com.pilovieira.updater4j.Lang.msg;

public class Processor implements Runnable {

    private Options options;
    private Callback callback;
    private Synchronizer synchronizer;
    private Launcher launcher;
    private boolean aborted;

    public Processor(Options options, Callback callback) {
        this(options, callback,
                new Synchronizer(options, new Synchronizer.Callback() {
                    @Override
                    public void setMessage(String message) {
                        callback.setStatus(message);
                    }

                    @Override
                    public void setProgress(long done, long max) {
                        callback.setProgress(done, max);
                    }
                }),
                new Launcher());
    }

    Processor(Options options, Callback callback, Synchronizer synchronizer, Launcher launcher) {
        this.options = options;
        this.callback = callback;
        this.synchronizer = synchronizer;
        this.launcher = launcher;
    }

    public void abort() {
        aborted = true;
    }

    @Override
    public void run() {
        try {
            update();
            launcher.launch(options.launchCommand);
            callback.onPostLaunch();
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
        if (options.updateConfirmation.get()) {
            callback.onStart();
            synchronizer.load();
        } else if (!options.launchWhenCannotUpdate.get())
            throw new RuntimeException(msg("updateCannotBeExecutedNow"));
    }


    public interface Callback {
        void onStart();
        void onFinish();
        void onPostLaunch();
        void onFail(Exception ex);
        void setStatus(String status);
        void setProgress(long done, long max);
    }

}
