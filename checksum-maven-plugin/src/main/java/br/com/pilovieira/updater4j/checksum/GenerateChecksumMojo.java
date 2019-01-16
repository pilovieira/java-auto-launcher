package br.com.pilovieira.updater4j.checksum;

import br.com.pilovieira.updater4j.core.FileWorker;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

@Mojo(name = "generate-checksum")
public class GenerateChecksumMojo extends AbstractMojo {

	@Parameter(property = "generate-checksum.path")
	private String path;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (path == null)
			throw new RuntimeException("Please insert parameter 'path'");

		getLog().info("");
		getLog().info(String.format("Generating checksum for: '%s'", path));
		getLog().info("");

		try {
			new ChecksumFileGenerator(new FileWorker(), this::log).generate(new File(path));
		} catch (RuntimeException ex) {
			throw new MojoFailureException(ex.getMessage());
		} catch (Exception ex) {
			throw new MojoExecutionException("generate-checksum error", ex);
		}

		getLog().info("");
	}

	private void log(String value) {
		getLog().info(value.replace("\n", ""));
	}
}
