package earth.cube.gradle.plugins.commons.gitignoretpl;

import java.io.IOException;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import earth.cube.gradle.plugins.commons.utils.GitRepoFinder;

public class GitIgnoreTplTask extends DefaultTask {
	
	@TaskAction
	public void extend() throws IOException {
		GitIgnoreTpl worker = new GitIgnoreTpl(GitRepoFinder.findGitRepo(getProject().getRootDir()));
		worker.execute();
	}

}
