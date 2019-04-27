package br.com.pilovieira.updater4j.ui;

import br.com.pilovieira.updater4j.Options;
import br.com.pilovieira.updater4j.core.Processor;

class ConsoleUpdater implements Processor.Callback, UpdaterUi {

    private final Thread thread;

    public ConsoleUpdater(Options options) {
        thread = new Thread(new Processor(options, this), "Updater4j ConsoleUpdater UpdaterUi");
    }

    @Override
    public void onStart() {
        System.out.println("\n[Updater4j] -- Starting update.\n");
    }

    @Override
    public void onFinish() {
        System.out.println("\n[Updater4j] -- Update finished!\n");
    }

    @Override
    public void onPostLaunch() {
        System.out.println("Command launched!");
    }

    @Override
    public void onFail(Exception ex) {
        System.out.println("Update thread failed!");
        ex.printStackTrace();
    }

    @Override
    public void setStatus(String status) {
        System.out.println(status);
    }

    @Override
    public void setProgress(long done, long max) {}

    @Override
    public void startUpdate() {
        thread.start();
    }
}
