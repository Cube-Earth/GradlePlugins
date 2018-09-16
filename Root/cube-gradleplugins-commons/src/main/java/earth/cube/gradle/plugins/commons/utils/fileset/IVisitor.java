package earth.cube.gradle.plugins.commons.utils.fileset;

import java.io.IOException;
import java.nio.file.Path;

public interface IVisitor {
	
	void visitDirectory(Path dir, boolean bDirsOnly) throws IOException;

	void visitFile(Path file) throws IOException;
}
