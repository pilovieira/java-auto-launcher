package br.com.pilovieira.updater4j;

import br.com.pilovieira.updater4j.checksum.ChecksumFileGenerator;
import br.com.pilovieira.updater4j.core.FileWorker;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        Args arguments = new Args(args);

        System.out.println(String.format("[Updater4j] -- Starting with\nchecksum: %s\nchecksumPath: %s\nremoteRepository: %s\ndownloadPath: %s\nlaunchCommand: %s\ngui: %s",
                arguments.checksum, arguments.checksumPath, arguments.remoteRepository, arguments.downloadPath, arguments.launchCommand, arguments.gui));

        if (arguments.checksum)
            new ChecksumFileGenerator(new FileWorker(), System.out::println).generate(new File(arguments.checksumPath));
        else
            new Updater4j()
                    .setRemoteRepositoryUrl(arguments.remoteRepository)
                    .setDownloadPath(arguments.downloadPath)
                    .setLaunchCommand(arguments.launchCommand)
                    .setGui(arguments.gui)
                    .start();
    }

}
