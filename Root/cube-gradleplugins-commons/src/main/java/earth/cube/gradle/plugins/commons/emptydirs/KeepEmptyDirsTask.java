package earth.cube.gradle.plugins.commons.emptydirs;

import java.io.IOException;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import earth.cube.gradle.plugins.commons.utils.GitRepoFinder;

public class KeepEmptyDirsTask extends DefaultTask {
	

	@TaskAction
	public int mark() throws IOException {
		KeepEmptyDirs worker = new KeepEmptyDirs(GitRepoFinder.findGitRepo(getProject().getRootDir()));
		if(getProject().hasProperty("probe"))
			worker.enableProbe();
		return worker.mark();
	}

}
