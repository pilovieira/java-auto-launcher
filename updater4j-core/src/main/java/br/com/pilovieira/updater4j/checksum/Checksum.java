package br.com.pilovieira.updater4j.checksum;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static br.com.pilovieira.updater4j.Lang.msg;

public class Checksum {

    public static final String CHECKSUM_FILE_NAME = "loader-checksum.txt";
    public static final String CHECKSUM_SPLITTER = "-->";

    private static final String ALGORITHM = "SHA-512";

    public String buildChecksum(File file) {
        try {
            byte[] fileBytes = Files.readAllBytes(Paths.get(file.toURI()));
            MessageDigest alg = MessageDigest.getInstance(ALGORITHM);
            byte[] digestMessage = alg.digest(fileBytes);

            StringBuilder checksum = new StringBuilder();
            for (byte b : digestMessage)
                checksum.append(String.format("%02x", b & 0xff));

            return checksum.toString().toUpperCase();
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(msg("buildChecksumFailed") + " " + file.getName());
        }
    }

}
