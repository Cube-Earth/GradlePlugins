package earth.cube.gradle.plugins.commons.utils.filesystem;

import java.io.File;
import java.io.FileFilter;

public interface IFileSystem {
	
	boolean exists(String sPath);
	
	File[] listFiles(String sPath, FileFilter filter);
	

}
