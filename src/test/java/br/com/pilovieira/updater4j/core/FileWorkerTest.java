package br.com.pilovieira.updater4j.core;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.function.Consumer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileWorkerTest {

	@Rule public ExpectedException thrown = ExpectedException.none();

	@Mock private File root;
	@Mock private File file;
	@Mock private Consumer<File> scanner;

	@Before
	public void setup() {
		when(root.isDirectory()).thenReturn(true);
		when(root.getAbsolutePath()).thenReturn("/home/pilovieira/");
		when(file.getAbsolutePath()).thenReturn("/home/pilovieira/file.test");
	}

	@Test
	public void delete() {
		when(file.delete()).thenReturn(true);

		FileWorker.delete(file, false);

		verify(file).delete();
	}

	@Test
	public void cryOnDelete() {
		when(file.delete()).thenReturn(false);

		thrown.expect(RuntimeException.class);
		thrown.expectMessage("Failed on delete /home/pilovieira/file.test");

		FileWorker.delete(file, true);
	}

	@Test
	public void ignoreError() {
		when(file.delete()).thenReturn(false);

		FileWorker.delete(file, false);

		verify(file).delete();
	}

	@Test
	public void addExtension() {
		when(file.renameTo(any(File.class))).thenReturn(true);

		FileWorker.addExtension(file, ".new");

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

		FileWorker.addExtension(file, ".new");
	}

	@Test
	public void removeExtension() {
		when(file.renameTo(any(File.class))).thenReturn(true);

		FileWorker.removeExtension(file, ".test");

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

		FileWorker.removeExtension(file, ".test");
	}

	@Test
	public void scanAllFiles() {
		File file2 = Mockito.mock(File.class);
		when(root.listFiles()).thenReturn(new File[]{file, file2});

		FileWorker.scanAll(root, scanner);

		verify(scanner, never()).accept(root);
		verify(scanner).accept(file);
		verify(scanner).accept(file2);
	}

	private String normalizePath(String path) {
		return path.replace("\\", "/");
	}

}
