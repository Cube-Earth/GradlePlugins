package earth.cube.gradle.plugins.commons.utils.filesystem;

import java.io.File;
import java.io.FileFilter;

public class LocalFileSystem implements IFileSystem {

	@Override
	public boolean exists(String sPath) {
		return new File(sPath).exists();
	}

	@Override
	public File[] listFiles(String sPath, FileFilter filter) {
		return new File(sPath).listFiles(filter);
	}

}
