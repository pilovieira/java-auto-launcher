package br.com.pilovieira.updater4j.checksum;

import br.com.pilovieira.updater4j.Util;
import br.com.pilovieira.updater4j.core.FileWorker;

import java.io.File;
import java.util.function.Consumer;

import static br.com.pilovieira.updater4j.Lang.msg;
import static br.com.pilovieira.updater4j.checksum.Checksum.CHECKSUM_FILE_NAME;
import static br.com.pilovieira.updater4j.checksum.Checksum.CHECKSUM_SPLITTER;

public class ChecksumFileGenerator {

    private StringBuilder b;
    private Checksum checksum = new Checksum();
    private FileWorker fileWorker;
    private Consumer<String> log;

    public static void main(String[] args) {
        if (args.length == 0)
            throw new RuntimeException(msg("insertPath"));

        new ChecksumFileGenerator(new FileWorker(), System.out::println).generate(new File(args[0]));
    }

    public ChecksumFileGenerator(FileWorker fileWorker, Consumer<String> log) {
        this.fileWorker = fileWorker;
        this.log = log;
    }

    public void generate(File path) {
        validate(path);
        String checksum = createChecksum(path);
        fileWorker.create(new File(path.getAbsolutePath(), CHECKSUM_FILE_NAME), checksum, log);
    }

    private void validate(File path) {
        if (path == null)
            throw new RuntimeException(msg("insertPath"));
        if (!path.exists() || !path.isDirectory())
            throw new RuntimeException(msg("pathMustBeDirectory"));
    }

    private String createChecksum(File path) {
        b = new StringBuilder();
        Util.scanAll(path, file -> buildChecksum(path, file));
        return b.toString();
    }

    private void buildChecksum(File path, File file) {
        if (CHECKSUM_FILE_NAME.equals(file.getName()))
            return;

        String value = String.format("%s %s %s\n", checksum.buildChecksum(file), CHECKSUM_SPLITTER, buildName(path, file));
        log.accept(value);
        b.append(value);
    }

    private String buildName(File path, File file) {
        return file.getAbsolutePath()
                .replace(path.getAbsolutePath(), "")
                .replace("\\", "/");
    }

}