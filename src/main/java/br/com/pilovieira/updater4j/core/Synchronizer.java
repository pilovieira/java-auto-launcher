package br.com.pilovieira.updater4j.core;

import br.com.pilovieira.updater4j.Options;
import br.com.pilovieira.updater4j.Util;
import br.com.pilovieira.updater4j.checksum.Checksum;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static br.com.pilovieira.updater4j.Lang.msg;
import static br.com.pilovieira.updater4j.checksum.Checksum.CHECKSUM_FILE_NAME;
import static br.com.pilovieira.updater4j.checksum.Checksum.CHECKSUM_SPLITTER;
import static br.com.pilovieira.updater4j.core.FileWorker.BACKUP_EXT;
import static br.com.pilovieira.updater4j.core.FileWorker.UPDATE_EXT;

class Synchronizer {

    private FileWorker worker;
    private Options options;
    private final File root;
    private Callback callback;
    private final String remoteRepo;
    private Map<String, String> allRemote;
    private HashMap<String, String> onlyRemote;
    private List<File> toDelete;
    private List<File> toClean;

    public Synchronizer(Options options, Callback callback) {
        this.worker = new FileWorker();
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
        String checksumFile = worker.download(url);
        String[] allChecksums = checksumFile.trim().replace("\r", "").split("\n");

        Map<String, String> checksumMap = new HashMap<>();
        Arrays.stream(allChecksums).forEach(raw -> {
            String[] split = raw.split(CHECKSUM_SPLITTER);
            checksumMap.put(split[1].trim(), split[0].trim());
        });

        return checksumMap;
    }

    private void update(File root) {
        Util.scanAll(root, this::cleanUp);

        Util.scanAll(root, this::process);

        AtomicInteger totalForDownload = new AtomicInteger(onlyRemote.size());

        onlyRemote.keySet().forEach(s -> {
            File file = new File(root, s);
            callback.setMessage(msg("downloading") + " " + s);
            downloadAndValidate(file, s, onlyRemote.get(s));
            callback.setProgress(allRemote.size() - totalForDownload.decrementAndGet(), allRemote.size());
        });

        replaceUpdatedFiles();
    }

    private void cleanUp(File file) {
        String absolutePath = file.getAbsolutePath();
        if (absolutePath.contains(BACKUP_EXT) || absolutePath.contains(UPDATE_EXT))
            worker.delete(file, true);
    }

    private void process(File file) {
        String fileName = buildName(file);

        callback.setMessage(msg("verifiying") + " " + fileName);

        String localChecksum = Checksum.buildChecksum(file);
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

        worker.download(buildUrl(fileName), updatedPath);
        File updatedFile = new File(updatedPath);
        toClean.add(updatedFile);

        String localChecksum = Checksum.buildChecksum(updatedFile);
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
        toDelete.forEach(f -> worker.addExtension(f, BACKUP_EXT));
        toClean.forEach(f -> worker.removeExtension(f, UPDATE_EXT));
        toDelete.forEach(f -> worker.delete(new File(f.getAbsolutePath() + BACKUP_EXT), false));
    }

    private void rollback() {
        toDelete.forEach(f -> worker.removeExtension(f, BACKUP_EXT));
        toClean.forEach(f -> worker.delete(f, true));
    }


    public interface Callback {
        void setMessage(String message);
        void setProgress(long done, long max);
    }

}
