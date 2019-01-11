package br.com.pilovieira.updater4j.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import static br.com.pilovieira.updater4j.Lang.msg;

class FileWorker {

    public static final String BACKUP_EXT = ".bkp";
    public static final String UPDATE_EXT = ".updated";

    public String download(String url) {
        try {
            BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
            StringBuilder b = new StringBuilder();
            byte dataBuffer[] = new byte[1];
            while (in.read(dataBuffer, 0, 1) != -1)
                b.append(new String(dataBuffer));
            in.close();
            return b.toString();
        } catch (IOException ex) {
            throw new RuntimeException(msg("fileDownloadFailed"), ex);
        }
    }

    public void download(String url, String destiny) {
        new File(destiny).getParentFile().mkdirs();

        try {
            ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(url).openStream());
            FileOutputStream os = new FileOutputStream(destiny);
            os.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            os.close();
        } catch (IOException ex) {
            throw new RuntimeException(msg("fileDownloadFailed"), ex);
        }
    }

    public void delete(File file, boolean cry) {
        boolean ok = file.delete();
        if (cry && !ok)
            throw new RuntimeException(msg("failedOnDelete") + " " + file.getAbsolutePath());
    }

    public void addExtension(File file, String ext) {
        String newPath = file.getAbsolutePath() + ext;
        boolean ok = file.renameTo(new File(newPath));
        if (!ok)
            throw new RuntimeException(msg("failedOnRename") + " " + file.getAbsolutePath());
    }

    public void removeExtension(File file, String ext) {
        String newPath = file.getAbsolutePath().replace(ext, "");
        boolean ok = file.renameTo(new File(newPath));
        if (!ok)
            throw new RuntimeException(msg("failedOnRename") + " " + file.getAbsolutePath());
    }

}
