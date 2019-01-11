package br.com.pilovieira.updater4j.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.function.Consumer;

import static br.com.pilovieira.updater4j.util.Lang.msg;

public class FileUtil {

    public static final String BACKUP_EXT = ".bkp";
    public static final String UPDATE_EXT = ".updated";

    public static String download(String url) {
        try {
            BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
            StringBuilder b = new StringBuilder();
            byte dataBuffer[] = new byte[1];
            while (in.read(dataBuffer, 0, 1) != -1)
                b.append(new String(dataBuffer));
            return b.toString();
        } catch (IOException ex) {
            throw new RuntimeException(msg("fileDownloadFailed"), ex);
        }
    }

    public static void download(String url, String destiny) {
        new File(destiny).getParentFile().mkdirs();

        try {
            ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(url).openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(destiny);
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            fileOutputStream.close();
        } catch (IOException ex) {
            throw new RuntimeException(msg("fileDownloadFailed"), ex);
        }
    }

    public static void delete(File file, boolean cry) {
        boolean ok = file.delete();
        if (cry && !ok)
            throw new RuntimeException(msg("failedOnDelete") + " " + file.getAbsolutePath());
    }

    public static void addExt(File file, String ext) {
        String newPath = file.getAbsolutePath() + ext;
        boolean ok = file.renameTo(new File(newPath));
        if (!ok)
            throw new RuntimeException(msg("failedOnRename") + " " + file.getAbsolutePath());
    }

    public static void removeExt(File file, String ext) {
        String newPath = file.getAbsolutePath().replace(ext, "");
        boolean ok = file.renameTo(new File(newPath));
        if (!ok)
            throw new RuntimeException(msg("failedOnRename") + " " + file.getAbsolutePath());
    }

    public static void applyAll(File file, Consumer<File> consumer) {
        if (!file.isDirectory())
            consumer.accept(file);
        else {
            File[] files = file.listFiles();
            if (files != null)
                for (File child : files)
                    applyAll(child, consumer);
        }
    }

}
