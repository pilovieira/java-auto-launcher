package br.com.pilovieira.updater4j.core;

import br.com.pilovieira.updater4j.Options;

import static br.com.pilovieira.updater4j.Lang.msg;

public class Updater implements Runnable {

    private Options options;
    private Callback callback;
    private boolean aborted;

    public Updater(Options options, Callback callback) {
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
        if (options.updateConfirmation.get()) {
            callback.onStart();
            new Synchronizer(options, new Synchronizer.Callback() {
                @Override
                public void setMessage(String message) {
                    callback.setStatus(message);
                }

                @Override
                public void setProgress(long done, long max) {
                    callback.setProgress(done, max);
                }
            }).load();
        } else if (!options.launchWhenCannotUpdate.get())
            throw new RuntimeException(msg("updateCannotBeExecutedNow"));
    }


    public interface Callback {
        void onStart();
        void onFinish();
        void onPostRun();
        void onFail(Exception ex);
        void setStatus(String status);
        void setProgress(long done, long max);
    }

}
