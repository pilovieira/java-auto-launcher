package br.com.pilovieira.updater4j.checksum;

import br.com.pilovieira.updater4j.util.FileUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static br.com.pilovieira.updater4j.checksum.ChecksumUtil.CHECKSUM_FILE_NAME;
import static br.com.pilovieira.updater4j.checksum.ChecksumUtil.CHECKSUM_SPLITTER;
import static br.com.pilovieira.updater4j.util.Lang.msg;

public class ChecksumGenerator {

    private File root;
    private StringBuilder b;

    public static void main(String[] args) {
        if (args.length == 0)
            throw new RuntimeException(msg("insertRootPath"));

        File root = new File(args[0]);
        ChecksumGenerator generator = new ChecksumGenerator(root);
        String checksum = generator.createChecksum();

        System.out.println(checksum);

        try {
            File file = new File(root, CHECKSUM_FILE_NAME);
            FileWriter fw = new FileWriter(file);
            fw.write(checksum);
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ChecksumGenerator(File root) {
        if (!root.exists() || !root.isDirectory())
            throw new RuntimeException(msg("rootMustBeDirectory"));
        this.root = root;
    }

    private String createChecksum() {
        b = new StringBuilder();
        FileUtil.applyAll(root, this::buildChecksum);
        return b.toString();
    }

    private void buildChecksum(File file) {
        if (!CHECKSUM_FILE_NAME.equals(file.getName()))
            b.append(String.format("%s %s %s\n", buildName(file), CHECKSUM_SPLITTER, ChecksumUtil.buildChecksum(file)));
    }

    private String buildName(File file) {
        return file.getAbsolutePath()
                .replace(root.getAbsolutePath(), "")
                .replace("\\", "/");
    }

}
