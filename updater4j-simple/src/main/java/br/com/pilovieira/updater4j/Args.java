package br.com.pilovieira.updater4j;

import java.util.ArrayList;
import java.util.List;

class Args {

	private static final String H = "-h";
	private static final String HELP = "-help";

	private static final String GENERATE_CHECKSUM = "-generate-checksum";

	private static final String RR = "-rr";
	private static final String REMOTE_REPOSITORY_URL = "-remote-repository-url";

	private static final String DP = "-dp";
	private static final String DOWNLOAD_PATH = "-download-path";

	private static final String LC = "-lc";
	private static final String LAUNCH_COMMAND = "-launch-command";

	private static final String GUI = "-gui";

	public boolean gui;
	public boolean checksum;
	public String checksumPath;
	public String remoteRepository;
	public String downloadPath;
	public String launchCommand;

	public Args(String[] args) {
		load(args);
		validate();
	}

	private void load(String[] args) {
		if (args == null)
			return;

		String reading = "";
		for (String arg : args) {
			if (H.equals(arg) || HELP.equals(arg)) {
				Help.help();
				System.exit(0);
			} else if (arg.equals(GUI)) {
				gui = true;
				reading = "";
			} else if (arg.startsWith("-")) {
				reading = arg;
			} else {
				setArg(reading, arg);
				reading = "";
			}
		}
	}

	private void setArg(String tag, String value) {
		switch (tag) {
			case GENERATE_CHECKSUM:
				checksum = true;
				checksumPath = value;
				break;
			case RR:
			case REMOTE_REPOSITORY_URL:
				remoteRepository = value;
				break;
			case DP:
			case DOWNLOAD_PATH:
				downloadPath = value;
				break;
			case LC:
			case LAUNCH_COMMAND:
				launchCommand = value;
				break;
			default:
				break;
		}
	}
	
	private void validate() {
		List<String> args = new ArrayList<>();
		if (checksum) {
			if (checksumPath == null) args.add(GENERATE_CHECKSUM);
		} else {
			if (remoteRepository == null) args.add(REMOTE_REPOSITORY_URL);
			if (downloadPath == null) args.add(DOWNLOAD_PATH);
			if (launchCommand == null) args.add(LAUNCH_COMMAND);
		}

		if (!args.isEmpty())
			help(args.toString());
	}
	
	private void help(String message) {
		Help.help();
		throw new RuntimeException(message);
	}

}
