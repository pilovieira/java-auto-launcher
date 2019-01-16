package br.com.pilovieira.updater4j;

import java.io.File;
import java.util.function.Consumer;

public class Util {

    public static void scanAll(File file, Consumer<File> consumer) {
        if (!file.isDirectory())
            consumer.accept(file);
        else {
            File[] files = file.listFiles();
            if (files != null)
                for (File child : files)
                    scanAll(child, consumer);
        }
    }

}
