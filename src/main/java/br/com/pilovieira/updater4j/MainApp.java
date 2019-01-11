package br.com.pilovieira.updater4j;

import br.com.pilovieira.updater4j.util.Lang;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        AutoLauncherOptions options = new AutoLauncherOptions(
                "http://pilovieira.com.br/checksum/",
                "C:\\Users\\Pilo\\.pasto\\system",
                "C:\\Windows\\System32\\mspaint.exe");

        options.launcherTitle = "Auto Launcher Test";
        //options.updateMessage = "Updating now. Please wait";
        //options.launchWhenFail = true;
        options.lang = Lang.Portuguese;
        options.canUpdateNow = () -> true;
        options.launchWhenCannotUpdate = () -> false;

        AutoLauncher.start(options);
    }
}
