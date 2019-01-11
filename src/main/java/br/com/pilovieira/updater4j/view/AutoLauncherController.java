package br.com.pilovieira.updater4j.view;

import br.com.pilovieira.updater4j.AutoLauncherOptions;
import br.com.pilovieira.updater4j.core.FileUpdater;
import br.com.pilovieira.updater4j.core.Processor;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import static br.com.pilovieira.updater4j.util.Lang.msg;

public class AutoLauncherController {

    public Label textInfo;
    public ImageView imageLogo;
    public ProgressBar progress;
    public Label textProcess;
    public Button btnCancel;

    private AutoLauncherOptions options;
    private LoadTask loadTask;
    private Thread animThread;
    private Thread loadThread;

    public void initialize(AutoLauncherOptions options) {
        this.options = options;
        imageLogo.setImage(new Image(this.options.launcherLogo));
    }

    @FXML
    public void initialize() {
        btnCancel.setText(msg("cancel"));
        textInfo.setText(msg("initializingRet"));

        AnimTask animTask = new AnimTask();
        loadTask = new LoadTask();

        progress.setProgress(0);
        progress.progressProperty().unbind();
        progress.progressProperty().bind(loadTask.progressProperty());

        textProcess.textProperty().unbind();
        textProcess.textProperty().bind(loadTask.messageProperty());

        animThread = new Thread(animTask);

        loadThread = new Thread(loadTask);
        loadThread.start();
    }

    public void cancelAction() {
        loadTask.processor.abort();
        animThread.interrupt();
        loadThread.interrupt();
        close();
    }

    private void close() {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }

    private void showAlert(Exception ex) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(msg("failed"));
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        });
    }


    private class LoadTask extends Task<Void> {

        private Processor processor;

        @Override
        protected Void call() {
            createProcessor();
            processor.run();
            return null;
        }

        private void createProcessor() {
            this.processor = new Processor(options, new Processor.Callback() {
                @Override
                public void onStart() {
                    animThread.start();
                }

                @Override
                public void onFinish() {
                    animThread.interrupt();
                }

                @Override
                public void onPostRun() {
                    Platform.runLater(AutoLauncherController.this::close);
                }

                @Override
                public void onFail(Exception ex) {
                    showAlert(ex);

                    Platform.runLater(() -> {
                        textInfo.setText(msg("updateFailed"));
                        btnCancel.setText(msg("close"));
                    });
                }

                @Override
                public FileUpdater.Callback updaterCallback() {
                    return new FileUpdater.Callback() {
                        @Override
                        public void setMessage(String message) {
                            updateMessage(message);
                        }

                        @Override
                        public void setProgress(long done, long max) {
                            updateProgress(done, max);
                        }
                    };
                }
            });
        }
    }


    private class AnimTask extends Task<Void> {
        @Override
        protected Void call() throws Exception {
            String msg = options.updateMessage.isEmpty() ? msg("updating") : options.updateMessage;
            int count = 0;
            while (count < 1000) {
                Platform.runLater(() -> textInfo.setText(msg + "."));
                Thread.sleep(500);
                Platform.runLater(() -> textInfo.setText(msg + ".."));
                Thread.sleep(500);
                Platform.runLater(() -> textInfo.setText(msg + "..."));
                Thread.sleep(500);
            }
            return null;
        }
    }

}
