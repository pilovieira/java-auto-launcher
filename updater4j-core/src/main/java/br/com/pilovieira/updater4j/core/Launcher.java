package br.com.pilovieira.updater4j.core;

import java.io.IOException;

class Launcher {

    public void launch(String[] command) throws IOException {
        Runtime.getRuntime().exec(command);
    }

}
