package br.com.pilovieira.updater4j.core;

import br.com.pilovieira.updater4j.AutoLauncherOptions;
import br.com.pilovieira.updater4j.checksum.ChecksumUtil;
import br.com.pilovieira.updater4j.util.FileUtil;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static br.com.pilovieira.updater4j.checksum.ChecksumUtil.CHECKSUM_FILE_NAME;
import static br.com.pilovieira.updater4j.checksum.ChecksumUtil.CHECKSUM_SPLITTER;
import static br.com.pilovieira.updater4j.util.FileUtil.BACKUP_EXT;
import static br.com.pilovieira.updater4j.util.FileUtil.UPDATE_EXT;
import static br.com.pilovieira.updater4j.util.Lang.msg;

public class FileUpdater {

    private AutoLauncherOptions options;
    private final File root;
    private Callback callback;
    private final String remoteRepo;
    private Map<String, String> allRemote;
    private HashMap<String, String> onlyRemote;
    private List<File> toDelete;
    private List<File> toClean;

    public FileUpdater(AutoLauncherOptions options, Callback callback) {
        this.options = options;
        this.root = new File(options.downloadPath);
        this.callback = callback;

        if (root.exists() && !root.isDirectory())
            throw new RuntimeException(msg("downloadPathMustBeDirectory"));

        if (!root.exists()) {
            boolean mkdirs = root.mkdirs();
            if (!mkdirs)
                throw new RuntimeException(msg("failedCreatingDirectories"));
        }

        this.remoteRepo = options.remoteRepositoryUrl;
        this.toDelete = new ArrayList<>();
        this.toClean = new ArrayList<>();
    }

    public void load() {
        allRemote = loadRemoteChecksums();
        onlyRemote = new HashMap<>(allRemote);

        try {
            update(root);
        } catch (Exception ex) {
            rollback();
            if (!options.launchWhenFail.get())
                throw ex;
        }
    }

    private Map<String, String> loadRemoteChecksums() {
        String url = buildUrl(CHECKSUM_FILE_NAME);
        String checksumFile = FileUtil.download(url);
        String[] allChecksums = checksumFile.trim().replace("\r", "").split("\n");

        Map<String, String> checksumMap = new HashMap<>();
        Arrays.stream(allChecksums).forEach(raw -> {
            String[] split = raw.split(CHECKSUM_SPLITTER);
            checksumMap.put(split[0].trim(), split[1].trim());
        });

        return checksumMap;
    }

    private void update(File root) {
        updateFileTree(root, this::cleanUp);

        updateFileTree(root, this::process);

        AtomicInteger totalForDownload = new AtomicInteger(onlyRemote.size());

        onlyRemote.keySet().forEach(s -> {
            File file = new File(root, s);
            callback.setMessage(msg("downloading") + " " + s);
            downloadAndValidate(file, s, onlyRemote.get(s));
            callback.setProgress(allRemote.size() - totalForDownload.decrementAndGet(), allRemote.size());
        });

        replaceUpdatedFiles();
    }

    private void updateFileTree(File file, Consumer<File> consumer) {
        if (!file.isDirectory())
            consumer.accept(file);
        else {
            File[] files = file.listFiles();
            if (files != null)
                for (File child : files)
                    updateFileTree(child, consumer);
        }
    }

    private void cleanUp(File file) {
        String absolutePath = file.getAbsolutePath();
        if (absolutePath.contains(BACKUP_EXT) || absolutePath.contains(UPDATE_EXT))
            FileUtil.delete(file, true);
    }

    private void process(File file) {
        String fileName = buildName(file);

        callback.setMessage(msg("verifiying") + " " + fileName);

        String localChecksum = ChecksumUtil.buildChecksum(file);
        String remoteChecksum = allRemote.get(fileName);

        if (remoteChecksum == null) {
            callback.setMessage(msg("deleting") + " " + fileName);
            toDelete.add(file);
        } else if (!remoteChecksum.equals(localChecksum)) {
            callback.setMessage(msg("downloading") + " " + fileName);
            toDelete.add(file);
            downloadAndValidate(file, fileName, remoteChecksum);
        }

        onlyRemote.remove(fileName);
        callback.setProgress(allRemote.size() - onlyRemote.size(), allRemote.size());
    }

    private void downloadAndValidate(File file, String fileName, String remoteChecksum) {
        String updatedPath = file.getAbsolutePath() + UPDATE_EXT;

        FileUtil.download(buildUrl(fileName), updatedPath);
        File updatedFile = new File(updatedPath);
        toClean.add(updatedFile);

        String localChecksum = ChecksumUtil.buildChecksum(updatedFile);
        if (!remoteChecksum.equals(localChecksum))
            throw new RuntimeException(msg("downloadHasFailed"));
    }

    private String buildName(File file) {
        return file.getAbsolutePath().replace(root.getAbsolutePath(), "")
                .replace("\\", "/");
    }

    private String buildUrl(String fileName) {
        return String.format("%s%s%s", remoteRepo, remoteRepo.endsWith("/") ? "" : "/", fileName);
    }

    private void replaceUpdatedFiles() {
        toDelete.forEach(f -> FileUtil.addExt(f, BACKUP_EXT));
        toClean.forEach(f -> FileUtil.removeExt(f, UPDATE_EXT));
        toDelete.forEach(f -> FileUtil.delete(new File(f.getAbsolutePath() + BACKUP_EXT), false));
    }

    private void rollback() {
        toDelete.forEach(f -> FileUtil.removeExt(f, BACKUP_EXT));
        toClean.forEach(f -> FileUtil.delete(f, true));
    }


    public interface Callback {
        void setMessage(String message);
        void setProgress(long done, long max);
    }

}
