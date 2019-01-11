package br.com.pilovieira.updater4j.core;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileWorkerTest {

	@Rule public ExpectedException thrown = ExpectedException.none();

	@Mock private File root;
	@Mock private File file;

	private FileWorker subject;

	@Before
	public void setup() {
		when(root.isDirectory()).thenReturn(true);
		when(root.getAbsolutePath()).thenReturn("/home/pilovieira/");
		when(file.getAbsolutePath()).thenReturn("/home/pilovieira/file.test");

		subject = new FileWorker();
	}

	@Test
	public void delete() {
		when(file.delete()).thenReturn(true);

		subject.delete(file, false);

		verify(file).delete();
	}

	@Test
	public void cryOnDelete() {
		when(file.delete()).thenReturn(false);

		thrown.expect(RuntimeException.class);
		thrown.expectMessage("Failed on delete /home/pilovieira/file.test");

		subject.delete(file, true);
	}

	@Test
	public void ignoreError() {
		when(file.delete()).thenReturn(false);

		subject.delete(file, false);

		verify(file).delete();
	}

	@Test
	public void addExtension() {
		when(file.renameTo(any(File.class))).thenReturn(true);

		subject.addExtension(file, ".new");

		verify(file).renameTo(Matchers.argThat(new ArgumentMatcher<File>() {
			@Override
			public boolean matches(Object o) {
				File newFile = (File) o;
				return normalizePath(newFile.getAbsolutePath()).endsWith("/home/pilovieira/file.test.new");
			}
		}));
	}

	@Test
	public void cryOnAddExtension() {
		when(file.renameTo(any(File.class))).thenReturn(false);

		thrown.expect(RuntimeException.class);
		thrown.expectMessage("Failed on rename /home/pilovieira/file.test");

		subject.addExtension(file, ".new");
	}

	@Test
	public void removeExtension() {
		when(file.renameTo(any(File.class))).thenReturn(true);

		subject.removeExtension(file, ".test");

		verify(file).renameTo(Matchers.argThat(new ArgumentMatcher<File>() {
			@Override
			public boolean matches(Object o) {
				File newFile = (File) o;
				return normalizePath(newFile.getAbsolutePath()).endsWith("/home/pilovieira/file");
			}
		}));
	}

	@Test
	public void cryOnRemoveExtension() {
		when(file.renameTo(any(File.class))).thenReturn(false);

		thrown.expect(RuntimeException.class);
		thrown.expectMessage("Failed on rename /home/pilovieira/file.test");

		subject.removeExtension(file, ".test");
	}

	private String normalizePath(String path) {
		return path.replace("\\", "/");
	}

}
