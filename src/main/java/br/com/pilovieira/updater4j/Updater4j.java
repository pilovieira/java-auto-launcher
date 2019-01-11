package br.com.pilovieira.updater4j;

import br.com.pilovieira.updater4j.util.Lang;
import br.com.pilovieira.updater4j.view.AutoLauncherController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Updater4j {

    public static void start(Options options) throws IOException {
        Lang.initialize(options.lang);

        FXMLLoader fxmlLoader = new FXMLLoader(Updater4j.class.getResource("/fxml/auto-launcher.fxml"));
        Parent root = fxmlLoader.load();
        AutoLauncherController controller = fxmlLoader.getController();
        controller.initialize(options);

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setTitle(options.launcherTitle);
        stage.getIcons().add(new Image(options.launcherScreenIcon));
        stage.setOnCloseRequest(event -> controller.cancelAction());
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

}
