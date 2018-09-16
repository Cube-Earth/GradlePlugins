package earth.cube.gradle.plugins.commons.utils.fileset;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class MemoryFileSystem {
	
	private FileSystem _fs = Jimfs.newFileSystem(Configuration.unix());
	
	public void addFile(String sPath, String sContent) throws IOException {
		Path path = _fs.getPath(sPath);
		Files.createDirectories(path.getParent());	
		Files.write(path, ImmutableList.of(sContent == null ? "" : sContent), StandardCharsets.UTF_8);
	}
	
	public void addFile(String sPath) throws IOException {
		addFile(sPath, null);
	}

	public void addDirectory(String sPath) throws IOException {
		Path path = _fs.getPath(sPath);
		Files.createDirectories(path);	
	}
	
	public FileSystem get() {
		return _fs;
	}


	
	
}
