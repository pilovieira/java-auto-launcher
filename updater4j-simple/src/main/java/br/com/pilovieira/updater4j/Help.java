package br.com.pilovieira.updater4j;

class Help {
	
	public static void help() {
		System.out.println(
				"***************\r\n" +
				"\r\n" +
				"Updater4j\r\n" +
				"Update and launch your projects!\r\n" +
				"\r\n" +
				"***************\r\n" +
				"\r\n" +
				"To generate checksum ->\n" +
				"  -generate-checksum \"directory/path/arg/\"\n" +
				"\n" +
				"***************\n\n" +
				"To launch ->\n" +
				"Required arguments:\r\n" +
				"  -remote-repository-url\t-rr\t\tArtifact repository for your system\n" +
				"  -download-path\t\t\t-dp\t\tPath where the system files will be downloaded\n" +
				"  -launch-command\t\t\t-lc\t\tCommand that will be executed after the artifacts are downloaded\n" +
				"\r\n" +
				"Optional arguments:\r\n" +
				"  -gui\t\t\t\t\t\t\t\tShow download interface\n" +
				"  -help\t\t\t\t\t\t-h\t\tHelp\r\n" +
				"\r\n" +
				"***************\r\n" +
				"\r\n");
	}

}
