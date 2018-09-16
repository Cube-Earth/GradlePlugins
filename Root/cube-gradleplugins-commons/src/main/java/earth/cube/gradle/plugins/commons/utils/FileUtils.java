package earth.cube.gradle.plugins.commons.utils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {
	
	public static boolean hasChildren(Path dir) throws IOException {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			return stream.iterator().hasNext();
		}		
	
	}

}
